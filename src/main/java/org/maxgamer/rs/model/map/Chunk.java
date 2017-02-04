package org.maxgamer.rs.model.map;

/**
 * @author netherfoam
 */
public class Chunk {
    /**
     * Area is marked as a bridge. Dementhium did an odd thing with the height
     * here, though I'm not sure why. Perhaps because it used mob pathfinding
     * for projectile pathfinding?
     */
    public static final int FLAG_BRIDGE = 0x2;

    /**
     * Area is clipped for one reason or another (Such as water/lava)
     */
    public static final int FLAG_CLIP = 0x1;

    /**
     * Flag given if there is a roof over this piece of terrain. This is of no
     * known consequence.
     */
    public static final int FLAG_ROOF = 0x4;

    /**
     * Steep cliff flag? Elevation flag? These appear to be sharp edges in
     * terrain which is not climbable.
     */
    public static final int FLAG_UNKNOWN = 0x8;

    /**
     * I'm hazy on this, it could be a wall flag or a steep wall flag. I'm of
     * the opinion that any area marked with this is already marked with a
     * FLAG_CLIP flag, or an object which doesn't allow passing.
     */
    public static final int FLAG_UNKNOWN2 = 0x10;

    /**
     * The X coordinate, in the cache, of this chunk
     */
    private int cacheX;

    /**
     * The Y coordinate, in the cache, of this chunk
     */
    private int cacheY;

    /**
     * The clip flags for this chunk. Indexes are [x][y], where x and y are 0 to
     * 7. May be null, in which case all values can be assumed to be -1
     */
    private int[][] clip; //x, y

    /**
     * The flags such as indoors or bridge flags. Indexes are [x][y] where x and
     * y are 0 to 7. May be null, in which case all values are assumed to be 0
     */
    private byte[][] flags;

    /**
     * True if this chunk has been initialized with gameobjects and clip
     */
    private boolean loaded = false;

    /**
     * The Z coordinate, in the cache, of this chunk
     */
    private int z;

    /**
     * Constructs a new chunk, with the given values representing the location
     * from the cache to load the data from. These do not have to correspond to
     * the real location of the chunk, unless in a StandardMap which is not
     * customizable
     *
     * @param cacheX the cache x value of the chunk (x >> 3)
     * @param cacheY the cache y value of the chunk (y >> 3)
     * @param z      the cache z value of the chunk
     */
    public Chunk(int cacheX, int cacheY, int z) {
        this.cacheX = cacheX;
        this.cacheY = cacheY;
        this.z = z;
    }

    public void addClip(int x, int y, int clip) {
        if (this.clip == null) {
            this.clip = new int[WorldMap.CHUNK_SIZE][WorldMap.CHUNK_SIZE];
        }
        this.clip[x][y] |= clip;
    }

    public int getCacheX() {
        return this.cacheX;
    }

    public int getCacheY() {
        return this.cacheY;
    }

    public int getCacheZ() {
        return this.z;
    }

    public int getClip(int x, int y) {
        if (!this.isLoaded()) {
            return ClipMasks.UNLOADED_TILE;
        }
        if (this.clip == null) {
            return 0; //No clips here!
        }
        return this.clip[x][y];
    }

    /**
     * Returns the flags for the given sub coordinate
     *
     * @param x the x coordinate, 0 to 7
     * @param y the y coordinate, 0 to 7
     * @return the flags or 0 if none
     */
    public int getFlags(int x, int y) {
        if (this.flags == null) {
            return 0;
        }
        return this.flags[x][y];
    }

    /**
     * Returns true if this chunk has all of the given flags at the given
     * location
     *
     * @param x    the x coordinate, 0 to 7
     * @param y    the y coordinate, 0 to 7
     * @param flag the flags to check for. May be combination of several flags
     *             by using bitwise OR
     * @return true if this chunk has all of the given flags
     */
    public boolean hasFlag(int x, int y, int flag) {
        return (this.flags[x][y] & flag) == flag;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void removeClip(int x, int y, int clip) {
        if (this.clip == null) {
            return;
        }
        this.clip[x][y] &= ~clip;
    }

    /**
     * Sets the flag for this chunk at the given coordinate to the given flag.
     * This sets, and does not bitwise OR.
     *
     * @param x    the x coorindate
     * @param y    the y coordinate
     * @param flag the flag value
     */
    public void setFlag(int x, int y, int flag) {
        if (flag == 0 && this.flags == null) {
            return; //It's assumed to be 0 already. This saves us allocating extra data.
        }
        if (this.flags == null) {
            this.flags = new byte[WorldMap.CHUNK_SIZE][WorldMap.CHUNK_SIZE];
        }
        this.flags[x][y] = (byte) flag;
    }

    @Override
    public String toString() {
        return "Chunk(" + cacheX + ", " + cacheY + ") isLoaded(" + isLoaded() + ")";
    }
}