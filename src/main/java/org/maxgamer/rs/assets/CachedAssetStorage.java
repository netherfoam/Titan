package org.maxgamer.rs.assets;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Subclass of {@link AssetStorage} that caches {@link MultiAsset}'s using a {@link WeakReference}. When the
 * {@link DataTable}'s are modified, and then this cache is accessed, the cache will be cleared to prevent
 * stale reads from the cache.
 *
 * @author netherfoam
 */
public class CachedAssetStorage extends AssetStorage {
    public static AssetStorage create(File folder) throws IOException {
        forceCreate(new File(folder, "main_file_cache.idx255"));
        forceCreate(new File(folder, "main_file_cache.dat2"));

        return new CachedAssetStorage(folder);
    }

    private long cacheLastCleared;
    private Map<Integer, Map<Integer, WeakReference<MultiAsset>>> cache;

    /**
     * Constructs a new AssetStorage using the given folder
     *
     * @param folder the folder
     * @throws IOException if the master index or data table don't exist
     */
    public CachedAssetStorage(File folder) throws IOException {
        super(folder);

        reset();
    }

    @Override
    public MultiAsset archive(int idx, int file) throws IOException {
        int key = (idx << 24) | file;

        // If the physical files were modified, we need to clear our cache because our assets might've changed
        long modified = Math.max(getMasterTable().modified(), getTable(idx).modified());

        if(modified >= cacheLastCleared) {
            reset();
        }

        Map<Integer, WeakReference<MultiAsset>> index = cache.get(key);

        if(index == null) {
            index = new HashMap<>();
            cache.put(key, index);
        }

        WeakReference<MultiAsset> reference = index.get(file);
        if(reference != null) {
            MultiAsset asset = reference.get();

            // We don't return the asset, we return a copy of it. Such that, if one copy
            // is modified, and a second copy is retrieved, the second copy will not show
            // the first copy's changes, unless the first copy was written to disk!
            if(asset != null) return asset.copy();
        }

        // We've got no cached version, so fetch it and cache it
        MultiAsset asset = super.archive(idx, file);
        index.put(file, new WeakReference<>(asset));

        // We return a copy so that nobody can modify our internal version
        return asset.copy();
    }

    /**
     * Invalidate all items in the cache
     */
    private void reset() {
        cache = new HashMap<>();
        cacheLastCleared = System.currentTimeMillis();
    }
}
