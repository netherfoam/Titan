package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.codec.asset.SubAssetReference;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class MultiAssetTest {
    @Test
    public void readWrite() throws IOException {
        SubAssetReference child = new SubAssetReference(0, 0);
        AssetReference properties = AssetReference.create(1, child);
        MultiAsset multi = new MultiAsset(properties);

        byte[] data = "Hello World".getBytes();
        multi.put(0, ByteBuffer.wrap(data));

        MultiAsset recoded = new MultiAsset(properties, multi.encode());
        Assert.assertEquals(multi.size(), recoded.size());

        ByteBuffer out = recoded.get(0);
        Assert.assertNotNull("child went missing", out);

        byte[] result = new byte[out.remaining()];
        out.get(result);

        Assert.assertArrayEquals("mismatch data", data, result);
    }

    @Test
    public void disperseReadWrite() throws IOException {
        SubAssetReference child = new SubAssetReference(5, 0);
        AssetReference properties = AssetReference.create(1, child);
        MultiAsset multi = new MultiAsset(properties);

        byte[] data = "Hello World".getBytes();
        multi.put(5, ByteBuffer.wrap(data));

        MultiAsset recoded = new MultiAsset(properties, multi.encode());
        Assert.assertEquals(multi.size(), recoded.size());

        ByteBuffer out = recoded.get(child.getId());
        Assert.assertNotNull("child went missing", out);

        byte[] result = new byte[out.remaining()];
        out.get(result);

        Assert.assertArrayEquals("mismatch data", data, result);
    }

    @Test
    public void incompleteAsset() throws IOException {
        // Create a reference to SubAsset 5
        SubAssetReference child = new SubAssetReference(5, 0);
        AssetReference properties = AssetReference.create(1, child);
        MultiAsset multi = new MultiAsset(properties);

        // Store SubAsset 4
        byte[] data = "Hello World".getBytes();
        multi.put(4, ByteBuffer.wrap(data));

        // SubAsset 5 has no data, SubAsset 4 is a stowaway
        Assert.assertFalse("Asset must be incomplete, it's got stowaways and missing children", multi.isComplete());

        try {
            multi.encode();

            Assert.fail("Shouldn't be able to encode an asset which is incomplete");
        } catch (IllegalArgumentException e) {
            // Great! We prevented an incomplete asset saving
        }
    }

    @Test
    public void missingChildren() throws IOException {
        // Create a reference to SubAsset 5
        SubAssetReference child = new SubAssetReference(5, 0);
        AssetReference properties = AssetReference.create(1, child);
        MultiAsset multi = new MultiAsset(properties);

        // SubAsset 5 has no data, SubAsset 4 is a stowaway
        Assert.assertFalse("Asset must be incomplete, it's missing data for child#5", multi.isComplete());
    }

    @Test
    public void stowawayChildren() throws IOException {
        // Create a reference to SubAsset 5
        AssetReference properties = AssetReference.create(1);
        MultiAsset multi = new MultiAsset(properties);

        // Store SubAsset 4
        byte[] data = "Hello World".getBytes();
        multi.put(4, ByteBuffer.wrap(data));

        // SubAsset 5 has no data, SubAsset 4 is a stowaway
        Assert.assertFalse("Asset must be incomplete, it's missing data for child#5", multi.isComplete());
    }
}
