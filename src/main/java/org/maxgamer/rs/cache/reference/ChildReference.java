package org.maxgamer.rs.cache.reference;

/**
 * Represents a reference for an item inside an archive from a runescape cache.
 *
 * @author netherfoam
 */
public class ChildReference {
    /**
     * A hash of the file's original name.
     */
    protected int identifier;

    /**
     * Child ID, unique when combined with parent
     */
    protected int id;

    /**
     * The unique identifier for this reference. This is created by hashing a name
     * using the cache's hashing function. Note that the uniqueness is not enforced.
     *
     * @return the unique identifier
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * The position of this child in the archive, from 0 to (archive size) - 1.
     *
     * @return the unique ID for this child file.
     */
    public int getId() {
        return id;
    }
}