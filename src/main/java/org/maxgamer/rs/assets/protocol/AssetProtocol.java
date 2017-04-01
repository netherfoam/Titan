package org.maxgamer.rs.assets.protocol;

import net.openrs.cache.ChecksumTable;
import net.openrs.util.ByteBufferUtils;
import net.openrs.util.crypto.Whirlpool;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.util.Assert;
import org.maxgamer.rs.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link AssetProtocol} class generates response using the JS5 protocol. These responses
 * can be written directly to the client when streaming the cache.
 *
 * @author netherfoam
 */
public class AssetProtocol {
    /**
     * Container class for holding compression, length and content of a response together
     */
    private static class Response {
        /**
         * The compression used
         */
        private final int compression;

        /**
         * The length of the response
         */
        private final int length;

        /**
         * The content
         */
        private final ByteBuffer content;

        public Response(int compression, int length, ByteBuffer content) {
            this.compression = compression;
            this.length = length;
            this.content = content;
        }
    }

    /**
     * A flag which indicates this is a priority response to a client's priority
     * cache file request.
     */
    public static final byte PRIORITY_FLAG = (byte) 0x80;

    /**
     * The asset storage containing the files we send
     */
    private AssetStorage storage;

    /**
     * The {@link ChecksumTable} that contains meta information and hashes about all of the files in
     * the cache. It is decoded by the client to figure out which files are available.
     */
    private ChecksumTable checksum;

    /**
     * A cache of the index buffers, since we need to re-encode them on the fly. So we avoid doing it
     * more often than necessary
     */
    private Map<Integer, SoftReference<ByteBuffer>> cache = Collections.synchronizedMap(new HashMap<Integer, SoftReference<ByteBuffer>>(37));

    /**
     * The timestamp when this {@link AssetProtocol} was initialised. Used to make sure the underlying
     * cache hasn't been modified.
     */
    private long initializedAt = -1;

    /**
     * A read write lock to prevent us from generating multiple checksums on the fly. This
     * is necessary, otherwise, multiple requests for the same file will trigger multiple
     * builds of the *same* checksum table. It's faster and more effective to just wait for
     * the first table to be built and return that
     */
    private ReentrantLock checksumLock = new ReentrantLock();

    /**
     * Constructs a new {@link AssetProtocol} which will stream files for the given cache to a JS5 client
     * @param storage the storage
     */
    public AssetProtocol(AssetStorage storage) {
        this.storage = storage;
    }

    /**
     * Builds the checksum in the background. If the checksum is already built, this has no effect.
     * @param executor the executor service
     */
    public void initialize(ExecutorService executor) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    getChecksum();
                } catch (IOException e) {
                    Log.warning("Couldn't pre-initialize asset storage protocol. This could be a sign of a " +
                                "corrupt cache. Clients will likely not be able to stream the cache.");
                }
            }
        });
    }

    /**
     * Fetches the checksum table. This will lazy initialize the table if necessary.
     * @return the checksum table
     * @throws IOException if the table can't be generated
     */
    public ChecksumTable getChecksum() throws IOException {
        if(checksum == null) {
            rebuildChecksum();
            initializedAt = System.currentTimeMillis();
        }

        return checksum;
    }

    /**
     * Regenerate the checksum table. This uses the lock
     * @throws IOException if the table can't be generated
     */
    private void rebuildChecksum() throws IOException {
        // NB that there are still issues if the checksum table changes after a client
        // has downloaded the cache.
        checksumLock.lock();

        try {
            this.cache.clear();
            ChecksumTable checksum = new ChecksumTable(storage.size());
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

                    set(i, raw);

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

            this.checksum = checksum;
            cache.put(255, new SoftReference<>(checksum.encode(true)));
        } finally {
            checksumLock.unlock();
        }
    }

    /**
     * Caches the given data for the given index table. This is used because compressing idx files is slow, but frequent.
     * @param idx the index to cache
     * @param encoded the data to cache
     */
    private void set(int idx, ByteBuffer encoded) {
        if(encoded == null) {
            cache.remove(idx);
            return;
        }

        cache.put(idx, new SoftReference<>(encoded.asReadOnlyBuffer()));
    }

    /**
     * Fetches the data for the given index table, if it's available in the cache.
     * @param idx the index
     * @return the raw table data or null if it's not cached
     * @throws IOException if the modification check fails
     */
    private ByteBuffer get(int idx) throws IOException {
        long modified;
        if(idx == 255) modified = storage.getMasterTable().modified();
        else modified = storage.getTable(idx).modified();

        if(initializedAt < modified && checksum != null) {
            throw new IOException(
                    "The cache has been modified since the AssetProtocol was initialised. This is " +
                    "dangerous, if clients have already streamed the cache - or worse - are still streaming the " +
                    "cache."
            );
        }

        SoftReference<ByteBuffer> ref = cache.get(idx);
        if(ref == null) return null;

        ByteBuffer buffer = ref.get();
        if(buffer == null) return null;

        return buffer.asReadOnlyBuffer();
    }

    /**
     * Generates a response usable by a client for the given index, file and opcode.
     * @param idx the index
     * @param fileId the file
     * @param opcode the opcode (indicates priority, or potentially, length)
     * @return the buffer
     * @throws IOException if the response can't be generated
     */
    public ByteBuffer response(int idx, int fileId, int opcode) throws IOException {
        Response r;

        if(idx == 255 && fileId == 255) {
            ByteBuffer buffer = get(255);

            if(buffer == null) {
                buffer = getChecksum().encode(true);
                set(255, buffer);
            }

            r = new Response(0, buffer.remaining(), buffer);
        } else if(idx == 255) {
            ByteBuffer buffer = get(fileId);

            if(buffer == null) {
                IndexTable index = storage.getIndex(fileId);
                if(index == null) throw new FileNotFoundException("No such index: " + fileId);

                ByteBuffer payload = index.encode();
                Asset asset = Asset.create(null, index.getCompression(), -1, payload);
                buffer = asset.encode();

                Assert.equal(AssetWriter.crc32(buffer), checksum.getEntry(fileId).getCrc());
                set(fileId, buffer);
            }

            r = new Response(buffer.get() & 0xFF, buffer.getInt(), buffer);
        } else {
            ByteBuffer buffer = storage.getTable(idx).read(fileId);
            r = new Response(buffer.get() & 0xFF, buffer.getInt(), buffer);
        }

        return encode(idx, fileId, opcode, r);
    }

    /**
     * Encodes the given response data. This adds the magic 0xFF bytes as well.
     * @param idx the index
     * @param fileId the file
     * @param opcode the opcode
     * @param response the compressed file stored in the storage, without compression or length headers
     * @return the response, readable by a client.
     */
    private ByteBuffer encode(int idx, int fileId, int opcode, Response response) {
        int compression = response.compression;
        int length = response.length;
        ByteBuffer content = response.content;

        // Allocate space for the 8 byte header, raw data and 0xFF markers after every 512th byte.
        ByteBuffer out = ByteBuffer.allocate(content.remaining() + 8 + ((content.remaining() + 8) / 512) + 4); //Why +4?

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
        content.limit(content.position() + length + (compression == 0 ? 0 : 4));

        byte[] buffer = new byte[512];
        // Write the raw file
        while (content.remaining() > 0) {
            if (out.position() % 512 == 0) {
                // Every 512th byte is followed by a magic 0xFF
                out.put((byte) 0xFF);
            }

            int n = Math.min(512 - (out.position() % 512), content.remaining());
            content.get(buffer, 0, n);
            out.put(buffer, 0, n);
        }
        out.flip();

        return out;
    }
}
