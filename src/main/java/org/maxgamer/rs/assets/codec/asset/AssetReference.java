package org.maxgamer.rs.assets.codec.asset;

public class AssetReference {
    private static final byte[] EMPTY_WHIRLPOOL = new byte[64];

    public static AssetReference create(int version, SubAssetReference... children) {
        return new AssetReference(0, 0, EMPTY_WHIRLPOOL, version, children);
    }

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

    protected SubAssetReference[] children;

    protected AssetReference() {
    }

    public AssetReference(int identifier, int crc, byte[] whirlpool, int version, int numChildren) {
        this.identifier = identifier;
        this.crc = crc;
        this.whirlpool = whirlpool;
        this.version = version;
        this.children = new SubAssetReference[numChildren];
    }

    public AssetReference(int identifier, int crc, byte[] whirlpool, int version, SubAssetReference[] children) {
        this(identifier, crc, whirlpool, version, 0);
        this.children = children.clone();
    }

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

    public SubAssetReference getChild(int id) {
        return children[id];
    }

    public SubAssetReference[] getChildren() {
        return children.clone();
    }

    public int indexOf(int id) {
        for(int i = 0; i < children.length; i++) {
            SubAssetReference child = children[i];
            if(child.getId() == id) return i;
        }

        return -1;
    }

    /**
     * The number of children this file has. If this file has no children,
     * then this value is 1 (Eg, it's just the one file).
     *
     * @return the number of children. 1-n
     */
    public int getChildCount() {
        return children.length;
    }

    public AssetReference copy() {
        AssetReference c = new AssetReference();
        c.crc = this.crc;
        c.identifier = this.identifier;
        c.version = this.version;

        // We copy on read, so this array is fine to pass around as a reference
        c.whirlpool = this.whirlpool;
        c.children = this.children.clone();

        return c;
    }
}