package org.maxgamer.rs.model.map.path;

import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public abstract class Direction {
    public final int dx;
    public final int dy;
    public final int clipTo;
    public final int clipFrom;

    protected Direction(int dx, int dy, int clipTo, int clipFrom) {
        this.dx = dx;
        this.dy = dy;
        this.clipTo = clipTo;
        this.clipFrom = clipFrom;
    }

    public final boolean canWalk(Location from) {
        return conflict(from) == 0;
    }

    public final int conflict(Location from) {
        return conflictTo(from) + conflictFrom(from);
    }

    /**
     * Returns true if you can walk this direction, from the given location. Eg,
     * does this tile let you walk north?
     *
     * @param from the location starting from
     * @return true if a single tile entity can move north, false
     */
    //public abstract int[] conflict(Location from);
    public abstract int conflictFrom(Location from);

    public abstract int conflictTo(Location to);

    public abstract int getWalkMask();

    /**
     * Returns true if you can shoot in this direction, from the given location.
     * Eg, does this tile let you shoot north?
     *
     * @param from the location starting from
     * @return true if a single tile entity can shoot north, false
     */
    public abstract boolean canShoot(Location from);
}