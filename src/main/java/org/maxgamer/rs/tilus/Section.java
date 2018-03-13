package org.maxgamer.rs.tilus;

import org.maxgamer.rs.tilus.paths.Coordinate;
import org.maxgamer.rs.tilus.paths.Move;
import org.maxgamer.rs.tilus.paths.Plan;

/**
 * TODO: Document this
 */
public abstract class Section {
    private Section[][] neighbours = new Section[3][3];
    protected final int size;
    protected Coordinate min;

    public Section(int size) {
        this.size = size;
    }

    public final Coordinate getMin() {
        return min;
    }

    public void init(Dimension dimension, int sx, int sy) {
        min = new Coordinate(sx * size, sy * size);

        for (int x = -1; x <= 1; x++) {
            if (sx + x < 0) continue;
            if (sx + x >= dimension.getWidth()) continue;

            for (int y = -1; y <= 1; y++) {
                if (sy + y < 0) continue;
                if (sy + y >= dimension.getHeight()) continue;

                // Get the neighbour we've moved next to
                Section neighbour = dimension.get(sx + x, sy + y);

                // Mark them as our neighbour
                neighbours[x + 1][y + 1] = neighbour;

                // Maybe we live next to nobody
                if (neighbour == null) continue;

                // Our neighbour should know we've moved in. If not, inform them
                if (neighbour.neighbour(-x, -y) != this) {
                    neighbour.init(dimension, sx + x, sy + y);
                }
            }
        }
    }

    public final Section neighbour(int dx, int dy) {
        Section neighbour = neighbours[dx + 1][dy + 1];

        dx -= signum(dx);
        dy -= signum(dy);

        if (dx == 0 && dy == 0) return neighbour;

        return neighbour.neighbour(dx, dy);
    }

    protected static final int signum(int v) {
        if (v >= 1) return 1;
        if (v <= -1) return -1;

        return 0;
    }
    
    public boolean contains(Coordinate coordinate) {
        if (coordinate.x < this.min.x) return false;
        if (coordinate.y < this.min.y) return false;
        if (coordinate.x >= this.min.x + size) return false;
        if (coordinate.y >= this.min.y + size) return false;

        return true;
    }

    public abstract void set(int x, int y, int clip);

    public abstract void unset(int x, int y, int clip);

    public abstract int get(int x, int y);

    public abstract void visit(Plan plan, Move move);

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "Section(" + this.min.x / size + ", " + this.min.y / size + ")";
    }
}
