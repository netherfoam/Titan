package org.maxgamer.rs.tiler.paths;

/**
 * TODO: Document this
 */
public class Move {
    private Move previous;
    private int cost;
    private int dx;
    private int dy;

    public Move(Move previous, int cost, int dx, int dy) {
        this.previous = previous;
        this.cost = cost;
        this.dx = dx;
        this.dy = dy;
    }
}
