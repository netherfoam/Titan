package org.maxgamer.rs.tiler.paths;

/**
 * TODO: Document this
 */
public class PlanParameters {
    public final Coordinate start;
    public final DestinationBounds end;
    public final Size size;

    public PlanParameters(Coordinate start, DestinationBounds end, Size size) {
        this.start = start;
        this.end = end;
        this.size = size;
    }
}
