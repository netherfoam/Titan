package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.CachedAssetStorage;
import org.maxgamer.rs.assets.IDX;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.formats.GameObjectFormat;
import org.maxgamer.rs.assets.formats.ItemFormat;
import org.maxgamer.rs.assets.formats.NPCFormat;
import org.maxgamer.rs.assets.formats.BitVarConfigFormat;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author netherfoam
 */
public class FormatTest {
    private static AssetStorage storage;

    @BeforeClass
    public static void init() throws IOException {
        storage = new CachedAssetStorage(new File("cache"));
    }

    @Test
    public void itemTest() throws IOException {
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

    @Test
    public void allObjectTest() throws IOException {
        for(int file : storage.getIndex(IDX.OBJECTS).getReferences().keySet()) {
            MultiAsset multi = storage.archive(IDX.OBJECTS, file);

            Iterator<Map.Entry<Integer, ByteBuffer>> iterator = multi.iterator();
            while(iterator.hasNext()) {
                Map.Entry<Integer, ByteBuffer> entry = iterator.next();

                GameObjectFormat format = new GameObjectFormat();
                ByteBuffer readOnly = entry.getValue().asReadOnlyBuffer();
                format.decode(readOnly);
                Assert.assertEquals("expect no data remaining", 0, readOnly.remaining());

                ByteBuffer encoded = format.encode();

                GameObjectFormat other = new GameObjectFormat();
                other.decode(encoded);
                encoded.position(0);

                Assert.assertEquals(format, other);
            }
        }
    }

    @Test
    public void obj5699Test() throws IOException {
        int[] ids = {5699};
        for(int id : ids) {
            MultiAsset a = storage.archive(IDX.OBJECTS, id >> 8);
            ByteBuffer bb = a.get(id & 0xFF);

            GameObjectFormat format = new GameObjectFormat();
            ByteBuffer readOnly = bb.asReadOnlyBuffer();
            format.decode(readOnly);
            Assert.assertEquals("expect no data remaining", 0, readOnly.remaining());

            ByteBuffer encoded = format.encode();

            GameObjectFormat other = new GameObjectFormat();
            other.decode(encoded);
            encoded.position(0);

            Assert.assertEquals(format, other);
        }
    }


    @Test
    public void obj6714Test() throws IOException {
        int[] ids = {6714, 6720};
        for(int id : ids) {
            MultiAsset a = storage.archive(IDX.OBJECTS, id >> 8);
            ByteBuffer bb = a.get(id & 0xFF);

            GameObjectFormat format = new GameObjectFormat();
            ByteBuffer readOnly = bb.asReadOnlyBuffer();
            format.decode(readOnly);
            Assert.assertEquals("expect no data remaining", 0, readOnly.remaining());

            ByteBuffer encoded = format.encode();

            GameObjectFormat other = new GameObjectFormat();
            other.decode(encoded);
            encoded.position(0);

            Assert.assertEquals(format, other);
        }
    }

    @Test
    public void objectTest() throws IOException {
        for(int id = 500; id < 36780; id+= 50) {
            //final int id = 36781; // Lumbridge spawn north fountain
            ByteBuffer bb = null;

            while(bb == null) {
                id += 1;
                MultiAsset a = storage.archive(IDX.OBJECTS, id >> 8);
                bb = a.get(id & 0xFF);
            }

            GameObjectFormat format = new GameObjectFormat();
            ByteBuffer readOnly = bb.asReadOnlyBuffer();
            format.decode(readOnly);
            Assert.assertEquals("expect no data remaining", 0, readOnly.remaining());

            ByteBuffer encoded = format.encode();

            GameObjectFormat other = new GameObjectFormat();
            other.decode(encoded);
            encoded.position(0);

            Assert.assertEquals(format, other);
        }
    }

    @Test
    public void varConfigTest() throws IOException {
        // id & 0x3FF to calculate file id in cache
        // i >>> 10 is the group id

        // ID taken from a barrows door object
        final int id = 469;
        ByteBuffer content = storage.archive(IDX.CONFIGS, id >> 10).get(id & 0x3FF);
        BitVarConfigFormat config = new BitVarConfigFormat(content);

        Assert.assertEquals("expect config to decode the same", config, new BitVarConfigFormat(config.encode()));
    }

    @Test
    public void shifts() {
        int[] shifts = new int[32];
        int i = 2;
        for (int index = 0; ~index > -33; index++) {
            shifts[index] = -1 + i;
            i += i;
        }
        System.out.println(Arrays.toString(shifts));
    }
}
