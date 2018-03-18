package org.maxgamer.rs.tilus.paths.destination;

import org.maxgamer.rs.tilus.paths.Coordinate;

/**
 * Implementation of Destination which desires to be inside a given rectangle.
 */
public class BoundsDestination implements Destination {
    protected final Coordinate min;
    protected final Coordinate max;

    public BoundsDestination(Coordinate min, Coordinate max) {
        if (min.x > max.x) throw new IllegalArgumentException("require min.x <= max.x, gave " + min.x + " <= " + max.x);
        if (min.y > max.y) throw new IllegalArgumentException("require min.y <= max.y, gave " + min.y + " <= " + max.y);

        this.min = min;
        this.max = max;
    }

    @Override
    public int minimumCost(Coordinate from) {
        Coordinate closest = closest(from);

        return closest.distanceSq(from);
    }

    public Coordinate closest(Coordinate from) {
        int x = from.x;
        int y = from.y;

        if (min.x > from.x) x = min.x;
        if (min.y > from.y) y = min.y;

        if (max.x < from.x) x = max.x;
        if (max.y < from.y) y = max.y;

        return new Coordinate(x, y);
    }

    public enum Type {
        // TODO: Account for these
        // TODO: maybe these belong in a subclass of BoundsDestination instead?
        INSIDE,
        BESIDE,
        RANGE,
    }
}
