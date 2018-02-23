package org.maxgamer.rs.tiler.paths;

/**
 * TODO: Document this
 */
public class PlanParameterBuilder {
    private Coordinate start;
    private DestinationBounds end;
    private Size size = new Size(1, 1);

    public PlanParameterBuilder() {
    }

    public PlanParameterBuilder start(Coordinate start) {
        this.start = start;

        return this;
    }

    public PlanParameterBuilder end(Coordinate min, Coordinate max, DestinationBounds.Type type) {
        end(new DestinationBounds(min, max, type));

        return this;
    }

    public PlanParameterBuilder end(DestinationBounds end) {
        this.end = end;

        return this;
    }

    public PlanParameterBuilder size(int width, int height) {
        this.size = new Size(width, height);

        return this;
    }

    public PlanParameters build() {
        return new PlanParameters(start, end, size);
    }
}
