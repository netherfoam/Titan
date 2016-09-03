package org.maxgamer.rs.cache.reference;

import org.maxgamer.rs.cache.Cache;
import org.maxgamer.rs.cache.CacheFile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;


/**
 * These are constructed from files which are located in the IDX files.
 */
public class ReferenceTable {
    /**
     * Number of bytes that a index block is. Eg, two tribytes (6 bytes)
     */
    public static final int IDX_BLOCK_LEN = 6;
    public static final int BLOCK_HEADER_LEN = 8;
    public static final int BLOCK_LEN = 512;
    public static final int TOTAL_BLOCK_LEN = BLOCK_HEADER_LEN + BLOCK_LEN;

    /**
     * A flag which indicates this {@link ReferenceTable} contains
     * {@link Djb2} hashed identifiers.
     */
    public static final int FLAG_IDENTIFIERS = 0x01;

    /**
     * A flag which indicates this {@link ReferenceTable} contains
     * whirlpool digests for its entries.
     */
    public static final int FLAG_WHIRLPOOL = 0x02;
    private int idx;
    private int version;
    private int format;
    private int flags;
    private TreeMap<Integer, Reference> references;

    protected ReferenceTable(int idx) {

    }

    public static ReferenceTable decode(int idx, CacheFile file) {
        ReferenceTable t = new ReferenceTable(idx);

        ByteBuffer bb = file.getData();
        t.idx = idx;
        t.format = bb.get() & 0xFF;

        if (t.format >= 6) {
            t.version = bb.getInt();
        }

        t.flags = bb.get() & 0xFF;

		/* the number of references */
        int size = bb.getShort() & 0xFFFF;
        t.references = new TreeMap<Integer, Reference>();

		/* read the ids */
        int[] ids = new int[size];
        int accumulator = 0;
        size = -1;
        for (int i = 0; i < ids.length; i++) {
            int delta = bb.getShort() & 0xFFFF;
            ids[i] = accumulator += delta;
            if (ids[i] > size) {
                size = ids[i];
            }
        }
        size++;

		/* and allocate specific entries within that array */
        for (int id : ids) {
            t.references.put(id, new Reference());
            t.references.get(id).id = id;
        }

        if ((t.flags & FLAG_IDENTIFIERS) != 0) {
            for (int id : ids) {
                t.references.get(id).identifier = bb.getInt();
            }
        }

		/* read the CRC32 checksums */
        for (int id : ids) {
            t.references.get(id).crc = bb.getInt();
        }

		/* read the whirlpool digests if present */
        if ((t.flags & FLAG_WHIRLPOOL) != 0) {
            for (int id : ids) {
                bb.get(t.references.get(id).whirlpool);
            }
        }

		/* read the version numbers */
        for (int id : ids) {
            t.references.get(id).version = bb.getInt();
        }

		/* read the child sizes */
        int[][] members = new int[size][];
        for (int id : ids) {
            int length = bb.getShort() & 0xFFFF;
            members[id] = new int[length];
            t.references.get(id).children = new TreeMap<Integer, ChildReference>();
        }

		/* read the child ids */
        for (int id : ids) {
			/* reset the accumulator and size */
            accumulator = 0;
            size = -1;

			/* loop through the array of ids */
            for (int i = 0; i < members[id].length; i++) {
                int delta = bb.getShort() & 0xFFFF;
                members[id][i] = accumulator += delta;
                if (members[id][i] > size) {
                    size = members[id][i];
                }
            }
            size++;

			/* and allocate specific entries within the array */
            //for (int child : members[id]) {
            for (int i = 0; i < members[id].length; i++) {
                int child = members[id][i];
                ChildReference c = new ChildReference();
                c.id = child;
                t.references.get(id).children.put(i /* child*/, c);
            }
            //t.references.get(id).size = size;
        }

		/* read the child identifiers if present */
        if ((t.flags & FLAG_IDENTIFIERS) != 0) {
            for (int id : ids) {
                //for (int child : members[id]) {
                for (int i = 0; i < members[id].length; i++) {
                    //int child = members[id][i];
                    ChildReference c = t.references.get(id).children.get(i /*child*/);
                    c.identifier = bb.getInt();
                    //c.id = id;
                }
            }
        }

        return t;
    }

    public Reference getReferenceByHash(int hash) throws FileNotFoundException {
        for (Reference r : references.values()) {
            if (r.identifier == hash) return r;
        }
        throw new FileNotFoundException();
    }

    public Reference getReferenceByHash(String str) throws FileNotFoundException {
        return getReferenceByHash(Cache.getNameHash(str));
    }

    public void remove(int fileId) {
        references.remove(fileId);
    }

    /**
     * Encodes this {@link ReferenceTable} into a {@link ByteBuffer}.
     *
     * @return The {@link ByteBuffer}.
     * @throws IOException if an I/O error occurs.
     */
    public ByteBuffer encode() throws IOException {
		/* 
		 * we can't (easily) predict the size ahead of time, so we write to a
		 * stream and then to the buffer
		 */
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bout);
        try {
			/* write the header */
            os.write(format);
            if (format >= 6) {
                os.writeInt(version);
            }
            os.write(flags);

			/* calculate and write the number of non-null entries */
            os.writeShort(references.size());

			/* write the ids */
            int last = 0;
            for (int id = 0; id < capacity(); id++) {
                if (references.containsKey(id)) {
                    int delta = id - last;
                    os.writeShort(delta);
                    last = id;
                }
            }

			/* write the identifiers if required */
            if ((flags & FLAG_IDENTIFIERS) != 0) {
                for (Reference entry : references.values()) {
                    os.writeInt(entry.identifier);
                }
            }

			/* write the CRC checksums */
            for (Reference entry : references.values()) {
                os.writeInt(entry.crc);
            }

			/* write the whirlpool digests if required */
            if ((flags & FLAG_WHIRLPOOL) != 0) {
                for (Reference entry : references.values()) {
                    os.write(entry.whirlpool);
                }
            }

			/* write the versions */
            for (Reference entry : references.values()) {
                os.writeInt(entry.version);
            }

			/* calculate and write the number of non-null child entries */
            for (Reference entry : references.values()) {
                os.writeShort(entry.children.size());
            }

			/* write the child ids */
            for (Reference entry : references.values()) {
                last = 0;
                for (int id = 0; id < entry.capacity(); id++) {
                    if (entry.children.containsKey(id)) {
                        int childId = entry.children.get(id).id;
                        int delta = childId - last;
                        os.writeShort(delta);
                        last = childId;
                    }
                }
            }

			/* write the child identifiers if required  */
            if ((flags & FLAG_IDENTIFIERS) != 0) {
                for (Reference entry : references.values()) {
                    for (ChildReference child : entry.children.values()) {
                        os.writeInt(child.identifier);
                    }
                }
            }

			/* convert the stream to a byte array and then wrap a buffer */
            byte[] bytes = bout.toByteArray();
            return ByteBuffer.wrap(bytes);
        } finally {
            os.close();
        }
    }

    public int capacity() {
        if (references.isEmpty()) {
            return 0;
        }

        return references.lastKey() + 1;
    }

    public Collection<Reference> getReferences() {
        return Collections.unmodifiableCollection(this.references.values());
    }

    /**
     * Fetch the reference by ID, may be null
     *
     * @param fileId the file's id
     * @return the reference, may be null
     */
    public Reference getReference(int fileId) {
        return references.get(fileId);
    }

    public int getIDX() {
        return idx;
    }

    public int getFormat() {
        return format;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getFlags() {
        return flags;
    }

    public boolean isWhirlpool() {
        return (flags & FLAG_WHIRLPOOL) != 0;
    }

    public boolean isNamed() {
        return (flags & FLAG_IDENTIFIERS) != 0;
    }

    public int size() {

        return references.size();
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;

        ReferenceTable rt = (ReferenceTable) o;
        if (rt.version != this.version) return false;
        if (rt.idx != this.idx) return false;
        if (rt.format != this.format) return false;
        if (rt.references.size() != this.references.size()) return false;
        for (Reference r : this.references.values()) {
            Reference s = rt.references.get(r.getId());
            if (s.crc != r.crc) return false;
            if (s.id != r.id) return false;
            if (s.identifier != r.identifier) return false;
            if (s.version != r.version) return false;
            for (int i = 0; i < s.whirlpool.length; i++) {
                if (s.whirlpool[i] != r.whirlpool[i]) return false;
            }
            if (s.children.size() != r.children.size()) return false;
            for (ChildReference cr : r.children.values()) {
                ChildReference sr = s.children.get(cr.getId());
                if (sr.id != cr.id) return false;
                if (sr.identifier != cr.identifier) return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int total = 0;

        total += this.version << 24;
        total += this.idx << 20;
        total += this.format << 19;
        total += this.references.size() << 10;
        return total;
    }
}