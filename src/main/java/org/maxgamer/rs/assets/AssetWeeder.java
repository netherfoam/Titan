package org.maxgamer.rs.assets;

import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.assets.formats.DefaultPlayerSettingsFormat;
import org.maxgamer.rs.assets.formats.EnumFormat;
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
        customTitles(storage);
    }

    public static void customTitles(AssetStorage storage) throws IOException {
        Asset asset = storage.read(IDX.DEFAULTS, 1);
        ByteBuffer content = asset.getPayload();

        DefaultPlayerSettingsFormat settings = new DefaultPlayerSettingsFormat(content);

        MultiAsset multi = storage.archive(IDX.CLIENTSCRIPT_SETTINGS, settings.getMaleTitleIds()[0] >> 8);
        ByteBuffer titlesContent = multi.get(settings.getMaleTitleIds()[0] & 0xFF);
        EnumFormat titles = new EnumFormat(titlesContent);
        titles.set(6, "Software Engineer");
        storage.writer(IDX.CLIENTSCRIPT_SETTINGS)
                .write(settings.getMaleTitleIds()[0] >> 8, settings.getMaleTitleIds()[0] & 0xFF, titles.encode())
                .commit();
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
}
