package org.maxgamer.rs.tilus.paths;

/**
 * TODO: Document this
 */
public class DestinationBounds implements Destination {
    private final Coordinate min;
    private final Coordinate max;
    private final Type type;

    public DestinationBounds(Coordinate min, Coordinate max, Type type) {
        if (min.x > max.x) throw new IllegalArgumentException("require min.x <= max.x, gave " + min.x + " <= " + max.x);
        if (min.y > max.y) throw new IllegalArgumentException("require min.y <= max.y, gave " + min.y + " <= " + max.y);

        this.min = min;
        this.max = max;
        this.type = type;
    }

    @Override
    public int minimumCost(Coordinate from) {
        Coordinate closest = closest(from);

        return closest.distanceSq(from);
    }

    private Coordinate closest(Coordinate from) {
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
        // TODO: maybe these belong in a subclass of DestinationBounds instead?
        INSIDE,
        BESIDE,
        RANGE,
    }
}
