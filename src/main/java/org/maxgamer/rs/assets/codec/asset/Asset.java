package org.maxgamer.rs.assets.codec.asset;

import org.maxgamer.rs.util.Assert;
import org.maxgamer.rs.assets.codec.Codec;
import org.maxgamer.rs.cache.RSCompression;
import org.maxgamer.rs.cache.XTEAKey;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Asset class represents a single file in the cache
 *
 * @author netherfoam
 */
public final class Asset extends Codec {
    /**
     * Wrap the given (decoded) payload in a new asset, with no XTEA encryption.
     * @param content the content to set
     * @return the asset
     */
    public static Asset wrap(byte[] content) {
        Asset asset = new Asset(null);
        asset.setPayload(ByteBuffer.wrap(content));

        return asset;
    }

    /**
     * Creates a new asset with the given XTEA encryption key, compression, version and payload.
     *
     * @param key the XTEA key
     * @param compression the compression
     * @param version the version of the file
     * @param payload the data contained by the file (decoded and decompressed)
     * @return the asset
     */
    public static Asset create(XTEAKey key, RSCompression compression, int version, ByteBuffer payload) {
        Assert.notNull(compression, "Compression may not be null");
        Assert.isPositive(version + 1, "Version must be positive or -1");
        Assert.notNull(payload, "Payload may not be null");

        Asset asset = new Asset(key);

        asset.compression = compression;
        asset.version = version;
        asset.payload = payload.asReadOnlyBuffer();

        return asset;
    }

    /**
     * The compression to use for this asset
     */
    private RSCompression compression = RSCompression.NONE;

    /**
     * The decoded contents of this asset
     */
    private ByteBuffer payload;

    /**
     * The XTEA encryption key for this asset. Probably null.
     */
    private XTEAKey key;

    /**
     * The version of this asset
     */
    private int version;

    /**
     * Constructs a new Asset with no content, compression or version
     * @param key the encryption key, may be null
     */
    public Asset(XTEAKey key) {
        this.key = key;
    }

    /**
     * Constructs an existing asset with the given encoded content. This sets the compression, version and
     * payload to those stored in the content.
     *
     * @param key the encryption key for the content, may be null
     * @param content the content
     * @throws IOException if the content can't be decoded properly (usually from an incorrect key)
     */
    public Asset(XTEAKey key, ByteBuffer content) throws IOException {
        this(key);
        Assert.notNull(content, "Content may not be null");

        decode(content);
    }

    /**
     * Changes the compression for this file
     * @param compression the compression
     */
    public void setCompression(RSCompression compression) {
        Assert.notNull(compression, "Compression may not be null");

        this.compression = compression;
    }

    public void setKey(XTEAKey key) {
        this.key = key;
    }

    public void setVersion(int version) {
        Assert.isPositive(version, "Version must be positive");
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public XTEAKey getKey() {
        return key;
    }

    public void setPayload(ByteBuffer decoded) {
        Assert.notNull(decoded, "Buffer may not be null");

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
        encoded.position(encoded.limit());
        encoded.limit(limit);

        /* The version is the last two bytes */
        if (encoded.remaining() >= 2) {
            version = encoded.getShort();
        } else {
            version = -1; /* No version attached */
        }

        Assert.isTrue(!encoded.hasRemaining(), "No data should be remaining in buffer. Remaining: " + encoded.remaining() + " bytes");
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
