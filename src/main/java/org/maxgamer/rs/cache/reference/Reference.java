package org.maxgamer.rs.cache.reference;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;


public class Reference {
    //public int size;

    /**
     * A hash of the file's original name.
     */
    protected int identifier;

    /**
     * The CRC32 checksum of this entry.
     */
    protected int crc;

    /**
     * The whirlpool digest of this entry.
     */
    protected byte[] whirlpool = new byte[64];

    /**
     * The version of this entry.
     */
    protected int version;

    /**
     * File number, not consecutive
     */
    protected int id;

    protected TreeMap<Integer, ChildReference> children;

    public int getIdentifier() {
        return identifier;
    }

    public int getCRC() {
        return crc;
    }

    public byte[] getWhirlpool() {
        return whirlpool.clone();
    }

    public int getVersion() {
        return version;
    }

    public int getId() {
        return id;
    }

    public ChildReference getChild(int id) {
        if (children == null) return null;
        return children.get(id);
    }

    public Collection<ChildReference> getChildren() {
        return children.values();
    }

    /**
     * The number of children this file has. If this file has no children,
     * then this value is 1 (Eg, it's just the one file).
     *
     * @return the number of children. 1-n
     */
    public int getChildCount() {
        if (children == null) return 0;
        return children.size();
    }

    /**
     * Gets the maximum number of child entries.
     *
     * @return The maximum number of child entries.
     */
    public int capacity() {
        if (children.isEmpty()) {
            return 0;
        }

        Iterator<ChildReference> rit = children.values().iterator();
        ChildReference r = rit.next();
        while (rit.hasNext()) {
            r = rit.next();
        }
        return r.id + 1;
    }

	/*public int getMaxChildId(){
		return size;
	}*/
}