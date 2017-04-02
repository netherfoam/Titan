package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.IDX;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.formats.ItemFormat;
import org.maxgamer.rs.assets.formats.NPCFormat;

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

    @Test
    public void npcTest() throws IOException {
        AssetStorage storage = new AssetStorage(new File("cache"));

        final int id = 3381; // Doris
        MultiAsset a = storage.archive(IDX.NPCS, id >> 7);
        ByteBuffer bb = a.get(id & 0x7F);

        NPCFormat format = new NPCFormat();
        format.decode(bb.asReadOnlyBuffer());

        ByteBuffer encoded = format.encode();

        NPCFormat other = new NPCFormat();
        other.decode(encoded);
        encoded.position(0);

        Assert.assertEquals(format, other);
    }
}
