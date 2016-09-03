package org.maxgamer.rs.cache;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class XTEAKey {
    /**
     * The golden ratio.
     */
    public static final int GOLDEN_RATIO = 0x9E3779B9;

    /**
     * The number of rounds.
     */
    public static final int ROUNDS = 32;

    private int[] keys;

    public XTEAKey(int[] keys) {
        if (keys == null) throw new NullPointerException("Keys may not be null");
        if (keys.length != 4) throw new IllegalArgumentException("Keys.length must be 4");

        this.keys = keys;
    }

    public int[] getKeys() {
        return keys.clone();
    }

    /**
     * Returns true if all values in this key are 0. Eg no key is required. Decrypt and encrypt
     * methods do nothing.
     *
     * @return true if all values are 0. eg, null key representation.
     */
    private boolean isZeroed() {
        for (int v : keys) {
            if (v != 0) return false;
        }
        return true;
    }

    /**
     * Deciphers the specified {@link ByteBuffer} with the given key.
     *
     * @param buffer The buffer.
     * @param keys   The key.
     * @throws IllegalArgumentException if the key is not exactly 4 elements
     *                                  long.
     */
    public void decipher(ByteBuffer buffer, int start, int end) {
        if (isZeroed()) return;

        int numQuads = (end - start) / 8;
        for (int i = 0; i < numQuads; i++) {
            int sum = GOLDEN_RATIO * ROUNDS;
            int v0 = buffer.getInt(start + i * 8);
            int v1 = buffer.getInt(start + i * 8 + 4);
            for (int j = 0; j < ROUNDS; j++) {
                v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + keys[(sum >>> 11) & 3]);
                sum -= GOLDEN_RATIO;
                v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + keys[sum & 3]);
            }
            buffer.putInt(start + i * 8, v0);
            buffer.putInt(start + i * 8 + 4, v1);
        }
    }

    /**
     * Enciphers the specified {@link ByteBuffer} with the given key.
     *
     * @param buffer The buffer.
     * @param keys   The key.
     * @throws IllegalArgumentException if the key is not exactly 4 elements
     *                                  long.
     */
    public void encipher(ByteBuffer buffer, int start, int end) {
        if (isZeroed()) return;

        int numQuads = (end - start) / 8;
        for (int i = 0; i < numQuads; i++) {
            int sum = 0;
            int v0 = buffer.getInt(start + i * 8);
            int v1 = buffer.getInt(start + i * 8 + 4);
            for (int j = 0; j < ROUNDS; j++) {
                v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + keys[sum & 3]);
                sum += GOLDEN_RATIO;
                v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + keys[(sum >>> 11) & 3]);
            }
            buffer.putInt(start + i * 8, v0);
            buffer.putInt(start + i * 8 + 4, v1);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(keys);
    }
}