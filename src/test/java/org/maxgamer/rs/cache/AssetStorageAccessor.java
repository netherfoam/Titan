package org.maxgamer.rs.cache;

import net.openrs.cache.ChecksumTable;
import org.maxgamer.rs.assets.AssetStorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class AssetStorageAccessor implements CacheAccessor {
    private AssetStorage storage;

    public AssetStorageAccessor(AssetStorage storage) throws IOException {
        this.storage = storage;
        storage.getProtocol().rebuildChecksum();
    }

    @Override
    public ChecksumTable getChecksum() {
        return storage.getProtocol().getChecksum();
    }

    @Override
    public ByteBuffer getRaw(int idx, int fileId) throws IOException {
        if(idx == 255) {
            return storage.getMasterTable().read(fileId);
        }

        return storage.getTable(idx).read(fileId);
    }

    @Override
    public int getSize(int idx) throws IOException {
        return storage.getTable(idx).size();
    }

    @Override
    public ByteBuffer createResponse(int idx, int fileId, int opcode) throws IOException {
        return storage.getProtocol().response(idx, fileId, opcode);
    }

    @Override
    public int getIDXCount() throws IOException {
        return storage.size();
    }

    @Override
    public int getVersion(int idx) throws FileNotFoundException {
        return storage.getIndex(idx).getVersion();
    }

}
