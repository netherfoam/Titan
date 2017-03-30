package org.maxgamer.rs.cache;

import net.openrs.cache.ChecksumTable;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Proxy class so that we can write tests for other implementations of the cache
 *
 * @author netherfoam
 */
public interface CacheAccessor {
    ByteBuffer createResponse(int idx, int fileId, int opcode) throws IOException;
    ChecksumTable getChecksum();
    int getIDXCount() throws IOException;
    ByteBuffer getRaw(int idx, int fileId) throws IOException;
    int getSize(int idx) throws IOException;
    int getVersion(int idx) throws IOException;
}
