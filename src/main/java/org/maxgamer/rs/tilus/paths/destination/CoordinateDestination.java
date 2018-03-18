package org.maxgamer.rs.tilus.paths.destination;

import org.maxgamer.rs.tilus.paths.Coordinate;

/**
 * Simplest implementation of Destination which desires to be on a given coordinate
 */
public class CoordinateDestination implements Destination {
    private Coordinate coordinate;

    public CoordinateDestination(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public int minimumCost(Coordinate from) {
        return from.distanceSq(this.coordinate);
    }
}
