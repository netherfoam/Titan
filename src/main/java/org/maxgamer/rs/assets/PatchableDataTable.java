package org.maxgamer.rs.assets;

import org.maxgamer.rs.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class PatchableDataTable extends DataTable {
    private long lastModified;
    private Map<Integer, ByteBuffer> patches = new HashMap<>();
    private int size = -1;

    /**
     * Constructs a new data table. If the data table doesn't exist yet, this will initialise it.
     *
     * @param indexId the index id (0-255)
     * @param index   the index
     * @param data    the data channel to read/write to
     * @throws IOException if the channel can't be read or written to
     */
    public PatchableDataTable(int indexId, FileChannel index, FileChannel data) throws IOException {
        super(indexId, index, data);
    }

    private void reset() {
        lastModified = System.currentTimeMillis();
        size = -1;
    }

    @Override
    public void write(int fileId, ByteBuffer contents) throws IOException {
        // We must do validation here, since we never call super.write(..)
        Assert.isPositive(fileId, "File Id must be positive");
        Assert.isTrue(fileId <= 0xFFFF, "File Id is 2 bytes at most");
        Assert.isTrue(contents.remaining() <= 0xFFFFFF, "Files must be < 16 megabytes");

        patches.put(fileId, contents.asReadOnlyBuffer());
        reset();
    }

    @Override
    public ByteBuffer read(int fileId) throws IOException {
        // We must do validation here, since we may never call super.read(..)
        Assert.isPositive(fileId, "File Id must be positive");
        Assert.isTrue(fileId <= 0xFFFFFF, "File Id is 3 bytes at most");

        if(patches.containsKey(fileId)) {
            ByteBuffer bb = patches.get(fileId);

            if (bb != null) {
                return bb.asReadOnlyBuffer();
            }

            // The patches contain the id, but the value is null. This indicates a deleted file
            throw new FileNotFoundException("File has been erased by patch " + fileId);
        }

        // File hasn't been patched at all
        return super.read(fileId);
    }

    @Override
    public int size() throws IOException {
        if(size == -1) {
            // We need to recalculate our size. So fetch the highest possible file id from super
            int patchedSize = super.size() - 1;

            // If any of our file id's are higher, use that instead
            for(int key : patches.keySet()) {
                if(key > patchedSize) patchedSize = key;
            }

            // File id's are zero based, so add one for length
            patchedSize += 1;

            this.size = patchedSize;
        }

        return size;
    }

    @Override
    public void erase(int fileId) throws IOException {
        // A key with a null value indicates a deleted value
        patches.put(fileId, null);
    }

    @Override
    public long modified() {
        // Whichever was modified most recently
        return Math.max(lastModified, super.modified());
    }
}
