package org.maxgamer.rs.cache;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.RSCompression;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Tests which check the results of {@link Cache#createResponse(int, int, int)} - data that is directly sent to the client.
 *
 * These are more of an integration style test which should read data the same way the client does.
 *
 * @author netherfoam
 */
public class CacheNetworkTest extends CacheTest {
    @Test
    public void checkChecksum() throws IOException {
        ByteBuffer response = cache.createResponse(255, 255, 1);

        Assert.assertEquals("idx", 255, response.get() & 0xFF);
        Assert.assertEquals("fileId", 255, response.getShort() & 0xFFFF);

        int attributes = response.get() & 0xFF;
        Assert.assertEquals("must not have compression in idx", (attributes & 0x01), RSCompression.NONE.getId());

        int length = response.getInt();

        for(int i = 0; i < length; i++) {
            if(response.position() % 512 == 0) {
                Assert.assertEquals("must have magic byte marker 0xFF", 0xFF, response.get() & 0xFF);
            }

            // Data read
            response.get();
        }

        Assert.assertEquals("expect no more data present", 0, response.remaining());
    }

    @Test
    public void checkIndices() throws IOException {
        for(int idx = 0; idx < cache.getIDXCount(); idx++) {
            // Here, we read all of the files from the master table. These files in the master table
            // correspond to metadata about the other tables. Therefore, idx is a file id inside the
            // master table.

            ByteBuffer response = null;
            try {
                response = cache.createResponse(255, idx, 1);
            } catch (FileNotFoundException original) {
                // Skipped, we check this later
            }

            ByteBuffer raw = null;
            try {
                raw = cache.getRaw(255, idx);
            } catch (FileNotFoundException ignored) {
                // Skipped, we check this later
            }

            if(raw == null && response == null) {
                // File doesn't exist
                continue;
            }
            else if((raw == null) != (response == null)) {
                // One of the checks threw FileNotFound,
                Assert.fail("FileNotFound for one, but not the other?");
                return;
            }

            Assert.assertEquals("idx", 255, response.get() & 0xFF);
            Assert.assertEquals("fileId", idx, response.getShort() & 0xFFFF);

            RSCompression compression = RSCompression.forId(response.get() & 0xFF);
            Assert.assertEquals("compression byte", raw.get(), compression.getId());

            int length = response.getInt();

            if (compression == RSCompression.BZIP || compression == RSCompression.GZIP) {
                // Compression implies that we also have an extra 4 bytes which store the length
                // inside of the file
                length += 4;
            }

            for (int i = 0; i < length; i++) {
                if (response.position() % 512 == 0) {
                    Assert.assertEquals("must have magic byte marker 0xFF", 0xFF, response.get() & 0xFF);
                }

                // We can't directly compare them, in case our compression algorithm was different
                response.get();
            }

            Assert.assertEquals("expect no more data present", 0, response.remaining());
        }
    }

    @Test
    public void testAllFiles() throws IOException {
        for(int idx = 0; idx < cache.getIDXCount(); idx++) {
            testAllFilesInTable(idx);
        }
    }

    public void testAllFilesInTable(int idx) throws IOException {
        // Here, we read all of the files from the given  file table
        int size;
        try {
            size = cache.getSize(idx);
        } catch (FileNotFoundException e) {
            return;
        }

        if (size <= 0) return;

        for (int fileId = 0; fileId < size; fileId++) {
            ByteBuffer response = null;
            try {
                response = cache.createResponse(idx, fileId, 1);
            } catch (FileNotFoundException original) {
                // Skipped, we check this later
            }

            ByteBuffer raw = null;
            try {
                raw = cache.getRaw(idx, fileId);
            } catch (FileNotFoundException ignored) {
                // Skipped, we check this later
            }

            if (raw == null && response == null) {
                // File doesn't exist
                return;
            } else if ((raw == null) != (response == null)) {
                // One of the checks threw FileNotFound,
                Assert.fail("FileNotFound for one, but not the other?");
                return;
            }

            Assert.assertEquals("idx", idx, response.get() & 0xFF);
            Assert.assertEquals("fileId", fileId, response.getShort() & 0xFFFF);

            RSCompression compression = RSCompression.forId(response.get() & 0xFF);
            Assert.assertEquals("compression byte", raw.get(), compression.getId());

            int length = response.getInt();
            Assert.assertEquals("raw length", raw.getInt(), length);

            if (compression == RSCompression.BZIP || compression == RSCompression.GZIP) {
                // Compression implies that we also have an extra 4 bytes which store the length
                // inside of the file
                length += 4;
            }

            for (int i = 0; i < length; i++) {
                if (response.position() % 512 == 0) {
                    Assert.assertEquals("must have magic byte marker 0xFF", 0xFF, response.get() & 0xFF);
                }

                // Assert that the data we get as a response is what we've got stored in the cache
                Assert.assertEquals("raw", raw.get() & 0xFF, response.get() & 0xFF);
            }

            Assert.assertEquals("expect no more data present", 0, response.remaining());
        }
    }
}
