package org.maxgamer.rs.tiler;

import org.maxgamer.rs.tiler.paths.Move;
import org.maxgamer.rs.tiler.paths.Plan;

/**
 * TODO: Document this
 */
public abstract class Section {
    private Section[][] neighbours = new Section[3][3];

    public void init(Dimension dimension, int sx, int sy) {
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
        // TODO: safety check dx and dy are -1 to +1
        return neighbours[dx + 1][dy + 1];
    }

    public abstract void set(int x, int y, int clip);

    public abstract void unset(int x, int y, int clip);

    public abstract int get(int x, int y);

    public abstract void visit(Plan plan, Move move);
}
