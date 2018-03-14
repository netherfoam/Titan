package org.maxgamer.rs.tilus.paths;

/**
 * TODO: Document this
 */
public class PlanBuilder {
    public static PlanBuilder create() {
        return new PlanBuilder();
    }

    protected Coordinate start;
    protected DestinationBounds end;
    protected Size size = new Size(1, 1);
    protected int speed = 1;

    public PlanBuilder() {
    }

    public PlanBuilder start(Coordinate start) {
        this.start = start;

        return this;
    }

    public PlanBuilder end(Coordinate min, Coordinate max, DestinationBounds.Type type) {
        end(new DestinationBounds(min, max, type));

        return this;
    }

    public PlanBuilder end(DestinationBounds end) {
        this.end = end;

        return this;
    }

    public PlanBuilder size(int width, int height) {
        this.size = new Size(width, height);

        return this;
    }

    public void speed(int speed) {
        this.speed = speed;
    }

    public Plan build() {
        return new Plan(this);
    }
}
