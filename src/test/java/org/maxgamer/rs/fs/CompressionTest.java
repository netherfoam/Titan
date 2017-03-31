package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.RSCompression;
import org.maxgamer.rs.assets.codec.asset.XTEAKey;

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

    @Test
    public void dontTouchMyBuffer() throws IOException {
        // A test that makes sure we don't modify the input buffers for any of the encode()/decode() methods
        // Use a combination of XTEA keys (null/not null), mutable buffers and read-only buffers
        ByteBuffer plsDontTouch = ByteBuffer.wrap("Hello World".getBytes());
        final int POS = plsDontTouch.position();
        final int LIMIT = plsDontTouch.limit();

        // Some trivial XTEA key
        XTEAKey key = new XTEAKey(new int[]{0x01, 0x02, 0x03, 0x04});

        for(RSCompression compression : RSCompression.values()) {
            // No XTEA key: Should just take a read-only copy and use that
            compression.encode(plsDontTouch, null);
            Assert.assertEquals("must not change position", POS, plsDontTouch.position());
            Assert.assertEquals("must not change limit", LIMIT, plsDontTouch.limit());

            // With XTEA key: will need to take a mutable copy and use that
            compression.encode(plsDontTouch, key);
            Assert.assertEquals("must not change position", POS, plsDontTouch.position());
            Assert.assertEquals("must not change limit", LIMIT, plsDontTouch.limit());

            // No XTEA key, but read only: Should just take a read-only copy
            ByteBuffer plsDontTouchReadOnly = plsDontTouch.asReadOnlyBuffer();
            compression.encode(plsDontTouchReadOnly, null);
            Assert.assertEquals("must not change position", POS, plsDontTouchReadOnly.position());
            Assert.assertEquals("must not change limit", LIMIT, plsDontTouchReadOnly.limit());

            // With XTEA key and read only: Should take a mutable copy and use that
            compression.encode(plsDontTouchReadOnly, key);
            Assert.assertEquals("must not change position", POS, plsDontTouchReadOnly.position());
            Assert.assertEquals("must not change limit", LIMIT, plsDontTouchReadOnly.limit());
        }
    }
}
