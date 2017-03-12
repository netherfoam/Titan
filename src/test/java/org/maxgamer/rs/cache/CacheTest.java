package org.maxgamer.rs.cache;

import org.junit.BeforeClass;

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
        cache = new BasicCacheAccessor(new Cache(new File("cache")));
    }
}
