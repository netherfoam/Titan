package org.maxgamer.rs.fs;

import net.openrs.cache.ChecksumTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.CachedAssetStorage;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.codec.RSCompression;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.SubAssetReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * @author netherfoam
 */
public class AssetStorageTest {
    private File folder = new File("test_cache");

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

    @Before
    public void init() throws IOException {
        if(folder.exists()) {
            for(File file : folder.listFiles()) {
                file.delete();
            }
        }

        folder.mkdir();
    }

    @After
    public void destroy() throws IOException {
        for(File f : folder.listFiles()) {
            if(!f.delete()) f.deleteOnExit();
        }
        if(!folder.delete()) folder.deleteOnExit();
    }

    @Test
    public void testCreate() throws IOException {
        AssetStorage storage = AssetStorage.create(folder);

        AssetReference properties = AssetReference.create(0);
        byte[] expected = "Hello World".getBytes();

        storage.writer(0)
                .write(1, properties, Asset.wrap(expected))
                .commit();

        ByteBuffer read = storage.read(0,1).getPayload();
        byte[] result = new byte[read.remaining()];
        read.get(result);

        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void testMultiCreate() throws IOException {
        AssetStorage storage = AssetStorage.create(folder);

        AssetReference properties = AssetReference.create(0);
        byte[] first = "Hello World".getBytes();
        byte[] second = "Goodbye World".getBytes();

        storage.writer(0)
                .write(0, properties, Asset.wrap(first))
                .commit();

        storage.writer(1)
                .write(0, properties, Asset.wrap(second))
                .commit();

        ByteBuffer read = storage.read(0, 0).getPayload();
        byte[] result = new byte[read.remaining()];
        read.get(result);

        Assert.assertArrayEquals(first, result);

        read = storage.read(1, 0).getPayload();
        result = new byte[read.remaining()];
        read.get(result);

        Assert.assertArrayEquals(second, result);
    }

    @Test
    public void testMultiCreateDelete() throws IOException {
        AssetStorage storage = AssetStorage.create(folder);

        AssetReference properties = AssetReference.create(0);
        byte[] first = "Hello World".getBytes();
        byte[] second = "Goodbye World".getBytes();

        storage.writer(0)
                .write(0, properties, Asset.wrap(first))
                .commit();

        storage.writer(0)
                .write(1, properties, Asset.wrap(second))
                .delete(0)
                .commit();

        try {
            storage.read(0, 0);
            Assert.fail("Expected NotFoundException");
        } catch (FileNotFoundException expected) {
            // Great!
        }

        ByteBuffer read = storage.read(0, 1).getPayload();
        byte[] result = new byte[read.remaining()];
        read.get(result);

        Assert.assertArrayEquals(second, result);
    }

    @Test
    public void testMultiCreateDeleteChecksum() throws IOException {
        AssetStorage storage = AssetStorage.create(folder);

        AssetReference properties = AssetReference.create(0);
        byte[] first = "Hello World".getBytes();
        byte[] second = "Goodbye World".getBytes();

        storage.writer(0)
                .write(0, properties, Asset.wrap(first))
                .commit();

        storage.writer(0)
                .write(1, properties, Asset.wrap(second))
                .delete(0)
                .commit();

        try {
            storage.read(0, 0);
            Assert.fail("Expected NotFoundException");
        } catch (FileNotFoundException expected) {
            // Great!
        }

        ByteBuffer read = storage.read(0, 1).getPayload();
        byte[] result = new byte[read.remaining()];
        read.get(result);

        Assert.assertArrayEquals(second, result);

        ChecksumTable table = storage.getProtocol().getChecksum();
        ChecksumTable.Entry entry = table.getEntry(0);

        ByteBuffer encodedIndexTable = storage.getMasterTable().read(0);
        Assert.assertEquals("expect correct crc32", entry.getCrc(), AssetWriter.crc32(encodedIndexTable));
    }

    @Test
    public void testIndexFilesHaveNoVersion() throws IOException {
        Asset asset = Asset.wrap("Hello World".getBytes());
        asset.setVersion(501);

        AssetReference ref = AssetReference.create(501);

        AssetStorage storage = AssetStorage.create(folder);
        storage.writer(0)
                .write(0, ref, asset)
                .commit();

        // Files written to disk should keep their version
        Asset result = storage.read(0, 0);
        Assert.assertEquals("expect asset to keep version", asset.getVersion(), result.getVersion());
        Assert.assertEquals("expect reference to keep version", ref.getVersion(), storage.properties(0, 0).getVersion());

        // But if we read back the master file, there should be no version attached to it.
        Asset indexFile = new Asset(null, storage.getMasterTable().read(0));
        Assert.assertEquals(-1, indexFile.getVersion());
    }

    @Test
    public void testCachedAssetStorage() throws IOException {
        SubAssetReference child = new SubAssetReference(0, 0);
        AssetReference properties = AssetReference.create(1, child);
        MultiAsset multi = new MultiAsset(properties);

        byte[] data = "Hello World".getBytes();
        multi.put(0, ByteBuffer.wrap(data));

        AssetReference ref = AssetReference.create(1, child);
        CachedAssetStorage storage = (CachedAssetStorage) CachedAssetStorage.create(folder);

        try {
            storage.archive(0, 0);
            Assert.fail("Expected file not found");
        } catch (FileNotFoundException e) {
            // Great! File is missing
        }

        storage.writer(0)
                .write(0, ref, Asset.create(null, RSCompression.NONE, -1, multi.encode()))
                .commit();

        MultiAsset result = storage.archive(0, 0);
        Assert.assertNotNull("Result may not be null", result);
        if(result == multi) Assert.fail("Expected a different object reference");

        Assert.assertArrayEquals("ByteBuffer must be the same", unbuffer(multi.get(0)), unbuffer(result.get(0)));

        multi.put(0, ByteBuffer.wrap("Goodbye World".getBytes()));
        storage.writer(0)
                .write(0, ref, Asset.create(null, RSCompression.NONE, -1, multi.encode()))
                .commit();

        result = storage.archive(0, 0);
        Assert.assertNotNull("Result may not be null", result);
        Assert.assertArrayEquals("Goodbye World".getBytes(), unbuffer(result.get(0)));
    }

    @Test
    public void testNoCrossContamination() throws IOException {
        CachedAssetStorage cached = (CachedAssetStorage) CachedAssetStorage.create(folder);
        cached.writer(0)
                .write(1, 1, ByteBuffer.wrap("first".getBytes()))
                .commit();

        cached.writer(1)
                .write(1, 1, ByteBuffer.wrap("second".getBytes()))
                .commit();

        ByteBuffer b1 = cached.archive(0, 1).get(1);
        ByteBuffer b2 = cached.archive(1, 1).get(1);

        byte[] first = unbuffer(b1);
        byte[] second = unbuffer(b2);

        Assert.assertArrayEquals("expect first", "first".getBytes(), first);
        Assert.assertArrayEquals("expect second", "second".getBytes(), second);
    }
}
