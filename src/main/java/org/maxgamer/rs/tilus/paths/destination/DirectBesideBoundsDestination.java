package org.maxgamer.rs.tilus.paths.destination;

import org.maxgamer.rs.tilus.paths.Coordinate;

/**
 * Complex implementation of Destination which desires to be beside a given set of bounds, without accepting tiles
 * which are diagonal as a solution
 */
public class DirectBesideBoundsDestination extends BesideBoundsDestination {
    public DirectBesideBoundsDestination(Coordinate min, Coordinate max) {
        super(min, max);
    }

    @Override
    public Coordinate closest(Coordinate from) {
        Coordinate closest = super.closest(from);
        int x = closest.x;
        int y = closest.y;

        if (closest.x == min.x - 1 && closest.y == min.y - 1) {
            // This is a diagonal on the bottom left corner
            // So we make it the tile to the left of the top left corner.
            y = min.y;
        }

        if (closest.x == max.x + 1 && closest.y == max.y + 1) {
            // This is a diagonal on the top right corner
            // So we make it the tile to the right of the bottom right corner
            y = max.y;
        }

        return new Coordinate(x, y);
    }
}
