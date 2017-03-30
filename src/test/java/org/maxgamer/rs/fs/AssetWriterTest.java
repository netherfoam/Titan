package org.maxgamer.rs.fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.codec.asset.IndexTable;

/**
 * @author netherfoam
 */
public class AssetWriterTest {
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
    public void testWrite() {
        // TODO
    }

    @Test
    public void testDelete() {
        // TODO
    }
}
