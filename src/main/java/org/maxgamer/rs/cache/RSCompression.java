package org.maxgamer.rs.cache;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.maxgamer.rs.util.io.ByteBufferInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * TODO: There are a few optimisations that could be done here, to speed up this class.
 */
public enum RSCompression {
    /**
     * There is no compression used. The return result of these is a read-only
     * copy of the given buffer.
     */
    NONE(0) {
        @Override
        public ByteBuffer decode(ByteBuffer bb, XTEAKey key) {
            if(key == null && bb.isReadOnly()) {
                // No need to duplicate the buffer here
                return bb.asReadOnlyBuffer();
            }

            // We don't want to modify the buffer itself
            ByteBuffer copy = ByteBuffer.allocate(bb.remaining());
            copy.put(bb);
            copy.flip();

            if (key != null) {
                key.decipher(copy, copy.position(), copy.limit());
            }

            return copy;
        }

        @Override
        public ByteBuffer encode(ByteBuffer bb, XTEAKey key) throws IOException {
            if(key == null && bb.isReadOnly()) {
                // No need to duplicate the buffer here
                return bb.asReadOnlyBuffer();
            }

            /* Create a deep copy so we don't modify the arguments */
            ByteBuffer copy = ByteBuffer.allocate(bb.remaining());
            int pos = bb.position();
            copy.put(bb);
            bb.position(pos); //Do not modify original buffer
            copy.flip();

            if (key != null) {
                key.encipher(copy, copy.position(), copy.limit());
            }

            return copy;
        }
    },

    /**
     * BZIP compression is used. When decoding, the first 4 bytes of the given buffer are decoded
     * as the length of the decompressed data and are not encrypted. The remaining payload from
     * bb.position() + 4 to bb.limit() is assumed encrypted by the given XTEAKey if it is not null.
     * The payload is to be in BZIP format, minus the two magic byte header ('h' and '1') at the start.
     */
    BZIP(1) {
        @Override
        public ByteBuffer decode(ByteBuffer bb, XTEAKey key) throws IOException {
            ByteBuffer copy = ByteBuffer.allocate(bb.remaining());
            copy.put(bb); //Do modify payload.
            copy.flip();

            if (key != null) {
                key.decipher(copy, copy.position(), copy.limit());
            }

            int decompressedLength = copy.getInt();

            // It seems that the cache uses BZIP, but without using the magic headers 'BZ' ('h1'?) on files BZIP.
            // This means we need to append our own headers to the file so that the BZIP library can parse it without
            // throwing an exception
            byte[] compressed = new byte[copy.remaining() + 2];
            compressed[0] = 'h';
            compressed[1] = '1';
            // Append all the remaining data in the given bytebuffer to the end of the compressed byte[] array
            copy.get(compressed, 2, compressed.length - 2);

            InputStream is = new CBZip2InputStream(new ByteArrayInputStream(compressed));
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream(decompressedLength);
                try {
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        os.write(buf, 0, len);
                    }
                } finally {
                    os.close();
                }

                byte[] inflated = os.toByteArray();
                if (inflated.length != decompressedLength) {
                    throw new IOException("Bad length header for BZIP.decode(). Inflated length: " + inflated.length + ", but decompressedLength " + decompressedLength);
                }

                return ByteBuffer.wrap(inflated);
            } finally {
                is.close();
            }
        }

        @Override
        public ByteBuffer encode(ByteBuffer bb, XTEAKey key) throws IOException {
            InputStream is = new ByteBufferInputStream(bb.asReadOnlyBuffer());
            try {
                // Appears that BZIP has about a 20.2% compression ratio, so we start with an allocated amount for 25% compression
                // GZIP stats: https://catchchallenger.first-world.info/wiki/Quick_Benchmark:_Gzip_vs_Bzip2_vs_LZMA_vs_XZ_vs_LZ4_vs_LZO
                ByteArrayOutputStream bout = new ByteArrayOutputStream(bb.remaining() / 4);
                OutputStream os = new CBZip2OutputStream(bout, 1);
                try {
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        os.write(buf, 0, len);
                    }
                } finally {
                    os.close();
                }

                //BZIP has a header of 2 characters ('h','1'), which the cache files do not contain.
                //Thus the header needs to be stripped away from the newly encoded data.

                byte[] compressed = bout.toByteArray();

                ByteBuffer result = ByteBuffer.allocate(compressed.length - 2 + 4); // -2 to remove header, +4 prepend length
                result.putInt(bb.remaining()); // Number of decompressed bytes
                result.put(compressed, 2, compressed.length - 2);  // Now append the compressed data, minus header

                if (key != null) {
                    key.encipher(result, 0, result.limit());
                }

                result.flip();
                return result;

            } finally {
                is.close();
            }
        }
    },

    GZIP(2) {
        @Override
        public ByteBuffer decode(ByteBuffer bb, XTEAKey key) throws IOException {
            ByteBuffer copy = ByteBuffer.allocate(bb.remaining());
            copy.put(bb); //Do modify payload.
            copy.flip();

            if (key != null) {
                key.decipher(copy, copy.position(), copy.limit());
            }

            int decompressedLength = copy.getInt();

            /* create the streams */
            InputStream is = new GZIPInputStream(new ByteBufferInputStream(copy));
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream(decompressedLength);
                try {
                    /* copy data between the streams */
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        os.write(buf, 0, len);
                    }
                } finally {
                    os.close();
                }

                /* return the uncompressed bytes */
                byte[] inflated = os.toByteArray();
                if (inflated.length != decompressedLength) {
                    throw new IOException("Bad length header for GZIP.decode()");
                }

                return ByteBuffer.wrap(inflated);

            } finally {
                is.close();
            }
        }

        @Override
        public ByteBuffer encode(ByteBuffer bb, XTEAKey key) throws IOException {
            /* Create the streams */
            InputStream is = new ByteBufferInputStream(bb.asReadOnlyBuffer());
            try {
                // Appears that GZIP has about a 27% compression ratio, so we start with an allocated amount for 33% compression
                // GZIP stats: https://catchchallenger.first-world.info/wiki/Quick_Benchmark:_Gzip_vs_Bzip2_vs_LZMA_vs_XZ_vs_LZ4_vs_LZO
                ByteArrayOutputStream bout = new ByteArrayOutputStream(bb.remaining() / 3);
                OutputStream os = new GZIPOutputStream(bout);
                try {
                    /* copy data between the streams */
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        os.write(buf, 0, len);
                    }
                } finally {
                    os.close();
                }

                /* return the compressed bytes */
                byte[] compressed = bout.toByteArray();
                ByteBuffer result = ByteBuffer.allocate(4 + compressed.length);

                result.putInt(bb.remaining());
                result.put(compressed);

                if (key != null) {
                    /* The compressed length is not encrypted */
                    key.encipher(result, 0, result.limit());
                }

                result.flip();
                return result;
            } finally {
                is.close();
            }
        }
    };

    /**
     * The ID of this compression type in the cache.
     */
    private byte id;

    /**
     * Constructs a new compression type. The given ID must
     * be unique in the cache. The client as of r637 only
     * handles types 0(None), 1(BZIP) and 2(GZIP)
     *
     * @param id the unique ID for this zip type
     */
    RSCompression(int id) {
        this.id = (byte) id;
    }

    /**
     * Fetches the compression type for the given compression type
     * ID. This ID is retrieved from the cache or data source,
     * and informs the program of the type of compression used.
     *
     * @param id the unique ID for the compression type
     * @return the compression type
     * @throws IllegalArgumentException if the compression type is not found
     */
    public static RSCompression forId(int id) {
        for (RSCompression c : RSCompression.values()) {
            if (c.id == id) return c;
        }
        throw new IllegalArgumentException("Bad compression type requested: " + id);
    }

    /**
     * Returns the unique ID for this zip type. This ID is the
     * one used in the cache to identify which zip type to use.
     *
     * @return the unique ID.
     */
    public byte getId() {
        return this.id;
    }

    /**
     * Decodes the given ByteBuffer. The buffer is modified after this call.
     *
     * @param bb  the bytebuffer, this will be modified.
     * @param key the XTEA key to use to decrypt, or null if none
     * @return the decrypted and decompressed data, not null, ready to be read from.
     * @throws IOException if an IO error occurs and the data cannot be processed.
     */
    public abstract ByteBuffer decode(ByteBuffer bb, XTEAKey key) throws IOException;

    /**
     * Encoes the given ByteBuffer. The buffer is NOT modified after this call.
     *
     * @param bb  the bytebuffer to encode
     * @param key the XTEA key to use to encrypt, or null if none
     * @return the encrypted and compressed data, not null, ready to be read from.
     * @throws IOException If an IO error occurs and the data cannot be processed.
     */
    public abstract ByteBuffer encode(ByteBuffer bb, XTEAKey key) throws IOException;
}