package org.maxgamer.rs.fs;

import net.openrs.util.crypto.Whirlpool;
import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * @author netherfoam
 */
public class ByteBufferHashTest {
    private byte[] data(int size) {
        Random r = new Random(0);
        byte[] data = new byte[size];
        r.nextBytes(data);

        return data;
    }

    @Test
    public void whirlpool() {
        byte[] source = data(1024 * 10 + 82); // 10kb of data to hash

        byte[] buffered = AssetWriter.whirlpool(ByteBuffer.wrap(source));
        byte[] standard = Whirlpool.whirlpool(source, 0, source.length);

        Assert.assertArrayEquals("expect exact same hash from same methods", standard, buffered);
    }

    @Test
    public void crc32() {
        byte[] source = data(1024 * 10 + 82); // 10kb of data to hash

        CRC32 crc32 = new CRC32();
        crc32.update(source);

        int buffered = AssetWriter.crc32(ByteBuffer.wrap(source));
        int standard = (int) crc32.getValue();

        Assert.assertEquals("expect exact same hash from different crc32 usages", standard, buffered);
    }

    @Test
    public void emptyCrc32() {
        byte[] source = new byte[0]; // Empty amount of data to hash

        CRC32 crc32 = new CRC32();
        crc32.update(source);

        int buffered = AssetWriter.crc32(ByteBuffer.wrap(source));
        int standard = (int) crc32.getValue();

        Assert.assertEquals("expect exact same hash from different crc32 usages", standard, buffered);
    }
}
