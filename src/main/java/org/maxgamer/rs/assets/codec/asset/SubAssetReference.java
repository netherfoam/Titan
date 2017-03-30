package org.maxgamer.rs.assets.codec.asset;

/**
 * Represents a reference for an item inside an archive from a runescape cache.
 *
 * @author netherfoam
 */
public final class SubAssetReference {
    protected int id;

    /**
     * A hash of the file's original name.
     */
    protected int identifier;

    protected SubAssetReference() {
    }

    public SubAssetReference(int id, int identifier) {
        this.id = id;
        this.identifier = identifier;
    }

    /**
     * The unique identifier for this reference. This is created by hashing a name
     * using the cache's hashing function. Note that the uniqueness is not enforced.
     *
     * @return the unique identifier
     */
    public int getIdentifier() {
        return identifier;
    }

    public int getId() {
        return id;
    }

    public SubAssetReference copy() {
        return new SubAssetReference(id, identifier);
    }
}