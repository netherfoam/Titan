package org.maxgamer.rs.cache;

import org.junit.BeforeClass;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.AssetWeeder;

import java.io.File;
import java.io.IOException;

/**
 * Abstract test, which initialises the cache from the cache/ folder. This shouldn't contain tests, instead, subclasses should
 * contain the tests.
 *
 * @author netherfoam
 */
public abstract class CacheTest {
    protected static CacheAccessor cache;

    @BeforeClass
    public static void init() throws IOException {
        // Prevent initialising the cache multiple times - that's an expensive operation.
        if (cache != null) return;

        AssetStorage storage = new AssetStorage(new File("cache"));
        AssetWeeder.weed(storage);
        cache = new AssetStorageAccessor(storage);
    }
}
