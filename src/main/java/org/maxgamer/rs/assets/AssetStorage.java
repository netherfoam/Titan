package org.maxgamer.rs.assets;

import org.maxgamer.rs.assets.codec.RSCompression;
import org.maxgamer.rs.assets.codec.asset.*;
import org.maxgamer.rs.assets.protocol.AssetProtocol;
import org.maxgamer.rs.util.Assert;
import org.maxgamer.rs.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * AssetStorage class which manages a group of data tables and the master index.
 *
 * @author netherfoam
 */
public class AssetStorage {
    /**
     * Construct a new AssetStorage, from scratch. This creates a new data file and master index file.
     * @param folder the folder to create the storage in
     * @return the created storage
     * @throws IOException if the files can't be created
     */
    public static AssetStorage create(File folder) throws IOException {
        forceCreate(new File(folder, "main_file_cache.idx255"));
        forceCreate(new File(folder, "main_file_cache.dat2"));

        return new AssetStorage(folder);
    }

    /**
     * Create the given file or raise an exception if a new file can't be created
     * @param file the file to create
     * @throws IOException if the file can't be created (eg, already exists, or no permission)
     */
    protected static void forceCreate(File file) throws IOException {
        if(!file.createNewFile()) throw new IllegalStateException("File " + file.getAbsolutePath() + " already exists!");
    }

    /**
     * The folder that this cache belongs in
     */
    private File folder;

    /**
     * main_file_cache.dat2
     */
    private RandomAccessFile dataFile;

    /**
     * Data table for main_file_cache.idx255
     */
    private DataTable masterTable;

    /**
     * Decoded files from the master index file (main_file_cache.idx255)
     */
    private IndexTable[] indices;

    /**
     * DataTables running off of each index (0-254) main_file_cache.idx(XXX)
     */
    private DataTable[] tables;

    /**
     * The XTEA keys used in this cache
     */
    private XTEAStore xteas;

    private List<RandomAccessFile> fileHandles = new LinkedList<>();

    private AssetProtocol protocol;

    /**
     * Constructs a new AssetStorage using the given folder
     * @param folder the folder
     * @throws IOException if the master index or data table don't exist
     * @throws FileNotFoundException if one of the index files is missing
     */
    public AssetStorage(File folder) throws IOException {
        Assert.isTrue(folder.exists(), "Folder must exist");

        this.folder = folder;
        this.protocol = new AssetProtocol(this);

        load();
    }

    /**
     * Read the master index, so that we have {@link DataTable}'s for accessing our data.
     * @throws IOException if the master index or data table don't exist
     * @throws FileNotFoundException if one of the index files is missing
     */
    protected void load() throws IOException {
        // We only initialize these to be 255, so that we don't accidentally expose the master index
        // to the regular API.
        indices = new IndexTable[255];
        tables = new DataTable[255];

        // The master index file, containing information about each index: the format, version, flags, as well as
        // the Djb2 name hash, crc and whrilpool checksums, version, and subfiles.
        RandomAccessFile masterIndexFile = new RandomAccessFile(new File(folder, "main_file_cache.idx255"), "rw");
        fileHandles.add(masterIndexFile);

        // The main 100mb+ cache file with raw data
        dataFile = new RandomAccessFile(new File(folder, "main_file_cache.dat2"), "rw");

        int size = (int) (masterIndexFile.length() / DataTable.INDEX_BLOCK_LEN);
        masterTable = new PatchableDataTable(255, masterIndexFile.getChannel(), dataFile.getChannel());

        // Each file in the master index should correspond to a physical .idx file
        for(int i = 0; i < size; i++) {
            Asset asset;
            try {
                // We fetch the asset, because we need the decompressed version to decode
                asset = new Asset(null, masterTable.read(i));
            } catch (FileNotFoundException e) {
                // Some idx files don't exist, that's okay.
                continue;
            }

            // Open the cache file. This may raise a FileNotFoundException if the file doesn't exist - in that case,
            // we would have a corrupt cache.
            RandomAccessFile index = new RandomAccessFile(new File(folder, "main_file_cache.idx" + i), "rw");
            fileHandles.add(index);

            // Store the properties for this index
            indices[i] = new IndexTable(i, asset.getCompression(), asset.getPayload());
            tables[i] = new PatchableDataTable(i, index.getChannel(), dataFile.getChannel());
        }

        File xteaFile = new File(this.folder, "xteas.xstore2");
        xteas = new XTEAStore(xteaFile);
        if(xteaFile.exists()) {
            xteas.load();
        }
    }

    /**
     * Closes this asset storage so that it may not be used again, but releases any resources used.
     */
    public void close() throws IOException {
        for(RandomAccessFile index : this.fileHandles) {
            IOUtils.closeQuietly(index);
        }
    }

    /**
     * Fetch underlying data table for the given index. This allows raw access to ByteBuffer contents of files. These files
     * can be read without XTEA keys, but are compressed (and can't be decompressed without XTEA keys).
     *
     * @param idx the index to fetch the {@link DataTable} for
     * @return the {@link DataTable}
     * @throws FileNotFoundException if the {@link DataTable} does not exist
     */
    public DataTable getTable(int idx) throws FileNotFoundException {
        Assert.isPositive(idx, "Index number must be positive");
        DataTable t = tables[idx];

        if(t == null) throw new FileNotFoundException("No such data table: " + idx);

        return t;
    }

    /**
     * Read an asset from the given index and file. The asset will be decoded, ready to be read by a codec.
     *
     * @param idx the index to read from
     * @param file the file number to read
     * @return the decoded asset
     * @throws IOException if the asset can't be read (such as not found, invalid xtea)
     */
    public Asset read(int idx, int file) throws IOException {
        Assert.isPositive(idx, "Index number must be positive");
        Assert.isPositive(file, "FileId must be positive");

        DataTable t = getTable(idx);

        ByteBuffer raw = t.read(file);
        XTEAKey key = xteas.getKey(idx, file);

        return new Asset(key, raw);
    }

    /**
     * Read an asset from the given index with the given hash. The asset will be decoded, and ready to be read by
     * a codec.
     *
     * @param idx the index
     * @param djb2 the hash name for the identifier
     * @return the asset
     * @throws IOException if the file can't be read (not found, invalid xtea)
     */
    public Asset read(int idx, String djb2) throws IOException {
        Assert.isPositive(idx, "Index number must be positive");
        int file = indices[idx].getFile(djb2);

        return read(idx, file);
    }

    /**
     * Read a {@link MultiAsset} from the given index with the given file id.
     *
     * @param idx the index
     * @param file the file number to read
     * @return the multi asset
     * @throws IOException if the file can't be read (not found, invalid xtea)
     */
    public MultiAsset archive(int idx, int file) throws IOException {
        Asset asset = read(idx, file);

        return new MultiAsset(properties(idx, file), asset.getPayload());
    }

    /**
     * Get the {@link IndexTable} for the given index id
     *
     * @param idx the index id
     * @return the index table
     * @throws FileNotFoundException if the index table is not found
     */
    public IndexTable getIndex(int idx) throws FileNotFoundException {
        Assert.isPositive(idx, "Index number must be positive");
        IndexTable table = indices[idx];

        if(table == null) throw new FileNotFoundException("no such index: " + idx);

        return table;
    }

    /**
     * Fetch the properties for the given asset by id. May be null if the file doesn't exist.
     * @param idx the idx
     * @param file the file
     * @return the properties
     */
    public AssetReference properties(int idx, int file) {
        Assert.isPositive(idx, "Index number must be positive");
        Assert.isPositive(file, "FileId must be positive");

        IndexTable apc = indices[idx];
        if(apc == null) return null;

        return apc.getReferences().get(file);
    }

    /**
     * Retrieve an AssetWriter for this storage, that will write to the given index. This can be used
     * to create an entirely new index, or append / overwrite an old one. AssetWriters must have their
     * {@link AssetWriter#commit()} method invoked in order to write changes to disk.
     *
     * @param idx the index to write to
     * @return the asset writer
     * @throws IOException if an IO error occurs
     */
    public AssetWriter writer(int idx) throws IOException {
        Assert.isPositive(idx, "Index number must be positive");

        IndexTable index = indices[idx];
        DataTable data = tables[idx];

        if(data == null) {
            // There's no data table for this index yet. We create it. Note that this does perform disk changes
            // before calling commit(), but generally speaking this should be fine. At worst, we end up with an
            // empty index for next time.

            File indexFile = new File(folder, "main_file_cache.idx" + idx);
            if(!indexFile.createNewFile() && !indexFile.exists()) throw new IOException("Can't create " + indexFile.getName());

            RandomAccessFile rafIndex = new RandomAccessFile(indexFile, "rw");
            data = new PatchableDataTable(idx, rafIndex.getChannel(), dataFile.getChannel());
            fileHandles.add(rafIndex);
            tables[idx] = data;
        }

        if(index == null) {
            // We have no index information (usually will be true, if data is also missing). So we create a new,
            // empty, index table, and add it.
            index = new IndexTable(idx, RSCompression.GZIP, 1);
            indices[idx] = index;
        }

        return new AssetWriter(index, masterTable, data, xteas);
    }

    /**
     * The number of indices defined in this table. Not all of these have to be filled (some indices can be skipped).
     * @return the number of indices
     * @throws IOException if the master file can't be read
     */
    public int size() throws IOException {
        return masterTable.size();
    }

    /**
     * The master table, containing pointers to each file describing the contents of each index
     * @return the master table
     */
    public DataTable getMasterTable() {
        return masterTable;
    }

    /**
     * The protocol handler for serving network requests
     * @return the protocol handler
     */
    public AssetProtocol getProtocol() {
        return protocol;
    }

    public XTEAStore getXTEAs() {
        return xteas;
    }
}
