package org.maxgamer.rs.assets;

import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.MapCache;
import org.maxgamer.rs.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author netherfoam
 */
public class AssetWeeder {
    public static void weed(AssetStorage storage) throws IOException {
        Log.debug("Cache change detected. Recalculating!");

        // We scan through all of the map files, attempt to parse them, and blacklist broken ones
        AssetWriter writer = storage.writer(IDX.LANDSCAPES);

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                int fileId;
                try {
                    fileId = storage.getIndex(IDX.LANDSCAPES).getFile("l" + x + "_" + y);
                } catch (FileNotFoundException e) {
                    // This file doesn't exist, no need to delete it
                    continue;
                }
                try {
                    MapCache.getObjects(storage, x, y);
                } catch (IOException e) {
                    AssetReference ref = storage.properties(IDX.LANDSCAPES, fileId);
                    // File is broken or encrypted and we don't have the key.
                    writer.delete(fileId);
                }
            }
        }

        writer.commit();
        // Update config & save
    }
}
