package org.maxgamer.rs.assets;

import org.maxgamer.rs.assets.codec.Codec;
import org.maxgamer.rs.assets.codec.asset.AssetReference;
import org.maxgamer.rs.assets.codec.asset.SubAssetReference;
import org.maxgamer.rs.util.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * MultiAsset is a codec that decodes a single Asset, which has one or more children inside it.
 *
 * @author netherfoam
 */
public class MultiAsset extends Codec {
    /**
     * The properties of the parent asset
     */
    private AssetReference reference;

    /**
     * Map of entry id to payload. Id's are not sequential here, but order is maintained.
     */
    private TreeMap<Integer, ByteBuffer> entries = new TreeMap<>();

    /**
     * Constructs a new {@link MultiAsset}, with no children. This is used when creating a
     * new MultiAsset, to be written to disk
     * @param reference the meta information for the asset
     */
    public MultiAsset(AssetReference reference) {
        this.reference = reference;
    }

    /**
     * Constructs a new {@link MultiAsset} and then decodes the given buffer as the contents
     * @param reference the meta information for the asset
     * @param bb the buffer to decode
     * @throws IOException if the buffer can't be decoded
     */
    public MultiAsset(AssetReference reference, ByteBuffer bb) throws IOException {
        this.reference = reference;
        this.decode(bb);
    }

    @Override
    public void decode(ByteBuffer bb) throws IOException {
        int size = reference.getChildCount();
        int start = bb.position();
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
        bb.position(start);
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
            if (reference.getChild(i) != null) {
                index = reference.getChild(i).getId();
            }
            this.entries.put(index, entries[i]);
        }

        Assert.isTrue(isComplete(), "Decoded assets should be complete");
    }

    @Override
    public ByteBuffer encode() throws IOException {
        Assert.isTrue(isComplete(), "Asset must be complete to save");

        final int NUMBER_OF_CHUNKS = 1;
        // Number of chunks: 1
        // Chunk sizes: chunks * entries.size() * 4
        // Chunks data: sum(sum(chunkSizes))

        // Simplicity sake, let chunks = 1
        int length = NUMBER_OF_CHUNKS;
        length += NUMBER_OF_CHUNKS * entries.size() * 4;

        // Deltas are simplified because chunks is simplified to 1
        for(ByteBuffer bb : entries.values()) {
            length += bb.remaining();
        }

        ByteBuffer out = ByteBuffer.allocate(length);

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

        Assert.isFalse(out.hasRemaining(), "Expect buffer to be exact size");

        // That appears to be it
        out.flip();

        return out;
    }

    /**
     * The number of files inside this asset
     * @return The number of files inside this asset
     */
    public int size() {
        return entries.size();
    }

    /**
     * Fetch the given asset by id. Id's are not guaranteed to be sequential
     * @param j the id
     * @return the buffer or null
     */
    public ByteBuffer get(int j) {
        ByteBuffer bb = entries.get(j);
        if(bb == null) return null;

        return bb.asReadOnlyBuffer();
    }

    /**
     * Create a copy of this MultiAsset. The copy can be modified without modifying this asset.
     * @return the copy
     */
    public MultiAsset copy() {
        MultiAsset that = new MultiAsset(reference);
        that.entries = new TreeMap<>(this.entries);

        return that;
    }

    /**
     * Fetch an iterator for iterating over the files inside this asset
     * @return the iterator
     */
    public Iterator<Map.Entry<Integer, ByteBuffer>> iterator() {
        // Instead of just returning the entries.entrySet().iterator(), we make sure that we only
        // ever expose byte buffers that are shallow copies of the stored ones.
        final Iterator<Map.Entry<Integer, ByteBuffer>> inner = entries.entrySet().iterator();
        return new Iterator<Map.Entry<Integer, ByteBuffer>>(){
            @Override
            public boolean hasNext() {
                return inner.hasNext();
            }

            @Override
            public Map.Entry<Integer, ByteBuffer> next() {
                return new ReadOnlyEntry(inner.next());
            }

            @Override
            public void remove() {
                inner.remove();
            }
        };
    }

    /**
     * Appends the given asset by id. Id's are not guaranteed to be sequential
     * @param id the id
     * @param contents the contents to add to the asset
     */
    public void put(int id, ByteBuffer contents) {
        if(contents == null) {
            entries.remove(id);
            return;
        }

        entries.put(id, contents.asReadOnlyBuffer());
    }

    /**
     * Returns true if this archive has no missing / stowaway children. A MultiAsset must be
     * "complete" before it is able to be encoded.
     * @return true if sane, false if insane
     */
    public boolean isComplete() {
        // If the size mismatches, then we're definitely not ready to write!
        if(reference.getChildCount() != entries.size()) return false;

        // Iterate over the referenced children, check that none are missing
        for(SubAssetReference subRef : reference.getChildren()) {
            if(entries.get(subRef.getId()) == null) return false;
        }

        // By virtue, the size matches and all of the children were found. Therefore,
        // we don't have any extra stowaways

        return true;
    }

    /**
     * Map entry subclass that handles byte buffers, such that any copies added/fetched are read only
     */
    public static class ReadOnlyEntry implements Map.Entry<Integer, ByteBuffer> {
        private Map.Entry<Integer, ByteBuffer> entry;

        public ReadOnlyEntry(Map.Entry<Integer, ByteBuffer> entry) {
            this.entry = entry;
        }


        @Override
        public Integer getKey() {
            return entry.getKey();
        }

        @Override
        public ByteBuffer getValue() {
            return entry.getValue().asReadOnlyBuffer();
        }

        @Override
        public ByteBuffer setValue(ByteBuffer byteBuffer) {
            return entry.setValue(byteBuffer.asReadOnlyBuffer());
        }
    }
}
