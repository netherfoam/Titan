package org.maxgamer.rs.assets;

import org.maxgamer.rs.Assert;
import org.maxgamer.rs.assets.codec.Codec;
import org.maxgamer.rs.assets.codec.asset.AssetReference;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TreeMap;

/**
 * @author netherfoam
 */
public class MultiAsset extends Codec {
    private AssetReference properties;
    private TreeMap<Integer, ByteBuffer> entries = new TreeMap<>();

    public MultiAsset(AssetReference properties) {
        this.properties = properties;
    }

    public MultiAsset(AssetReference properties, ByteBuffer bb) throws IOException {
        this.properties = properties;
        this.decode(bb);
    }

    @Override
    public void decode(ByteBuffer bb) throws IOException {
        int size = properties.getChildCount();

        bb.position(bb.limit() - 1);
        int chunks = bb.get() & 0xFF;

        // read the sizes of the child entries and individual chunks
        int[][] chunkSizes = new int[chunks][size];
        int[] sizes = new int[size];
        bb.position(bb.limit() - 1 - chunks * size * 4);
        for (int chunk = 0; chunk < chunks; chunk++) {
            int chunkSize = 0;
            for (int id = 0; id < size; id++) {
                // read the delta-encoded chunk length
                int delta = bb.getInt();
                chunkSize += delta;

                chunkSizes[chunk][id] = chunkSize; // store the size of this chunk
                sizes[id] += chunkSize;            // and add it to the size of the whole file
            }
        }

        ByteBuffer[] entries = new ByteBuffer[size];

        // allocate the buffers for the child entries
        for (int id = 0; id < size; id++) {
            if (sizes[id] > 1024 * 1024 * 20) {
                //Size > 20mb
                throw new IOException("Illegal archive subfile size. ChildId: " + id + ", Requested Size: " + sizes[id]);
            }
            entries[id] = ByteBuffer.allocate(sizes[id]);
        }

        // read the data into the buffers
        bb.position(0);
        for (int chunk = 0; chunk < chunks; chunk++) {
            for (int id = 0; id < size; id++) {
                // get the length of this chunk
                int chunkSize = chunkSizes[chunk][id];

                // copy this chunk into a temporary buffer
                byte[] temp = new byte[chunkSize];
                bb.get(temp);

                // copy the temporary buffer into the file buffer
                entries[id].put(temp);
            }
        }

        // flip all of the buffers
        for (int id = 0; id < size; id++) {
            Assert.isTrue(!entries[id].hasRemaining(), "should write entire buffer");
            entries[id].flip();
        }

        // correctly disperse the ids
        for (int i = 0; i < size; i++) {
            int index = i;
            if (properties.getChild(i) != null) {
                index = properties.getChild(i).getId();
            }
            this.entries.put(index, entries[i]);
        }
    }

    @Override
    public ByteBuffer encode() throws IOException {
        final int NUMBER_OF_CHUNKS = 1;
        // Number of chunks: 1
        // Chunk sizes: chunks * entries.size() * 4
        // Chunks data: sum(sum(chunkSizes))

        // Simplicity sake, let chunks = 1
        int length = NUMBER_OF_CHUNKS;
        length += NUMBER_OF_CHUNKS * entries.size() * 4;
        //TODO: We should be iterating over entries instead of incrementing an index counter

        // Deltas are simplified because chunks is simplified to 1
        //for(int id = 0; id < entries.size(); id++) {
        for(ByteBuffer bb : entries.values()) {
            length += bb.remaining();
        }

        ByteBuffer out = ByteBuffer.allocate(length);

        //for(int id = 0; id < entries.size(); id++) {
        for(int id : entries.keySet()) {
            out.put(get(id));
        }

        if(out.limit() - 1 - NUMBER_OF_CHUNKS * entries.size() * 4 != out.position()) {
            throw new IllegalStateException();
        }

        // Write the chunk sizes at the end
        int accumulator = 0;
        for(ByteBuffer bb : entries.values()) {
            int delta = bb.remaining() - accumulator;
            accumulator += delta;

            out.putInt(delta);
        }

        // Number of chunks
        out.put((byte) NUMBER_OF_CHUNKS);

        if(out.hasRemaining()) {
            throw new IllegalStateException();
        }

        // That appears to be it
        out.flip();

        return out;
    }

    public int size() {
        return entries.size();
    }

    public ByteBuffer get(int j) {
        ByteBuffer bb = entries.get(j);
        if(bb == null) return null;

        return bb.asReadOnlyBuffer();
    }

    public void put(int id, ByteBuffer contents) {
        if(contents == null) {
            entries.remove(id);
            return;
        }

        entries.put(id, contents.asReadOnlyBuffer());
    }
}
