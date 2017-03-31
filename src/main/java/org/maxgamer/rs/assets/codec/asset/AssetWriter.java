package org.maxgamer.rs.assets.codec.asset;

import net.openrs.util.crypto.Whirlpool;
import org.maxgamer.rs.util.Assert;
import org.maxgamer.rs.assets.DataTable;
import org.maxgamer.rs.cache.XTEAKey;
import org.maxgamer.rs.cache.XTEAStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * @author netherfoam
 */
public class AssetWriter {
    /**
     * Performs a whirlpool function on the remaining content of the given {@link ByteBuffer}. This doesn't
     * consume the rest of the buffer, since it takes a read only copy and works off that. It avoids needlessly
     * duplicating the entire buffer as a byte array.
     *
     * @param bb the byte buffer
     * @return the hash
     */
    public static byte[] whirlpool(ByteBuffer bb) {
        bb = bb.asReadOnlyBuffer();

        Whirlpool whirlpool = new Whirlpool();
        whirlpool.NESSIEinit();

        byte[] source = new byte[1024];
        while(bb.remaining() > 0) {
            int length = Math.min(bb.remaining(), 1024);
            bb.get(source, 0, length);

            whirlpool.NESSIEadd(source, length * 8);
        }

        byte digest[] = new byte[64];
        whirlpool.NESSIEfinalize(digest);

        return digest;
    }

    /**
     * Performs a CRC32 hash on the remaining content of the given {@link ByteBuffer}. This doesn't consume
     * the rest of the buffer, since it takes a read only copy and works off that. It avoids needlessly
     * duplicating the entire buffer as a byte array
     *
     * @param bb the buffer to checksum
     * @return the crc as a 32 bit integer
     */
    public static int crc32(ByteBuffer bb) {
        bb = bb.asReadOnlyBuffer();

        CRC32 crc = new CRC32();

        byte[] source = new byte[1024];
        while(bb.remaining() > 0) {
            int length = Math.min(bb.remaining(), 1024);
            bb.get(source, 0, length);

            crc.update(source, 0, length);
        }

        return (int) crc.getValue();
    }

    private static final class WriteRequest {
        private final AssetReference reference;
        private final ByteBuffer content;
        private final XTEAKey key;

        public WriteRequest(AssetReference reference, ByteBuffer content, XTEAKey key) {
            this.reference = reference;
            this.content = content;
            this.key = key;
        }
    }

    private IndexTable indexTable;
    private DataTable masterTable;
    private DataTable dataTable;
    private XTEAStore xteas;

    private Map<Integer, WriteRequest> writes = new HashMap<>();

    /**
     * Constructs a new AssetWriter. This is a wrapper class that helps write files into
     * the cache.
     *
     * @param indexTable the decoded list of files in the index
     * @param masterTable the index table to write changes to file meta into
     * @param dataTable the data table to write changes to file contents into
     */
    public AssetWriter(IndexTable indexTable, DataTable masterTable, DataTable dataTable, XTEAStore xteas) {
        Assert.notNull(indexTable, "IndexTable may not be null");
        Assert.notNull(masterTable, "MasterTable may not be null");
        Assert.notNull(dataTable, "DataTable may not be null");
        Assert.notNull(xteas, "XTEAStore may not be null");

        this.indexTable = indexTable;
        this.masterTable = masterTable;
        this.dataTable = dataTable;
        this.xteas = xteas;
    }

    /**
     * Appends the given write to the list of writes to perform when {@link #commit()} is called. This does not
     * modify the given {@link ByteBuffer}, as it takes a read-only copy for later.
     *
     * @param id the id of the file to write to
     * @param reference the existing reference to the file
     * @param asset the asset to write for the file
     * @return this
     */
    public AssetWriter write(int id, AssetReference reference, Asset asset) throws IOException {
        Assert.isPositive(id, "File ID must be positive");
        Assert.notNull(reference, "Reference may not be null");
        Assert.notNull(asset, "Asset may not be null");

        // Take a read only copy for writing later
        ByteBuffer content = asset.encode();

        // We don't want to modify the original, in case it was pulled from the cache (until we commit)
        reference = reference.copy();
        reference.whirlpool = whirlpool(content);
        reference.crc = crc32(content);

        // We assert that asset.getVersion() == reference.getVersion()
        Assert.isTrue(asset.getVersion() == -1 || reference.getVersion() == asset.getVersion(), "Asset version must equal reference version, if set");

        writes.put(id, new WriteRequest(reference, content, asset.getKey()));

        return this;
    }

    public AssetWriter delete(int id) throws IOException {
        Assert.isPositive(id, "File ID must be positive");

        writes.put(id, null);

        return this;
    }

    /**
     * Writes all of the contents to the data table and the meta object, then writes the encoded meta
     * object to the data table as well
     *
     * @throws IOException if the data can't be written
     */
    public void commit() throws IOException {
        if(writes.isEmpty()) {
            // No assets to be written!
            return;
        }

        // First, we write all of updated contents of files to disk
        for(Map.Entry<Integer, WriteRequest> entry : writes.entrySet()) {
            int id = entry.getKey();
            WriteRequest writeRequest = entry.getValue();

            if(writeRequest == null) {
                indexTable.getReferences().remove(id);
                dataTable.erase(id);
                xteas.setKey(indexTable.getIdx(), id, null);
            } else {
                AssetReference ref = writeRequest.reference;
                ByteBuffer bb = writeRequest.content;

                indexTable.getReferences().put(id, ref);
                dataTable.write(id, bb);

                // Update the encryption key in the XTEA store
                xteas.setKey(indexTable.getIdx(), id, writeRequest.key);
            }
        }

        // Now we write the new meta information about the assets. First, we must compress the
        // encoded information though, using an Asset wrapper, which supports compression.
        Asset wrapper = indexTable.toAsset();

        // Finally, save to disk
        masterTable.write(indexTable.getIdx(), wrapper.encode());
    }
}
