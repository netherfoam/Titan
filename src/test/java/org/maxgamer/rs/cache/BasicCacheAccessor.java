package org.maxgamer.rs.cache;

import net.openrs.cache.ChecksumTable;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class BasicCacheAccessor implements CacheAccessor {
    private Cache cache;

    public BasicCacheAccessor(Cache cache) {
        this.cache = cache;
    }

    @Override
    public ChecksumTable getChecksum() {
        return cache.getChecksum();
    }

    @Override
    public ByteBuffer getRaw(int idx, int fileId) throws IOException {
        return cache.getRaw(idx, fileId);
    }

    @Override
    public ByteBuffer createResponse(int idx, int fileId, int opcode) throws IOException {
        return cache.createResponse(idx, fileId, opcode);
    }

    @Override
    public int getIDXCount() {
        return cache.getIDXCount();
    }

    @Override
    public int getSize(int idx) throws IOException {
        return cache.getFileTable(idx).size();
    }

    @Override
    public int getVersion(int idx) throws IOException {
        return cache.getReferenceTable(idx).getVersion();
    }
}
