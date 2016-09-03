package net.openrs.util;

import net.openrs.util.crypto.Whirlpool;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Contains {@link ByteBuffer}-related utility methods.
 *
 * @author Graham
 * @author `Discardedx2
 */
public final class ByteBufferUtils {

    /**
     * The modified set of 'extended ASCII' characters used by the client.
     */
    private static char CHARACTERS[] = {'\u20AC', '\0', '\u201A', '\u0192',
            '\u201E', '\u2026', '\u2020', '\u2021', '\u02C6', '\u2030',
            '\u0160', '\u2039', '\u0152', '\0', '\u017D', '\0', '\0', '\u2018',
            '\u2019', '\u201C', '\u201D', '\u2022', '\u2013', '\u2014',
            '\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\0', '\u017E',
            '\u0178'};

    /**
     * Default private constructor to prevent instantiation.
     */
    private ByteBufferUtils() {

    }

    /**
     * Performs a MD5 hash on the given bytes and converts the resultant hash into a 32 digit long hex number,
     * prefied with 0x.
     *
     * @param data the data to fingerprint
     * @return a (hopefully) unique identifer for the given data.
     */
    public static String fingerprint(byte[] data) {
        MessageDigest md5;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 MessageDigest not found!", e);
        }

        byte[] result = md5.digest(data);
        StringBuilder builder = new StringBuilder(2 + result.length * 2);
        builder.append("0x");
        for (int i = 0; i < result.length; i++) {
            String hex = Integer.toHexString(result[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    /**
     * Gets a null-terminated string from the specified buffer, using a
     * modified ISO-8859-1 character set.
     *
     * @param buf The buffer.
     * @return The decoded string.
     */
    public static String getJagexString(ByteBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        int b;
        while ((b = buf.get()) != 0) {
            if (b >= 127 && b < 160) {
                char curChar = CHARACTERS[b - 128];
                if (curChar != 0) {
                    bldr.append(curChar);
                }
            } else {
                bldr.append((char) b);
            }
        }
        return bldr.toString();
    }

    /**
     * Reads a 'tri-byte' from the specified buffer.
     *
     * @param buf The buffer.
     * @return The value.
     */
    public static int getTriByte(ByteBuffer buf) {
        return ((buf.get() & 0xFF) << 16) | ((buf.get() & 0xFF) << 8) | (buf.get() & 0xFF);
    }

    /**
     * Writes a 'tri-byte' to the specified buffer.
     *
     * @param buf   The buffer.
     * @param value The value.
     */
    public static void putTriByte(ByteBuffer buf, int value) {
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
    }

    /**
     * Calculates the CRC32 checksum of the specified buffer.
     *
     * @param buffer The buffer.
     * @return The CRC32 checksum.
     */
    public static int getCrcChecksum(ByteBuffer buffer) {
        Checksum crc = new CRC32();
        for (int i = 0; i < buffer.limit(); i++) {
            crc.update(buffer.get(i));
        }
        return (int) crc.getValue();
    }

    /**
     * Calculates the whirlpool digest of the specified buffer.
     *
     * @param buf The buffer.
     * @return The 64-byte whirlpool digest.
     */
    public static byte[] getWhirlpoolDigest(ByteBuffer buf) {
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        return Whirlpool.whirlpool(bytes, 0, bytes.length);
    }

    /**
     * Converts the contents of the specified byte buffer to a string, which is
     * formatted similarly to the output of the {@link Arrays#toString()}
     * method.
     *
     * @param buffer The buffer.
     * @return The string.
     */
    public static String toString(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < buffer.limit(); i++) {
            String hex = Integer.toHexString(buffer.get(i) & 0xFF).toUpperCase();
            if (hex.length() == 1)
                hex = "0" + hex;

            builder.append("0x").append(hex);
            if (i != buffer.limit() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

}
