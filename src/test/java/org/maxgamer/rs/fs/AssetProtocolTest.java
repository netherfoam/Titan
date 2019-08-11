package org.maxgamer.rs.fs;

import net.openrs.cache.ChecksumTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.codec.RSCompression;
import org.maxgamer.rs.assets.codec.asset.*;
import org.maxgamer.rs.assets.protocol.AssetProtocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * @author netherfoam
 */
public class AssetProtocolTest {
    private File root = new File("tmp");
    private AssetStorage storage;

    @Before
    public void init() throws IOException {
        root.mkdir();
        storage = new AssetStorage(root);
        root.deleteOnExit();

        // Initialize the cache with three fairly different files.
        // First, a simple, uncompressed file of small size, whose file id will be 0
        Asset first = Asset.create(null, RSCompression.NONE, 1, ByteBuffer.wrap(data(150)));

        // Second, a compressed file of small size, whose file id will be 2 (eg. we skip file id 1)
        Asset second = Asset.create(null, RSCompression.BZIP, 1, ByteBuffer.wrap(data(500)));

        // Third, a multi asset file, who will be in idx=2 instead of idx=0, at fileId=5
        SubAssetReference brother = new SubAssetReference(0, 0);
        SubAssetReference sister = new SubAssetReference(0, 0);
        AssetReference multiRef = AssetReference.create(1, brother, sister);
        MultiAsset multi = new MultiAsset(multiRef);
        multi.put(0, ByteBuffer.wrap("I am Brother".getBytes()));
        multi.put(1, ByteBuffer.wrap("I am Sister".getBytes()));
        Asset third = Asset.create(null, RSCompression.GZIP, 1, multi.encode());

        storage.writer(0)
                .write(0, AssetReference.create(1), first)
                .write(2, AssetReference.create(1), second)
                .commit();

        storage.writer(2)
                .write(5, multiRef, third)
                .commit();
    }

    @After
    public void destroy() throws IOException {
        storage.close();

        for(File f : root.listFiles()) {
            if (!f.delete()) throw new IOException("Couldn't delete " + f);
        }
        if (!root.delete()) throw new IOException("Couldn't delete " + root);
    }

    @Test
    public void testChecksumParity() throws IOException {
        ChecksumTable expect = storage.getProtocol().getChecksum();
        ChecksumTable result = ChecksumTable.decode(expect.encode(true), true);

        Assert.assertEquals("Expect checksum to maintain size", expect.getSize(), result.getSize());

        for(int i = 0; i < expect.getSize(); i++) {
            ChecksumTable.Entry eentry = expect.getEntry(i);
            ChecksumTable.Entry rentry = result.getEntry(i);

            Assert.assertNotNull("expected entry can't be null", eentry);
            Assert.assertNotNull("result entry can't be null", rentry);

            Assert.assertEquals("whirlpool must be 64 bytes", 64, eentry.getWhirlpool().length);
            Assert.assertEquals("whirlpool must be 64 bytes", 64, rentry.getWhirlpool().length);

            Assert.assertArrayEquals("whirlpools must match", eentry.getWhirlpool(), rentry.getWhirlpool());
            Assert.assertEquals("crcs must match", eentry.getCrc(), rentry.getCrc());
            Assert.assertEquals("versions must match", eentry.getVersion(), rentry.getVersion());

            // Now we assert that the CRC and Checksums are correct
            try {
                IndexTable index = storage.getIndex(i);
                Asset asset = index.toAsset();
                ByteBuffer compressed = asset.encode();

                Assert.assertEquals("crc must match calculated value", AssetWriter.crc32(compressed), eentry.getCrc());
                Assert.assertArrayEquals("whirlpool must match calculated value", AssetWriter.whirlpool(compressed), eentry.getWhirlpool());
            } catch (FileNotFoundException e) {
                Assert.assertEquals("crc must match calculated value", 0, eentry.getCrc());
                Assert.assertArrayEquals("whirlpool must match calculated value", AssetWriter.whirlpool(ByteBuffer.wrap(new byte[0])), eentry.getWhirlpool());
            }
        }
    }

    @Test
    public void testMasterResponse() throws IOException {
        // Read our checksum file and assert that it agrees with everything we'd hope
        ByteBuffer data = retrieveAndValidate(255, 255, 0);
        Assert.assertEquals("compression byte", 0, data.get());

        byte[] expect = unbuffer(storage.getProtocol().getChecksum().encode(true));
        byte[] result = unbuffer(data);

        Assert.assertArrayEquals("checksum file", expect, result);
    }

    private ByteBuffer retrieveAndValidate(int idx, int file, int opcode) throws IOException {
        ByteBuffer response = storage.getProtocol().response(idx, file, opcode);

        Assert.assertEquals("idx", idx, response.get() & 0xFF);
        Assert.assertEquals("file", file, response.getShort() & 0xFFFF);

        byte attributes = response.get();
        if(opcode == 0) {
            Assert.assertEquals("attributes", AssetProtocol.PRIORITY_FLAG, attributes & AssetProtocol.PRIORITY_FLAG);
        } else {
            Assert.assertEquals("attributes", 0, attributes & AssetProtocol.PRIORITY_FLAG);
        }

        // Remove the priority flag. Leaves us with compression
        attributes &= ~AssetProtocol.PRIORITY_FLAG;

        RSCompression compression = RSCompression.forId(attributes);
        Assert.assertNotNull("compression", compression);
        int length = response.getInt();

        ByteBuffer data;

        if(compression != RSCompression.NONE) {
            // Plus 9 because we need to length twice:
            // one byte for the opcode
            // one byte for the  length of the content
            // one byte for the length of the decompressed size

            data = ByteBuffer.allocate(length + 9); // +4 is the length (int) of the decompressed data
            data.put(compression.getId());
            data.putInt(length);
        } else {
            data = ByteBuffer.allocate(length + 5);
            data.put(compression.getId());
            if(opcode == 1) {
                // TODO: Shouldn't this relate to Attributes? Potential bug that the client discards
                data.putInt(length);
            }
        }

        while(response.hasRemaining()) {
            if(response.position() % 512 == 0) {
                Assert.assertEquals("magic 0xFF marker", 0xFF, response.get() & 0xFF);
            }

            data.put(response.get());
        }
        data.flip();

        return data;
    }

    @Test
    public void testSafeErrors() {
        // TODO Request idx=255 with some crazy file numbers
        // TODO Request with some crazy idx numbers
        // TODO Request idx=0 with some crazy file numbers
        // TODO Request with some crazy priority opcode
    }

    @Test
    public void testIndexResponse() throws IOException {
        // read each index file, assert that the checksum matches the read file
        ChecksumTable checksum = storage.getProtocol().getChecksum();
        for(int idx = 0; idx < checksum.getSize(); idx++) {
            ChecksumTable.Entry entry = checksum.getEntry(idx);
            if(entry.getCrc() == 0) continue;

            ByteBuffer response = retrieveAndValidate(255, idx, 0);
            Assert.assertEquals("crc", entry.getCrc(), AssetWriter.crc32(response));
            Assert.assertArrayEquals("whirlpool", entry.getWhirlpool(), AssetWriter.whirlpool(response));
        }
    }

    @Test
    public void testDataResponse() throws IOException {
        // Read each data file, assert that every file referenced by index tables can be fetched
        for(int idx = 0; idx < storage.size(); idx++) {
            IndexTable table;
            try {
                table = storage.getIndex(idx);
            } catch (FileNotFoundException e) {
                continue;
            }

            for(int file : table.getReferences().keySet()) {
                ByteBuffer response = retrieveAndValidate(idx, file, 1);
                AssetReference reference = table.getReferences().get(file);
                Assert.assertNotNull(reference);

                Asset asset = new Asset(null, response.asReadOnlyBuffer());
                Assert.assertArrayEquals(unbuffer(storage.read(idx, file).getPayload()), unbuffer(asset.getPayload()));

                // Now we assert that our checksums and whirlpools of the raw encoded content match what is expected.
                int limit = response.limit();
                if(asset.getVersion() != -1) {
                    // We trim off the version, that's not part of the checksum
                    response.limit(response.limit() - 2);
                }

                Assert.assertEquals("crc32", reference.getCRC(), AssetWriter.crc32(response));
                Assert.assertArrayEquals("whirlpool", reference.getWhirlpool(), AssetWriter.whirlpool(response));

                if(asset.getVersion() != -1) {
                    // Restore the limit
                    response.position(limit);
                }
            }
        }
    }

    private byte[] data(int size) {
        Random r = new Random(0);
        byte[] data = new byte[size];
        r.nextBytes(data);

        return data;
    }

    private byte[] unbuffer(ByteBuffer buffer) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        return data;
    }
}
