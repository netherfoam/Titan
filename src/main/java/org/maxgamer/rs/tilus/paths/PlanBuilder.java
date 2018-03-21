package org.maxgamer.rs.tilus.paths;

import org.maxgamer.rs.tilus.paths.destination.Destination;

/**
 * TODO: Document this
 */
public class PlanBuilder {
    public static PlanBuilder create() {
        return new PlanBuilder();
    }

    protected Coordinate start;
    protected Destination end;
    protected Size size = new Size(1, 1);
    protected int speed = 1;
    protected int z;

    public PlanBuilder() {
    }

    public PlanBuilder start(Coordinate start, int z) {
        this.start = start;
        this.z = z;

        return this;
    }

    public PlanBuilder end(Destination end) {
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
