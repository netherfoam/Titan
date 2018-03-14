package org.maxgamer.rs.tilus.paths;

/**
 * TODO: Document this
 */
public class Coordinate {
    public final int x;
    public final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distanceSq(Coordinate c) {
        int dx = c.x - this.x;
        int dy = c.y - this.y;

        return dx * dx + dy * dy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != that.x) return false;

        return y == that.y;
    }

    @Override
    public int hashCode() {
        return (x << 16 | y);
    }
}
