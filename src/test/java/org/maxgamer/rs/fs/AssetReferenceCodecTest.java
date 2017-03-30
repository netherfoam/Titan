package org.maxgamer.rs.fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.assets.codec.asset.SubAssetReference;

/**
 * @author netherfoam
 */
public class AssetReferenceCodecTest {
    private IndexTable assetPropertiesCodex;

    private IndexTable reencode(IndexTable table) {
        return new IndexTable(assetPropertiesCodex.getIdx(), table.encode());
    }

    @Before
    public void init() {
        assetPropertiesCodex = new IndexTable(12, 1);
    }

    @After
    public void verify() {
        // Smoke test: this should work
        IndexTable result = reencode(assetPropertiesCodex);
        Assert.assertEquals("version", assetPropertiesCodex.getVersion(), result.getVersion());
        Assert.assertEquals("flags", assetPropertiesCodex.getFlags(), result.getFlags());
        Assert.assertEquals("format", assetPropertiesCodex.getFormat(), result.getFormat());
        Assert.assertEquals("file count", assetPropertiesCodex.getReferences().size(), result.getReferences().size());
    }

    @Test
    public void empty() {
        // init and verify do our tests here for an empty table
    }

    @Test
    public void singleReadWrite() {
        AssetReference reference = AssetReference.create(1);
        assetPropertiesCodex.getReferences().put(0, reference);

        IndexTable reloaded = reencode(assetPropertiesCodex);

        AssetReference fetched = reloaded.getReferences().get(0);
        Assert.assertNotNull("must fetch a reference", fetched);

        assertEquals(reference, fetched);
    }

    @Test
    public void multiReadWrite() {
        AssetReference first = AssetReference.create(1);
        AssetReference second = AssetReference.create(1);
        assetPropertiesCodex.getReferences().put(0, first);
        assetPropertiesCodex.getReferences().put(1, second);

        IndexTable reloaded = reencode(assetPropertiesCodex);

        assertEquals(first, reloaded.getReferences().get(0));
        assertEquals(second, reloaded.getReferences().get(1));
    }

    @Test
    public void singleOverwrite() {
        AssetReference initial = AssetReference.create(1);
        AssetReference replacement = AssetReference.create(2);
        assetPropertiesCodex.getReferences().put(0, initial);
        assetPropertiesCodex.getReferences().put(0, replacement);

        IndexTable reloaded = reencode(assetPropertiesCodex);

        assertEquals(replacement, reloaded.getReferences().get(0));
    }

    @Test
    public void singleChildReadWrite() {
        SubAssetReference child = new SubAssetReference(0, "(some_hash)".hashCode());
        AssetReference parent = AssetReference.create(1, child);

        assetPropertiesCodex.getReferences().put(0, parent);
        assetPropertiesCodex.setFlags(IndexTable.FLAG_IDENTIFIERS);
        IndexTable reloaded = reencode(assetPropertiesCodex);

        assertEquals(parent, reloaded.getReferences().get(0));
        assertEquals(child, reloaded.getReferences().get(0).getChild(0));
    }

    @Test
    public void multiChildReadWrite() {
        SubAssetReference first = new SubAssetReference(0, "(some_hash)".hashCode());
        SubAssetReference second = new SubAssetReference(1, "(other_hash)".hashCode());
        AssetReference parent = AssetReference.create(1, first, second);

        assetPropertiesCodex.getReferences().put(0, parent);
        assetPropertiesCodex.setFlags(IndexTable.FLAG_IDENTIFIERS);
        IndexTable reloaded = reencode(assetPropertiesCodex);

        assertEquals(parent, reloaded.getReferences().get(0));
        assertEquals(first, reloaded.getReferences().get(0).getChild(0));
        assertEquals(second, reloaded.getReferences().get(0).getChild(1));
    }

    @Test
    public void disperseReadWrite() {
        AssetReference first = AssetReference.create(1); //new AssetReference(0, 0, new byte[64], 1, 0);
        AssetReference second = AssetReference.create(1);
        assetPropertiesCodex.getReferences().put(54, first);
        assetPropertiesCodex.getReferences().put(980, second);

        IndexTable reloaded = reencode(assetPropertiesCodex);

        assertEquals(first, reloaded.getReferences().get(54));
        assertEquals(second, reloaded.getReferences().get(980));
    }

    @Test
    public void disperseChildReadWrite() {
        SubAssetReference first = new SubAssetReference(16, "(some_hash)".hashCode());
        SubAssetReference second = new SubAssetReference(813, "(other_hash)".hashCode());
        AssetReference parent = AssetReference.create(1, first, second);

        assetPropertiesCodex.getReferences().put(0, parent);
        assetPropertiesCodex.setFlags(IndexTable.FLAG_IDENTIFIERS);
        IndexTable reloaded = reencode(assetPropertiesCodex);

        assertEquals(parent, reloaded.getReferences().get(0));
        assertEquals(first, reloaded.getReferences().get(0).getChild(0));
        assertEquals(second, reloaded.getReferences().get(0).getChild(1));
    }

    private void assertEquals(AssetReference expected, AssetReference result) {
        if(expected == result) return;
        Assert.assertNotNull("expected is null", expected);
        Assert.assertNotNull("result is null", result);

        Assert.assertEquals("child count", expected.getChildCount(), result.getChildCount());
        Assert.assertEquals("crc checksum", expected.getCRC(), result.getCRC());
        Assert.assertEquals("identifier", expected.getIdentifier(), result.getIdentifier());
        Assert.assertEquals("version", expected.getVersion(), result.getVersion());
        Assert.assertArrayEquals("whirlpool checksum", expected.getWhirlpool(), result.getWhirlpool());
    }

    private void assertEquals(SubAssetReference expected, SubAssetReference result) {
        if(expected == result) return;
        Assert.assertNotNull("expected is null", expected);
        Assert.assertNotNull("result is null", result);

        Assert.assertEquals("identifier", expected.getIdentifier(), result.getIdentifier());
    }
}
