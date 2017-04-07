package org.maxgamer.rs.cache;

import net.openrs.cache.ChecksumTable;
import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.IndexTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class CacheUtilTest extends CacheTest {
    @Test
    public void testHash() {
        String s = "l5_4";
        int hash = IndexTable.djb2(s);

        Assert.assertEquals("hash must be as expected", 3271358, hash);
    }

    @Test
    public void testChecksumStructure() throws IOException {
        ChecksumTable check = cache.getChecksum();

        // With whirlpool
        ByteBuffer raw = check.encode(true);

        int tables = raw.get() & 0xFF;
        Assert.assertEquals("table count", cache.getIDXCount(), tables);

        for(int i = 0; i < tables; i++) {
            int crc = raw.getInt();
            int version = raw.getInt();
            byte[] whirlpool = new byte[64];
            raw.get(whirlpool);

            try {
                cache.getRaw(255, i);
            } catch (FileNotFoundException e) {
                // File doesn't exist is denoted as 0, 0
                Assert.assertEquals("crc", 0, crc);
                Assert.assertEquals("version", 0, version);
                continue;
            }

            Assert.assertEquals("version", cache.getVersion(i), version);
        }

        Assert.assertEquals("marker", 0, raw.get() & 0xFF);

        byte[] encryptedWhirlpoolChecksum = new byte[64];
        raw.get(encryptedWhirlpoolChecksum);

        // It'd be nice to check the encrypted whirlpool checksum, but we don't here.
        Assert.assertEquals("should've consumed checksum", raw.remaining(), 0);
    }
}
