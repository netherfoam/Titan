package org.maxgamer.rs.tilus.paths.destination;

import org.maxgamer.rs.tilus.paths.Coordinate;

/**
 * TODO: Document this
 */
public class Destinations {
    public static CoordinateDestination on(Coordinate goal) {
        return new CoordinateDestination(goal);
    }

    public static BoundsDestination inside(Coordinate min, Coordinate max) {
        return new BoundsDestination(min, max);
    }

    public static BesideBoundsDestination beside(Coordinate min, Coordinate max) {
        return new BesideBoundsDestination(min, max);
    }

    public static BesideBoundsDestination beside(Coordinate min, Coordinate max, boolean allowDiagonal) {
        if (allowDiagonal) return beside(min, max);

        return new DirectBesideBoundsDestination(min, max);
    }

    private Destinations() {
        // Static class, so here's private constructor
    }
}
