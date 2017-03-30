package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.cache.RSCompression;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class CompressionTest {
    @Test
    public void testAll() throws IOException {
        byte[] expected = "Hello World".getBytes();

        for(RSCompression compression : RSCompression.values()) {
            ByteBuffer encoded = compression.encode(ByteBuffer.wrap(expected), null);
            ByteBuffer decoded = compression.decode(encoded, null);

            Assert.assertEquals("expect same size", expected.length, decoded.remaining());

            byte[] result = new byte[decoded.remaining()];
            decoded.get(result);

            Assert.assertArrayEquals("must match source", expected, result);
        }
    }
}
