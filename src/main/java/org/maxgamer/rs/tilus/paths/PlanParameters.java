package org.maxgamer.rs.tilus.paths;

/**
 * TODO: Document this
 */
public class PlanParameters {
    public final Coordinate start;
    public final DestinationBounds end;
    public final Size size;
    public final int speed;

    public PlanParameters(Coordinate start, DestinationBounds end, Size size, int speed) {
        this.start = start;
        this.end = end;
        this.size = size;
        this.speed = speed;
    }
}
