package org.maxgamer.rs.cache;

import net.openrs.cache.ChecksumTable;
import org.maxgamer.rs.cache.reference.ReferenceTable;

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
    int getIDXCount();
    ReferenceTable getReferenceTable(int idx);
    ByteBuffer getRaw(int idx, int fileId) throws IOException;
    public FileTable getFileTable(int idx);
}
