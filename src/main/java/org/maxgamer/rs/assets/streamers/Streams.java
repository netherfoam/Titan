package org.maxgamer.rs.assets.streamers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author netherfoam
 */
public class Streams {
    public static final Encoder<String> STRING = new Encoder<String>() {
        @Override
        public void encode(ByteArrayOutputStream out, String value) throws IOException {
            if (value.indexOf((char) 0) >= 0) {
                throw new IllegalArgumentException("The given string may not contain a NUL (byte 0) character");
            }

            out.write(value.getBytes());
            out.write((byte) 0);
        }
    };

    public static final Encoder<String> NULL_PREFIX_STRING = new Encoder<String>() {
        @Override
        public void encode(ByteArrayOutputStream out, String value) throws IOException {
            if (value.indexOf((char) 0) >= 0) {
                throw new IllegalArgumentException("The given string may not contain a NUL (byte 0) character");
            }


            out.write((byte) 0);
            STRING.encode(out, value);
        }
    };

    public static final Encoder<byte[]> bytes(final int numLengthBytes) {
        return new Encoder<byte[]>() {
            @Override
            public void encode(ByteArrayOutputStream out, byte[] value) throws IOException {
                writeBytes(out, numLengthBytes, value.length);

                out.write(value);
            }
        };
    }

    public static final Encoder<short[]> shorts(final int numLengthBytes) {
        return new Encoder<short[]>() {
            @Override
            public void encode(ByteArrayOutputStream out, short[] value) throws IOException {
                writeBytes(out, numLengthBytes, value.length);

                for(short s : value) {
                    out.write(s >> 8);
                    out.write(s);
                }
            }
        };
    }

    public static final Encoder<int[]> tribytes(final int numLengthBytes) {
        return new Encoder<int[]>() {
            @Override
            public void encode(ByteArrayOutputStream out, int[] value) throws IOException {
                writeBytes(out, numLengthBytes, value.length);

                for(int s : value) {
                    out.write(s >> 16);
                    out.write(s >> 8);
                    out.write(s);
                }
            }
        };
    }

    public static final Encoder<int[]> ints(final int numLengthBytes) {
        return new Encoder<int[]>() {
            @Override
            public void encode(ByteArrayOutputStream out, int[] value) throws IOException {
                writeBytes(out, numLengthBytes, value.length);

                for(int s : value) {
                    out.write(s >> 24);
                    out.write(s >> 16);
                    out.write(s >> 8);
                    out.write(s);
                }
            }
        };
    }

    public static final Encoder<long[]> longs(final int numLengthBytes) {
        return new Encoder<long[]>() {
            @Override
            public void encode(ByteArrayOutputStream out, long[] value) throws IOException {
                writeBytes(out, numLengthBytes, value.length);

                for(long s : value) {
                    out.write((byte) (s >> 56));
                    out.write((byte) (s >> 48));
                    out.write((byte) (s >> 40));
                    out.write((byte) (s >> 32));

                    out.write((byte) (s >> 24));
                    out.write((byte) (s >> 16));
                    out.write((byte) (s >> 8));
                    out.write((byte) (s));
                }
            }
        };
    }

    protected static void writeBytes(ByteArrayOutputStream out, int numBytes, long value) {
        for(int i = numBytes - 1; i >= 0; i--) {
            byte v = (byte) ((value >> (numBytes * 8)) & 0xFF);

            out.write(v);
        }
    }
}
