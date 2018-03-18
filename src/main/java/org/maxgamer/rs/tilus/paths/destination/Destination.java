package org.maxgamer.rs.tilus.paths.destination;

import org.maxgamer.rs.tilus.paths.Coordinate;

/**
 * Interface which provides a heuristic calculation of the cost of moving from the given tile to this destination.
 */
public interface Destination {
    /**
     * Provides a minimum cost from the given coordinate to the ending coordinate. If this returns zero, then the
     * path finding will cease and the result will be marked as successful.
     *
     * @param from the coordinate to start at
     * @return the approximation of cost or a value less than or equal to 0 if the destination accepts the given coordinate
     */
    int minimumCost(Coordinate from);
}
