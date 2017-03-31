package org.maxgamer.rs.assets.protocol;

import net.openrs.cache.ChecksumTable;
import net.openrs.util.ByteBufferUtils;
import net.openrs.util.crypto.Whirlpool;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class AssetProtocol {
    /**
     * A flag which indicates this is a priority response to a client's priority
     * cache file request.
     */
    public static final byte PRIORITY_FLAG = (byte) 0x80;

    /**
     * A cache of the index buffers, since we need to re-encode them on the fly. So we avoid doing it
     * more often than necessary
     */
    private Map<Integer, SoftReference<ByteBuffer>> cache = new HashMap<>(37);

    private AssetStorage storage;

    private ChecksumTable checksum;

    public AssetProtocol(AssetStorage storage) {
        this.storage = storage;
    }

    public void rebuildChecksum() throws IOException {
        this.cache.clear();
        this.checksum = new ChecksumTable(storage.size());
        /* Generate reference tables and build checksum */
        for (int i = 0; i < checksum.getSize(); i++) {
            //Checksum info
            int crc = 0;
            int version = 0;
            byte[] whirlpool = new byte[64];

            try {
                IndexTable index = storage.getIndex(i);
                ByteBuffer payload = index.encode();
                Asset asset = Asset.create(null, index.getCompression(), -1, payload);
                ByteBuffer raw = asset.encode();

                cache.put(i, new SoftReference<>(raw.asReadOnlyBuffer()));

                // Build checksum values
                crc = ByteBufferUtils.getCrcChecksum(raw);
                version = index.getVersion();
                raw.position(0);
                whirlpool = ByteBufferUtils.getWhirlpoolDigest(raw);
            } catch (FileNotFoundException e) {
                // Occurs for a few IDX values which are missing from the cache
                whirlpool = Whirlpool.whirlpool(new byte[0], 0, 0);
            } catch (Exception e) {
                // Failed outright. Corrupt cache or bad parsing.
                e.printStackTrace();
                System.out.println("Error parsing IDX " + i + " index.");
                whirlpool = Whirlpool.whirlpool(new byte[0], 0, 0);
            } finally {
                // Append found values to checksum table.
                checksum.setEntry(i, new ChecksumTable.Entry(crc, version, whirlpool));
            }
        }

        cache.put(255, new SoftReference<>(checksum.encode(true)));
    }

    public ByteBuffer response(int idx, int fileId, int opcode) throws IOException {
        //Our return value, we write data to this
        ByteBuffer out;
        //The raw file's data. This is not decompressed/decrypted.
        ByteBuffer raw = null;

        //Length of data, excludes any version, length or compression bytes
        int length;
        //Compression type (0 = none)
        int compression;

        if (idx == 255 && fileId == 255) {
            // Client is requesting main checksum. Client uses this to discover
            // which files to request from the server.
            SoftReference<ByteBuffer> ref = cache.get(255);

            if(ref != null) {
                raw = ref.get();
            }

            if(raw == null) {
                if (checksum == null) {
                    //If our checksum was destroyed, rebuild it.
                    rebuildChecksum();
                }
                raw = checksum.encode(true);
                ref = new SoftReference<>(raw.asReadOnlyBuffer());
                cache.put(255, ref);
            } else {
                // We don't want to modify the buffer in the cache
                raw = raw.asReadOnlyBuffer();
            }

            compression = 0;
            length = raw.remaining();
        } else if(idx == 255) {
            SoftReference<ByteBuffer> ref = cache.get(fileId);

            if(ref != null) {
                raw = ref.get();
            }

            if(raw == null) {
                IndexTable index = storage.getIndex(fileId);
                if(index == null) throw new FileNotFoundException("No such index: " + fileId);

                ByteBuffer payload = index.encode();
                Asset asset = Asset.create(null, index.getCompression(), -1, payload);
                raw = asset.encode();

                Assert.equal(AssetWriter.crc32(raw), checksum.getEntry(fileId).getCrc());
                ref = new SoftReference<>(raw.asReadOnlyBuffer());
                cache.put(fileId, ref);
            } else {
                // We don't want to modify the buffer in the cache
                raw = raw.asReadOnlyBuffer();
            }

            //raw = storage.getMasterTable().read(fileId);
            compression = raw.get() & 0xFF;
            length = raw.getInt();
        } else {
            raw = storage.getTable(idx).read(fileId);
            compression = raw.get() & 0xFF;
            length = raw.getInt();
        }
        // Allocate space for the 8 byte header, raw data and 0xFF markers after every 512th byte.
        out = ByteBuffer.allocate(raw.remaining() + 8 + ((raw.remaining() + 8) / 512) + 4); //Why +4?

        // Consists of compression & priority flag
        int attribs = compression;
        // Opcode 0 is a priority request
        if (opcode == 0) attribs |= PRIORITY_FLAG;

        //Write our file headers
        out.put((byte) idx);
        out.putShort((short) fileId);
        out.put((byte) attribs);
        out.putInt(length);

        // We write 4 extra bytes if it's compressed (the length of the decompressed archive)
        // This also strips away the file version if it is appended at the end of the file.
        // The file version is sent in the checksum table for the (255,255) request. The file version
        // is a 2-byte short at the end of the buffer.
        raw.limit(raw.position() + length + (compression == 0 ? 0 : 4));

        // Write the raw file
        while (raw.remaining() > 0) {
            if (out.position() % 512 == 0) {
                // Every 512th byte is followed by a magic 0xFF
                out.put((byte) 0xFF);
            }

            out.put(raw.get());
        }
        out.flip();

        return out;
    }

    public ChecksumTable getChecksum() {
        return checksum;
    }
}
