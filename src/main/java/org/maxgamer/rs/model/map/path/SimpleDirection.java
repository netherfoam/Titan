package org.maxgamer.rs.model.map.path;

import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class SimpleDirection extends Direction {

    /**
     * Positive represents the first, negative is the second
     * @param dx Change in East/West
     * @param dy Change in North/South
     */
    protected SimpleDirection(int dx, int dy, int clipTo, int clipFrom) {
        super(dx, dy, clipTo, clipFrom);
    }

    /*
     * public int conflict(Location from) { //This seems to work now. return
     * (from.getMap().getClip(from.x + dx, from.y + dy, from.z) & clipTo) |
     * (from.getMap().getClip(from.x, from.y, from.z) & clipFrom); }
     */

    public int conflictTo(Location from) {
        return (from.getMap().getClip(from.x + dx, from.y + dy, from.z) & clipTo);
    }

    public int conflictFrom(Location from) {
        return (from.getMap().getClip(from.x, from.y, from.z) & clipFrom);
    }

    public boolean canShoot(Location from) {
        //TODO: This only works by checking the target tile, it should probably check the current tile too.
        if (conflict(from) == 0) return true;

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