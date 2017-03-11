package org.maxgamer.rs.cache;

import org.maxgamer.rs.cache.reference.Reference;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

public class Archive {
    /*public static Archive decode(Reference ref, CacheFile file) throws IOException{
        int size = ref.getChildCount();
        if(size <= 1) throw new IOException("That file doesn't appear to be an archive...");

        ByteBuffer bb = file.getData();
        Archive a = new Archive(size);

        //int ptr = bb.limit();
        //int chunks = data[--ptr] & 0xff; //Last byte is the number of chunks
        int chunks = bb.get(bb.limit() - 1) & 0xFF;

        //RSInputStream stream = new RSInputStream(new RSByteArrayInputStream(data));
        //ptr -= chunks * (size * 4);
        bb.position(bb.limit() - 1 - (chunks * size * 4));

        //stream.seek(ptr);
        int[] sizes = new int[size];
        for (int x = 0; chunks > x; x++) {
            int chunkSize = 0;
            for (int i = 0; i < size; i++) {
                chunkSize += bb.getInt();
                sizes[i] += chunkSize;
            }
        }
        byte[][] payloads = new byte[size][];
        for (int i = 0; i < size; i++) {
            payloads[i] = new byte[sizes[i]];
            sizes[i] = 0;
        }
        int readPos = 0;
        //stream.seek(ptr);
        bb.position(bb.limit() - 1 - (chunks * size * 4));
        for (int x = 0; x < chunks; x++) {
            int delta = 0;
            for (int i = 0; i < size; i++) {
                delta += bb.getInt();
                //System.arraycopy(data, readPos, payloads[i], sizes[i], delta);
                int pos = bb.position(); //Store old position
                bb.position(readPos); //Go to data position
                bb.get(payloads[i], sizes[i], delta); //Copy data
                bb.position(pos); //Go to old position
                
                readPos += delta;
                sizes[i] += delta;
            }
        }
        for (int i = 0; i < size; i++) {
            int i_31_;
            if (is_11_ != null)
                i_31_ = is_11_[i];
            else
                i_31_ = i;
            objects[i_31_] = payloads[i];
        }
        archiveFiles[cache][main] = objects;
        return true;
        
        for(int i = 0; i < size; i++){
            a.entries[i] = ByteBuffer.wrap(objects[i]);
        }
    }*/

    //private ByteBuffer[] entries;
    private HashMap<Integer, ByteBuffer> entries;

    /**
     * Constructs a new, blank archive of the given size
     *
     * @param size the size of the archive.
     */
    public Archive(int size) {
        this.entries = new HashMap<>(size);
    }

    /**
     * Decodes the given cache file into an archive.
     *
     * @param ref  the reference for the file
     * @param file the file to decode
     * @return the archive
     * @throws IOException
     */
    public static Archive decode(Reference ref, CacheFile file) throws IOException {
        Archive a = new Archive(ref.getChildCount());

        try {
            int size = ref.getChildCount();

            ByteBuffer bb = file.getData();

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
                    throw new IOException("Illegal archive subfile size. Archive ID: " + ref.getId() + ", ChildId: " + id + ", Requested Size: " + sizes[id]);
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
                entries[id].flip();
            }

            // correctly disperse the ids
            for (int i = 0; i < size; i++) {
                int index = i;
                if (ref.getChild(i) != null) {
                    index = ref.getChild(i).getId();
                }
                //a.entries.put(i, entries[i]);
                a.entries.put(index, entries[i]);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }

        return a;
    }

    /**
     * Gets the subfile by ID
     *
     * @param id the index
     * @return the subfile
     */
    public ByteBuffer get(int id) {
        ByteBuffer bb = entries.get(id);
        if (bb == null) return null;
        return bb.asReadOnlyBuffer();
    }

    /**
     * Fetches all bytebuffers from this archive. These are made to be a read-only copy
     * of the array. There may be null indexes.
     *
     * @return a read only copy of all subfiles
     */
    public HashMap<Integer, ByteBuffer> getAll() {
        HashMap<Integer, ByteBuffer> buffers = new HashMap<>(this.entries.size());
        for (Entry<Integer, ByteBuffer> e : entries.entrySet()) {
            ByteBuffer bb = e.getValue();
            if (bb != null) buffers.put(e.getKey(), bb.asReadOnlyBuffer());
        }
        return buffers;
    }

    /**
     * The number of subfiles
     *
     * @return the number of subfiles
     */
    public int size() {
        return entries.size();
    }

    /*
    public CacheFile encode(RSCompression compression){
        CacheFile f = new CacheFile(compression);

        //TODO: Encode() to F

        return f;
    }*/
}