package org.maxgamer.rs.assets.codec.asset;

import org.maxgamer.rs.Assert;
import org.maxgamer.rs.assets.codec.Codec;
import org.maxgamer.rs.cache.RSCompression;
import org.maxgamer.rs.cache.XTEAKey;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public final class Asset extends Codec {
    public static Asset wrap(byte[] content) {
        Asset asset = new Asset(null);
        asset.setPayload(ByteBuffer.wrap(content));

        return asset;
    }

    public static Asset create(XTEAKey key, RSCompression compression, int version, ByteBuffer payload) {
        Asset asset = new Asset(key);

        asset.compression = compression;
        asset.version = version;
        asset.payload = payload.asReadOnlyBuffer();

        return asset;
    }

    private RSCompression compression = RSCompression.NONE;
    private ByteBuffer payload;
    private XTEAKey key;
    private int version;

    public Asset(XTEAKey key) {
        this.key = key;
    }

    public Asset(XTEAKey key, ByteBuffer content) throws IOException {
        this(key);

        decode(content);
    }

    public void setCompression(RSCompression compression) {
        this.compression = compression;
    }

    public void setKey(XTEAKey key) {
        this.key = key;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public XTEAKey getKey() {
        return key;
    }

    public void setPayload(ByteBuffer decoded) {
        Assert.notNull(decoded);

        this.payload = decoded.asReadOnlyBuffer();
    }

    public ByteBuffer getPayload() {
        if(payload == null) return ByteBuffer.allocate(0);

        return payload.asReadOnlyBuffer();
    }

    public RSCompression getCompression() {
        return compression;
    }

    @Override
    public void decode(ByteBuffer encoded) throws IOException {
        // TODO: This can be done far more effectively without duplicating buffers
        if(encoded.remaining() <= 0) {
            // No data means nothing to decode, so we pick some sensible defaults
            compression = RSCompression.NONE;
            payload = null;
            version = -1;
            return;
        }

        compression = RSCompression.forId(encoded.get() & 0xFF);
        int length = encoded.getInt(); /* Length of compressed data */
        int limit = encoded.limit(); /* Preserve current limit */

        // +4 if compressed, else +0, since compressed files are prepended with an integer specifying decompressed length
        // Only read length bytes, the new limit is <= the old limit
        encoded.limit(encoded.position() + length + (compression == RSCompression.NONE ? 0 : 4));

        /* The following will most likely throw an exception if the given key was invalid
         * or not supplied.
         */
        payload = compression.decode(encoded, key);

        /* Restore previous limit */
        encoded.limit(limit);

        /* The version is the last two bytes */
        if (encoded.remaining() >= 2) {
            version = encoded.getShort();
        } else {
            version = -1; /* No version attached */
        }

        Assert.isTrue(!encoded.hasRemaining(), "No data should be remaining in buffer");
    }

    @Override
    public ByteBuffer encode() throws IOException {
        /* compress the data */
        ByteBuffer compressed = compression.encode(payload.asReadOnlyBuffer(), null);

        /* calculate the size of the header and trailer and allocate a buffer */
        int header = 5;
        if (this.version != -1) {
            header += 2;
        }
        ByteBuffer buf = ByteBuffer.allocate(header + compressed.remaining());

        /* write the header, with the optional uncompressed length */
        buf.put(compression.getId());

        if (compression == RSCompression.NONE) {
            buf.putInt(compressed.remaining());
        } else {
            buf.putInt(compressed.remaining() - 4); // first 4 bytes are length of decompressed data
        }
        buf.put(compressed);

        /* write the trailer with the optional version */
        if (this.version != -1) {
            buf.putShort((short) version);
        }

        Assert.isTrue(!buf.hasRemaining(), "No data should be remaining in buffer");

        /* flip the buffer and return it */
        buf.flip();

        return buf;
    }
}
