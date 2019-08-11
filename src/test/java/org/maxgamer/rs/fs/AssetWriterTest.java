package org.maxgamer.rs.fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.codec.RSCompression;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.SubAssetReference;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * @author netherfoam
 */
public class AssetWriterTest {
    private File folder = new File("test_cache");
    private AssetStorage storage;

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
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        }

        folder.mkdir();

        storage = AssetStorage.create(folder);
    }

    @After
    public void destroy() throws IOException {
        storage.close();

        for (File f : folder.listFiles()) {
            if (!f.delete()) throw new IOException("Couldn't delete " + f);
        }
        if (!folder.delete()) throw new IOException("Couldn't delete " + folder);
    }

    @Test
    public void writeBasic() throws IOException {
        // Test that we can write and overwrite an asset
        Asset asset = Asset.create(null, RSCompression.NONE, 1, ByteBuffer.wrap("Hello World".getBytes()));
        AssetReference reference = AssetReference.create(1);

        storage.writer(0)
                .write(0, reference, asset)
                .commit();

        Assert.assertArrayEquals("payloads should match", unbuffer(asset.getPayload()), unbuffer(storage.read(0, 0).getPayload()));

        asset.setPayload(ByteBuffer.wrap("Goodbye World".getBytes()));
        storage.writer(0)
                .write(0, reference, asset)
                .commit();

        Assert.assertArrayEquals("payloads should match", unbuffer(asset.getPayload()), unbuffer(storage.read(0, 0).getPayload()));
    }

    @Test
    public void writeNewVersion() throws IOException {
        // Test that we can skip bothering with the AssetReference, if we want to just reuse the existing one
        Asset asset = Asset.create(null, RSCompression.NONE, 1, ByteBuffer.wrap("Hello World".getBytes()));
        AssetReference reference = AssetReference.create(1);

        storage.writer(0)
                .write(0, reference, asset)
                .commit();

        asset.setPayload(ByteBuffer.wrap("Goodbye World".getBytes()));
        storage.writer(0)
                .write(0, asset)
                .commit();

        // Assert that our files match and our version incremented
        AssetReference newReference = storage.properties(0, 0);
        Assert.assertArrayEquals("payloads should match", unbuffer(asset.getPayload()), unbuffer(storage.read(0, 0).getPayload()));
        Assert.assertTrue("version should increase", newReference.getVersion() > reference.getVersion());

        storage.writer(0)
                .write(1, asset)
                .commit();

        // Assert that our payloads match and our version is > 0
        Assert.assertArrayEquals("payloads should match", unbuffer(asset.getPayload()), unbuffer(storage.read(0, 1).getPayload()));
        Assert.assertTrue("version should increase", newReference.getVersion() > 0);
    }

    @Test
    public void writeMultiAsset() throws IOException {
        SubAssetReference child = new SubAssetReference(5, 0);
        AssetReference reference = AssetReference.create(1, child);
        MultiAsset multi = new MultiAsset(reference);
        ByteBuffer content = ByteBuffer.wrap("Hello World".getBytes());
        multi.put(5, content);

        storage.writer(0)
                .write(0, multi)
                .commit();

        multi = storage.archive(0, 0);
        Assert.assertArrayEquals("expect content to match", unbuffer(content), unbuffer(multi.get(5)));

        // Now we test overwriting it
        content = ByteBuffer.wrap("Goodbye World".getBytes());
        multi.put(5, content);

        storage.writer(0)
                .write(0, multi)
                .commit();

        multi = storage.archive(0, 0);

        Assert.assertArrayEquals("expect content to match", unbuffer(content), unbuffer(multi.get(5)));
    }

    @Test
    public void writeSubAsset() throws IOException {
        // Write: create it in the cache
        ByteBuffer content = ByteBuffer.wrap("Hello World".getBytes());
        storage.writer(0)
                .write(0, 5, content)
                .commit();

        Assert.assertArrayEquals("Expect content to match", unbuffer(content), unbuffer(storage.archive(0, 0).get(5)));

        // Write: re-create it in the cache
        content.position(0);
        storage.writer(0)
                .write(0, 5, content)
                .commit();

        Assert.assertArrayEquals("Expect content to match", unbuffer(content), unbuffer(storage.archive(0, 0).get(5)));

        // Write: create two in the cache
        ByteBuffer brother = ByteBuffer.wrap("I am Brother".getBytes());
        ByteBuffer sister = ByteBuffer.wrap("I am Sister".getBytes());
        storage.writer(0)
                .write(1, 7, brother)
                .write(1, 8, sister)
                .commit();

        Assert.assertArrayEquals("Expect content to match", unbuffer(brother), unbuffer(storage.archive(0, 1).get(7)));
        Assert.assertArrayEquals("Expect content to match", unbuffer(sister), unbuffer(storage.archive(0, 1).get(8)));
    }

    @Test
    public void deleteSubAsset() throws IOException {
        // Write: create it in the cache
        ByteBuffer content = ByteBuffer.wrap("Hello World".getBytes());
        storage.writer(0)
                .write(0, 5, content)
                .commit();

        storage.writer(0)
                .delete(0, 5)
                .commit();

        Assert.assertNull("Expect child to be erased", storage.archive(0, 0).get(5));
    }

    @Test
    public void deleteQueuedWriteSubAsset() throws IOException {
        ByteBuffer content = ByteBuffer.wrap("Hello World".getBytes());
        storage.writer(0)
                .write(0, 5, content) // Write file
                .delete(0, 5) // Actually nevermind
                .commit();

        Assert.assertNull("Expect child not to be written", storage.archive(0, 0).get(5));
    }
}
