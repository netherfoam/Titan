package org.maxgamer.rs.assets;

import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.assets.formats.ItemFormat;
import org.maxgamer.rs.assets.protocol.MapCache;
import org.maxgamer.rs.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to remove map files which are encrypted and have invalid / no XTEA key to use to decrypt.
 *
 * @author netherfoam
 */
public class AssetWeeder {
    public static void weed(AssetStorage storage) throws IOException {
        evictMaps(storage);
        addLickToCoins(storage);
    }

    /**
     * Removes map files which are unreadable, given the available XTEA keys
     * @param storage the cache
     * @throws IOException if there was an error writing the changes to disk
     */
    public static void evictMaps(AssetStorage storage) throws IOException {
        // We scan through all of the map files, attempt to parse them, and blacklist broken ones
        AssetWriter writer = storage.writer(IDX.LANDSCAPES);
        IndexTable index = storage.getIndex(IDX.LANDSCAPES);

        // In order to do this quickly, we create a map of identifier -> file id. This is faster
        // than a linear search, since we have 256*256 (65536) look-ups to perform. This is about
        // 6x faster.
        Map<Integer, Integer> assetsByIdentifier = new HashMap<>(index.getReferences().size());
        for(Map.Entry<Integer, AssetReference> entry : index.getReferences().entrySet()) {
            assetsByIdentifier.put(entry.getValue().getIdentifier(), entry.getKey());
        }

        int removals = 0;
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                int hash = IndexTable.djb2("l" + x + "_" + y);
                Integer fileId = assetsByIdentifier.get(hash);
                if(fileId == null) {
                    // This file doesn't exist
                    continue;
                }

                try {
                    MapCache.getObjects(storage, x, y);
                } catch (IOException e) {
                    // File is broken or encrypted and we don't have the key.
                    writer.delete(fileId);
                    removals++;
                }
            }
        }

        if(removals > 0) {
            Log.info("Discovered " + removals + " landscape files with missing / invalid encryption keys. " +
                    "They're being patched out of the cache now.");
            writer.commit();
        }
    }

    public static void addLickToCoins(AssetStorage storage) throws IOException {
        final int id = 995; // Coins
        MultiAsset a = storage.archive(IDX.ITEMS, id >> 8);
        ByteBuffer bb = a.get(id & 0xFF);

        ItemFormat format = new ItemFormat();
        format.decode(bb.asReadOnlyBuffer());

        format.setInventoryOption(3, "Lick");

        storage.writer(IDX.ITEMS)
                .write(id >> 8, id & 0xFF, format.encode())
                .commit();
    }
}
