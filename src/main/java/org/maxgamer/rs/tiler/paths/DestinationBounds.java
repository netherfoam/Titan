package org.maxgamer.rs.tiler.paths;

/**
 * TODO: Document this
 */
public class DestinationBounds {
    public final Coordinate min;
    public final Coordinate max;
    public final Type type;

    public DestinationBounds(Coordinate min, Coordinate max, Type type) {
        this.min = min;
        this.max = max;
        this.type = type;
    }

    public enum Type {
        INSIDE,
        BESIDE,
        RANGE,
    }
}
