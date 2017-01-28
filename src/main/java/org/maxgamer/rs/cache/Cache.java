package org.maxgamer.rs.cache;

import net.openrs.cache.ChecksumTable;
import net.openrs.util.ByteBufferUtils;
import net.openrs.util.crypto.Whirlpool;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class Cache {
    /**
     * A flag which indicates this is a priority response to a client's priority
     * cache file request.
     */
    private static final byte PRIORITY_FLAG = (byte) 0x80;
    /**
     * The XTEA keys we use to decrypt the encrypted parts of this cache
     */
    private XTEAStore xteas;

    /**
     * The primary file we're reading data from
     */
    private RandomAccessFile data;
    /**
     * The index tables from the cache. Null if not found/valid. Does not
     * include index 255
     */
    private RandomAccessFile[] indexes;

    private FileTable metaFiles;
    /**
     * The filetables we're using to store our data. These cache read files.
     */
    private FileTable[] tables;

    /**
     * The filetables we're using to store our metadata. This includes size,
     * children, location, etc. These are lightweight.
     */
    private ReferenceTable[] refs;

    private ChecksumTable checksum;
    private RandomAccessFile index;

    /**
     * A hashmap of (IDX << 24) | (FileID) to archive. This is a cache used by
     * the getArchive() method.
     */
    private HashMap<Integer, Archive> archives = new HashMap<>();

    private HashMap<Integer, ByteBuffer> raws = new HashMap<>();

    /**
     * Constructs a new cache
     */
    public Cache() {

    }

    /**
     * Initializes a new cache. This removes any encrypted files from the cache which can't be read
     *
     * @return a newly initialized cache
     */
    public static Cache init() throws IOException {
        Log.debug("Loading Cache...");
        Cache cache = new Cache();
        cache.load(new File("cache"));

        //We store a file as data/cache.yml, this file contains information on files which we should
        //delete from our cache (Encrypted maps). This is done to avoid sending the player maps that
        //we do not have an XTEA key for.
        FileConfig cacheCfg = new FileConfig(new File("data", "cache.yml"));
        cacheCfg.reload();
        //The reference table as a ByteBuffer.
        CacheFile f = cache.getFile(255, IDX.LANDSCAPES);
        //The decoded reference table
        ReferenceTable r = cache.getReferenceTable(IDX.LANDSCAPES);

        //Now we figure out if our files have changed
        File main = new File("cache", "main_file_cache.dat2");
        File xtea = cache.getXTEA().getFile();
        //Quick way to check if the file has changed. This does not check the idx files, which may cause issues, but the .dat file and the xtea file are
        //key to allowing/disallowing files from the map cache
        if (main.lastModified() != cacheCfg.getLong("modified." + main.getName()) || xtea.lastModified() != cacheCfg.getLong("modified." + xtea.getName())) {
            Log.debug("Cache change detected. Recalculating!");
            //So we must scan through all of the map files, attempt to parse them, and blacklist broken ones
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 256; y++) {
                    Reference ref;
                    try {
                        ref = r.getReferenceByHash("l" + x + "_" + y);
                    } catch (FileNotFoundException e) {
                        continue;
                    }
                    try {
                        MapCache.getObjects(cache, x, y);
                    } catch (IOException e) {
                        //File is broken or encrypted and we don't have the key.
                        r.remove(ref.getId());
                        //Blacklist the file
                        cacheCfg.set("encryptedMaps." + ref.getId(), ref.getId());
                    }
                }
            }
            // Update config & save
            cacheCfg.set("modified." + main.getName(), main.lastModified());
            cacheCfg.set("modified." + xtea.getName(), xtea.lastModified());
            cacheCfg.save();
        } else {
            // We previously worked on this cache, and listed all broken files.
            // This is faster than testing each file if it's broken or not.
            for (String refId : cacheCfg.getSection("encryptedMaps").getKeys()) {
                int referenceId = cacheCfg.getInt("encryptedMaps." + refId, -1);
                r.remove(referenceId);
            }
        }

        // Now we re-encode the raw version of the data
        r.setVersion(r.getVersion() + 1);
        f.setData(r.encode()); //Set the file data to the reference table
        cache.setRaw(255, IDX.LANDSCAPES, f.encode());
        cache.rebuildChecksum();

        return cache;
    }

    /**
     * Hashes the given string for map entry lookups. Names are case
     * insensitive.
     *
     * @param s the string to hash
     * @return the hash value
     */
    public static int getNameHash(String s) {
        int count = 0;
        s = s.toLowerCase(); //Client forces all names to be lowercase.
        byte[] characters = s.getBytes();
        for (int i = 0; i < s.length(); i++) {
            count = (characters[i] & 0xff) + ((count << 5) - count);
        }
        return count;
    }

    /**
     * Loads this cache from the given folder. Any previous data is thrown away.
     * A RandomAccessFile is created for all index files (main_file_cache.idx*)
     * except for file 255, and another is created for the main_file_cache.dat2.
     * A cache that does not have this method called on it first will throw
     * exceptions.
     *
     * @param folder the folder to load from
     * @throws IOException if an IO error occurs
     */
    public void load(File folder) throws IOException {
        //Filetable 255's data stream
        index = new RandomAccessFile(new File(folder, "main_file_cache.idx255"), "r");
        //The main 100mb+ cache file with raw data
        data = new RandomAccessFile(new File(folder, "main_file_cache.dat2"), "r");
        /* FileTable 255 */
        metaFiles = FileTable.decode(255, index, data);

        //Number of IDX files
        int size = (int) (index.length() / ReferenceTable.IDX_BLOCK_LEN);
        this.tables = new FileTable[size];
        this.refs = new ReferenceTable[size];
        this.indexes = new RandomAccessFile[size];

        //Load the XTEA file to decrypt files.
        this.xteas = new XTEAStore(new File(folder, "xteas.xstore2"));
        this.xteas.load();

        for (int i = 0; i < this.tables.length; i++) {
            try {
                //May throw FileNotFoundException
                CacheFile f = metaFiles.get(i, null);

                //Throws IOException if file is corrupt
                ReferenceTable r = ReferenceTable.decode(i, f);
                refs[i] = r;

                //Create a stream for this particular index file
                RandomAccessFile idx = new RandomAccessFile(new File(folder, "main_file_cache.idx" + i), "r");
                this.indexes[i] = idx;

                //Decode the index stream into a filetable.
                tables[i] = FileTable.decode(i, idx, data);
            } catch (FileNotFoundException e) {
                //Occurs for a few IDX values
            } catch (Exception e) {
                //Failed outright. Corrupt cache or bad parsing.
                e.printStackTrace();
                System.out.println("Error parsing IDX " + i + " index.");
            }
        }

        rebuildChecksum();

        this.prune();
    }

    public ByteBuffer getRaw(int idx, int fileId) throws IOException {
        ByteBuffer raw = raws.get((idx << 24) | fileId);
        if (raw == null) {
            //raw = CacheFile.getRaw(idx, index.getChannel(), data.getChannel(), fileId);
            if (idx == 255) {
                raw = CacheFile.getRaw(idx, index.getChannel(), data.getChannel(), fileId);
            } else {
                raw = CacheFile.getRaw(idx, this.indexes[idx].getChannel(), data.getChannel(), fileId);
            }
            raws.put((idx << 24) | fileId, raw);
        }

        return raw.asReadOnlyBuffer();
    }

    public void setRaw(int idx, int fileId, ByteBuffer buffer) {
        raws.put((idx << 24) | fileId, buffer.asReadOnlyBuffer());
        this.checksum = null; //Our old checksum is now invalid
    }

    public void rebuildChecksum() {
        this.checksum = new ChecksumTable(this.tables.length);
        /* Generate reference tables and build checksum */
        for (int i = 0; i < this.tables.length; i++) {
            //Checksum info
            int crc = 0;
            int version = 0;
            byte[] whirlpool = new byte[64];

            try {
                //Raw, compressed/encrypted data
                ByteBuffer raw = getRaw(255, i);
                if (raw.limit() <= 0) throw new FileNotFoundException();

                ReferenceTable r = this.refs[i];

                //Build checksum values
                crc = ByteBufferUtils.getCrcChecksum(raw);
                version = r.getVersion();
                raw.position(0);
                whirlpool = ByteBufferUtils.getWhirlpoolDigest(raw);
            } catch (FileNotFoundException e) {
                //Occurs for a few IDX values
                whirlpool = Whirlpool.whirlpool(new byte[0], 0, 0);
            } catch (Exception e) {
                //Failed outright. Corrupt cache or bad parsing.
                e.printStackTrace();
                System.out.println("Error parsing IDX " + i + " index.");
                whirlpool = Whirlpool.whirlpool(new byte[0], 0, 0);
            } finally {
                //Append found values to checksum table.
                //This clause is called nomatter what happens with exceptions.
                checksum.setEntry(i, new ChecksumTable.Entry(crc, version, whirlpool));
            }
        }
    }

    public ByteBuffer createResponse(int idx, int fileId, int opcode) throws IOException {
        //Our return value, we write data to this
        ByteBuffer out;
        //The raw file's data. This is not decompressed/decrypted.
        ByteBuffer raw;

        //Length of data, excludes any version, length or compression bytes
        int length;
        //Compression type (0 = none)
        int compression;

        if (idx == 255 && fileId == 255) {
            //Client is requesting main checksum. Client uses this to discover
            //which files to request from the server.
            if (checksum == null) {
                //If our checksum was destroyed, rebuild it.
                rebuildChecksum();
            }
            raw = checksum.encode(true);

            compression = 0;
            length = raw.remaining();
        } else {
            raw = getRaw(idx, fileId);
            compression = raw.get() & 0xFF;
            length = raw.getInt();
        }
        //Allocate space for the 8 byte header, raw data and 0xFF markers after every 512th byte.
        out = ByteBuffer.allocate(raw.remaining() + 8 + ((raw.remaining() + 8) / 512) + 4); //Why +4?

        //Consists of compression & priority flag
        int attribs = compression;
        //Opcode 0 is a priority request
        if (opcode == 0) attribs |= PRIORITY_FLAG;

        //Write our file headers
        out.put((byte) idx);
        out.putShort((short) fileId);
        out.put((byte) attribs);
        out.putInt(length);

        //We write 4 extra bytes if it's compressed (the length of the decompressed archive)
        //This also strips away the file version if it is appended at the end of the file.
        //The file version is sent in the checksum table for the (255,255) request. The file version
        //is a 2-byte short at the end of the buffer.
        raw.limit(raw.position() + length + (compression == 0 ? 0 : 4));

        //Write the raw file
        while (raw.remaining() > 0) {
            if (out.position() % 512 == 0) {
                //Every 512th byte is followed by a magic 0xFF
                out.put((byte) 0xFF);
            }

            out.put(raw.get());
        }
        out.flip();

        return out;
    }

    /**
     * The XTEAStore that this cache uses to fetch keys for decrypting files.
     * Primarily, this consists of Landscape Object files, but remains in a
     * format that is flexible for any file in the cache.
     *
     * @return The XTEAStore assosciated with this cache.
     */
    public XTEAStore getXTEA() {
        return xteas;
    }

    public FileChannel getIndexChannel(int idx) {
        if (idx < 0 || (idx > indexes.length && idx != 255)) throw new IllegalArgumentException("Bad IDX request: " + idx);
        RandomAccessFile raf;
        if (idx == 255) raf = index;
        else {
            raf = indexes[idx];
        }
        if (raf == null) {
            return null;
        }

        return raf.getChannel();
    }

    public FileChannel getDataChannel() {
        return data.getChannel();
    }

    /**
     * Fetches the archive by a given IDX and fileId value. This method caches
     * the archives, which are immutable.
     *
     * @param idx    the IDX value (0-37)
     * @param fileId the file id
     * @param key    the XTEA key to use to decrypt the cache file (used in
     *               Cache.getFile() call)
     * @return the archive
     * @throws IOException if the archive could not be retrieved. Not thrown if
     *                     the archive is already cached.
     */
    public Archive getArchive(int idx, int fileId) throws IOException {
        int uid = (idx << 24) | (fileId);
        Archive a = archives.get(uid);
		/* We've got a previously parsed version of this file, return it */
        if (a != null) return a;

        CacheFile f = getFile(idx, fileId);
        Reference r = refs[idx].getReference(fileId);

        try {
            a = Archive.decode(r, f);
        } catch (IOException e) {
            throw new IOException("Failed to decode archive, IDX: " + idx + ", File: " + fileId + (e.getMessage() == null ? "" : ": " + e.getMessage()), e);
        }
		/* Cache the result */
        archives.put(uid, a);

        return a;
    }

    /**
     * Destroys any data in this cache so that load() may be called again. This
     * allows the garbage collection to clean up.
     *
     * @throws IOException if an IO error occurs closing file handles
     */
    public void close() throws IOException {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != null) indexes[i].close();
        }
        this.data.close();

        this.indexes = null;
        this.data = null;
        this.archives = new HashMap<>();
        this.refs = null;
        this.tables = null;
    }

    /**
     * Fetches the File ID in the given IDX container with the given identifier.
     *
     * @param idx        the idx value (0-37)
     * @param identifier the identifier tag to search for
     * @return the file ID
     * @throws FileNotFoundException if the file is not found
     */
    public int getFileId(int idx, int identifier) throws FileNotFoundException {
        if (idx >= refs.length || idx < 0) throw new FileNotFoundException("IDX invalid " + idx);

        ReferenceTable t = refs[idx];
        for (Reference r : t.getReferences()) {
            if (r.getIdentifier() == identifier) {
                return r.getId();
            }
        }
        throw new FileNotFoundException("IDX " + idx + ", identifier " + identifier);
    }

    /**
     * Fetches the ID of the file in the given IDX with the given name hash
     *
     * @param idx  the IDX value, 0-37
     * @param name the name of the file (case insensitive), this is hashed
     * @return the file id
     * @throws FileNotFoundException if the file was not found.
     */
    public int getFileId(int idx, String name) throws FileNotFoundException {
        int hash = getNameHash(name);
        return getFileId(idx, hash);
    }

    /**
     * Fetches the reference table for the given IDX value. These are
     * lightweight and do not contain actual file data
     *
     * @param idx the IDX of the reference table to get
     * @return the reference table, null if IDX is bad
     * @throws IndexOutOfBoundsException if the given IDX value is not found
     */
    public ReferenceTable getReferenceTable(int idx) {
        return refs[idx];
    }

    /**
     * The number of IDX files that are loaded, this includes any bad IDX files
     * which could not be parsed.
     *
     * @return the number of IDX values
     */
    public int getIDXCount() {
        return this.indexes.length;
    }

    /**
     * Fetches the file table for the given IDX value. These are heavy, and
     * contain the actual file data in a cache.
     *
     * @param idx the IDX of the reference table to get
     * @return the file table, null if IDX is bad
     * @throws IndexOutOfBoundsException if the given IDX value is not found
     */
    public FileTable getFileTable(int idx) {
        return tables[idx];
    }

    /**
     * Fetches the requested CacheFile. This file is cached so that it does not
     * have to be parsed multiple times.
     *
     * @param idx    the IDX value, 0-37
     * @param fileId the fileId to fetch
     * @param key    the XTEA key to use to decrypt the file, null if it is not
     *               encrypted
     * @return the cache file, not null.
     * @throws IOException           if an IO error occurs
     * @throws FileNotFoundException if the file was not found.
     */
    public CacheFile getFile(int idx, int fileId) throws IOException {
        //TODO: This should probably read data from raws instead!
        if (idx == 255) {
            return this.metaFiles.get(fileId, xteas.getKey(idx, fileId));
        }
        return tables[idx].get(fileId, xteas.getKey(idx, fileId));
    }

    /**
     * Fetches the metadata for the requested cache file. This is cached.
     *
     * @param idx    the IDX value, 0-37
     * @param fileId the fileId to fetch
     * @return the reference file, or null if the reference does not exist.
     */
    public Reference getReference(int idx, int fileId) {
        return refs[idx].getReference(fileId);
    }

    /**
     * Removes the contents of the memory cache for this RS Cache. This calls
     * FileTable.discardCache() on each FileTable controlled by this object, and
     * also clears the archives cache. This method is useful for when a
     * frequently accessed section of the filestore will no longer be accessed,
     * and memory is desireable.
     */
    public void prune() {
        for (FileTable t : this.tables) {
            if (t != null) t.prune();
        }

        metaFiles.prune();
        this.archives.clear();
    }
}