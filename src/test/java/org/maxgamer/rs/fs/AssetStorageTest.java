package org.maxgamer.rs.fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.AssetStorage;

import java.io.File;
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
        folder.delete();
        folder.deleteOnExit();
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
}
