package org.maxgamer.rs.fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.RSCompression;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.assets.codec.asset.SubAssetReference;

import java.util.Iterator;
import java.util.Map;

/**
 * @author netherfoam
 */
public class IndexTableTest {
    private IndexTable indexTable;

    private IndexTable reencode(IndexTable table) {
        return new IndexTable(indexTable.getIdx(), table.getCompression(), table.encode());
    }

    @Before
    public void init() {
        indexTable = new IndexTable(12, RSCompression.NONE, 1);
    }

    @After
    public void verify() {
        // Smoke test: this should work
        IndexTable other = reencode(indexTable);

        Assert.assertEquals(indexTable.getIdx(), other.getIdx());
        Assert.assertEquals(indexTable.getVersion(), other.getVersion());
        Assert.assertEquals(indexTable.getCompression(), other.getCompression());
        Assert.assertEquals(indexTable.getFlags(), other.getFlags());
        Assert.assertEquals(indexTable.getFormat(), other.getFormat());

        Iterator<Map.Entry<Integer, AssetReference>> iit = indexTable.getReferences().entrySet().iterator();
        Iterator<Map.Entry<Integer, AssetReference>> oit = other.getReferences().entrySet().iterator();

        while(iit.hasNext() || oit.hasNext()) {
            Map.Entry<Integer, AssetReference> ientry = iit.next();
            Map.Entry<Integer, AssetReference> oentry = oit.next();

            Assert.assertEquals(ientry.getKey(), oentry.getKey());

            AssetReference iref = ientry.getValue();
            AssetReference oref = oentry.getValue();

            Assert.assertEquals(iref.getChildCount(), oref.getChildCount());
            Assert.assertEquals(iref.getVersion(), oref.getVersion());
            Assert.assertEquals(iref.getCRC(), oref.getCRC());
            Assert.assertEquals(iref.getIdentifier(), oref.getIdentifier());
            Assert.assertArrayEquals(iref.getWhirlpool(), oref.getWhirlpool());

            Assert.assertEquals(iref.getIdentifier(), oref.getIdentifier());

            for(int j = 0; j < iref.getChildren().length && j < oref.getChildren().length; j++) {
                SubAssetReference isub = iref.getChild(j);
                SubAssetReference osub = oref.getChild(j);

                Assert.assertEquals(isub.getIdentifier(), osub.getIdentifier());
                Assert.assertEquals(isub.getIdentifier(), osub.getIdentifier());
                Assert.assertEquals(isub.getId(), osub.getId());
            }
        }

        Assert.assertEquals(AssetWriter.crc32(indexTable.encode()), AssetWriter.crc32(other.encode()));
        Assert.assertArrayEquals(AssetWriter.whirlpool(indexTable.encode()), AssetWriter.whirlpool(other.encode()));
    }

    @Test
    public void empty() {
        // init and verify do our tests here for an empty table
    }

    @Test
    public void singleReadWrite() {
        AssetReference reference = AssetReference.create(1);
        indexTable.getReferences().put(0, reference);

        IndexTable reloaded = reencode(indexTable);

        AssetReference fetched = reloaded.getReferences().get(0);
        Assert.assertNotNull("must fetch a reference", fetched);

        assertEquals(reference, fetched);
    }

    @Test
    public void multiReadWrite() {
        AssetReference first = AssetReference.create(1);
        AssetReference second = AssetReference.create(1);
        indexTable.getReferences().put(0, first);
        indexTable.getReferences().put(1, second);

        IndexTable reloaded = reencode(indexTable);

        assertEquals(first, reloaded.getReferences().get(0));
        assertEquals(second, reloaded.getReferences().get(1));
    }

    @Test
    public void singleOverwrite() {
        AssetReference initial = AssetReference.create(1);
        AssetReference replacement = AssetReference.create(2);
        indexTable.getReferences().put(0, initial);
        indexTable.getReferences().put(0, replacement);

        IndexTable reloaded = reencode(indexTable);

        assertEquals(replacement, reloaded.getReferences().get(0));
    }

    @Test
    public void singleChildReadWrite() {
        SubAssetReference child = new SubAssetReference(0, "(some_hash)".hashCode());
        AssetReference parent = AssetReference.create(1, child);

        indexTable.getReferences().put(0, parent);
        indexTable.setFlags(IndexTable.FLAG_IDENTIFIERS);
        IndexTable reloaded = reencode(indexTable);

        assertEquals(parent, reloaded.getReferences().get(0));
        assertEquals(child, reloaded.getReferences().get(0).getChild(0));
    }

    @Test
    public void multiChildReadWrite() {
        SubAssetReference first = new SubAssetReference(0, "(some_hash)".hashCode());
        SubAssetReference second = new SubAssetReference(1, "(other_hash)".hashCode());
        AssetReference parent = AssetReference.create(1, first, second);

        indexTable.getReferences().put(0, parent);
        indexTable.setFlags(IndexTable.FLAG_IDENTIFIERS);
        IndexTable reloaded = reencode(indexTable);

        assertEquals(parent, reloaded.getReferences().get(0));
        assertEquals(first, reloaded.getReferences().get(0).getChild(0));
        assertEquals(second, reloaded.getReferences().get(0).getChild(1));
    }

    @Test
    public void disperseReadWrite() {
        AssetReference first = AssetReference.create(1); //new AssetReference(0, 0, new byte[64], 1, 0);
        AssetReference second = AssetReference.create(1);
        indexTable.getReferences().put(54, first);
        indexTable.getReferences().put(980, second);

        IndexTable reloaded = reencode(indexTable);

        assertEquals(first, reloaded.getReferences().get(54));
        assertEquals(second, reloaded.getReferences().get(980));
    }

    @Test
    public void disperseChildReadWrite() {
        SubAssetReference first = new SubAssetReference(16, "(some_hash)".hashCode());
        SubAssetReference second = new SubAssetReference(813, "(other_hash)".hashCode());
        AssetReference parent = AssetReference.create(1, first, second);

        indexTable.getReferences().put(0, parent);
        indexTable.setFlags(IndexTable.FLAG_IDENTIFIERS);
        IndexTable reloaded = reencode(indexTable);

        assertEquals(parent, reloaded.getReferences().get(0));
        assertEquals(first, reloaded.getReferences().get(0).getChild(0));
        assertEquals(second, reloaded.getReferences().get(0).getChild(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceedIndexLimit() {
        new IndexTable(255, RSCompression.NONE, 1);
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
