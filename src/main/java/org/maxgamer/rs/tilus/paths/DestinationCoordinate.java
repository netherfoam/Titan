package org.maxgamer.rs.tilus.paths;

/**
 * TODO: Document this
 */
public class DestinationCoordinate implements Destination {
    private Coordinate coordinate;

    public DestinationCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public int minimumCost(Coordinate from) {
        return from.distanceSq(this.coordinate);
    }
}
