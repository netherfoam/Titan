package org.maxgamer.rs.fs;

import net.openrs.cache.ChecksumTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;

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

        storage.getProtocol().rebuildChecksum();
        ChecksumTable table = storage.getProtocol().getChecksum();
        ChecksumTable.Entry entry = table.getEntry(0);

        ByteBuffer encodedIndexTable = storage.getMasterTable().read(0);
        Assert.assertEquals("expect correct crc32", entry.getCrc(), AssetWriter.crc32(encodedIndexTable));
    }
}
