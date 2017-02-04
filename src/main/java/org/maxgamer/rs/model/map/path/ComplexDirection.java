package org.maxgamer.rs.model.map.path;

import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class ComplexDirection extends Direction {
    public final SimpleDirection dir1;
    public final SimpleDirection dir2;

    /**
     * Constructs a new ComplexDirection.
     *
     * @param dir1     the first simple direction eg, NORTH
     * @param dir2     the second simple direction eg, EAST
     * @param clipTo   Clip masks the destination tile CANNOT have to allow
     *                 movement
     * @param clipFrom Clip masks the current tile CANNOT have to allow movement
     */
    protected ComplexDirection(SimpleDirection dir1, SimpleDirection dir2, int clipTo, int clipFrom) {
        super(dir1.dx + dir2.dx, dir1.dy + dir2.dy, clipTo | dir1.clipTo | dir2.clipTo, clipFrom | dir1.clipFrom | dir2.clipFrom);
        this.dir1 = dir1;
        this.dir2 = dir2;
    }

    /*
     * public int conflict(Location from) { return conflictTo(from) |
     * conflictFrom(from); }
     */

    public int conflictTo(Location from) {
        return this.dir1.conflictTo(from) | dir2.conflictTo(from) | (from.getMap().getClip(from.x + dx, from.y + dy, from.z) & clipTo);
    }

    public int conflictFrom(Location from) {
        return this.dir1.conflictFrom(from) | dir2.conflictFrom(from) | (from.getMap().getClip(from.x, from.y, from.z) & clipFrom);
    }

    public boolean canShoot(Location from) {
        //TODO: This only works by checking the target tile, it should probably check the current tile too.
        if (conflict(from) == 0) {
            return true;
        }

        int to = from.getMap().getClip(from.x + dx, from.y + dy, from.z) & clipTo;

        int rangeFlags = (to & ClipMasks.WALL_ALLOW_RANGE_ALL) >> 22;
        to = to & ~(rangeFlags);
        to = to & ~(rangeFlags << 9);

        return (to & this.clipTo) == 0;
    }

    @Override
    public String toString() {
        return "(" + dx + "," + dy + ")";
    }

    @Override
    public int getWalkMask() {
        return clipTo;
    }
}