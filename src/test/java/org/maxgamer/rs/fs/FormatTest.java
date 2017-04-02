package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.IDX;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.formats.ItemFormat;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class FormatTest {
    @Test
    public void itemTest() throws IOException {
        AssetStorage storage = new AssetStorage(new File("cache"));

        final int id = 995; // Coins
        MultiAsset a = storage.archive(IDX.ITEMS, id >> 8);
        ByteBuffer bb = a.get(id & 0xFF);

        ItemFormat format = new ItemFormat();
        format.decode(bb.asReadOnlyBuffer());

        ByteBuffer encoded = format.encode();
        Assert.assertEquals(encoded.remaining(), bb.remaining());

        format.setInventoryOption(3, "Lick");
        ItemFormat updated = new ItemFormat();
        updated.decode(format.encode());

        Assert.assertEquals("expect Lick", "Lick", updated.getInventoryOption(3));
    }
}
