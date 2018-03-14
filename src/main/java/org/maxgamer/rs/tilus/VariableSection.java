package org.maxgamer.rs.tilus;

import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.tilus.paths.Coordinate;
import org.maxgamer.rs.tilus.paths.Move;
import org.maxgamer.rs.tilus.paths.Plan;

import java.util.Queue;
import java.util.Set;

/**
 * TODO: Document this
 *
 * TODO: safety checks on bounds
 */
public class VariableSection extends Section {
    private final int[] tiles;

    public VariableSection(int size) {
        super(size);
        this.tiles = new int[size * size];
    }

    private void validate(int x, int y) {
        if (x >= size) throw new IllegalArgumentException("Given x " + x + ", but size is " + size);
        if (y >= size) throw new IllegalArgumentException("Given y " + y + ", but size is " + size);

        if (x < 0) throw new IllegalArgumentException("Given x " + x +", but must be >= 0");
        if (y < 0) throw new IllegalArgumentException("Given y " + y +", but must be >= 0");
    }

    @Override
    public void set(int x, int y, int clip) {
        validate(x, y);
        tiles[x * size + y] |= clip;
    }

    @Override
    public void unset(int x, int y, int clip) {
        validate(x, y);
        tiles[x * size + y] &= ~clip;
    }

    @Override
    public int get(int x, int y) {
        validate(x, y);
        return tiles[x * size + y];
    }

    @Override
    public void visit(Plan plan, Move curMove) {
        // Find a path
        int maxDelta = plan.getSpeed();

        Coordinate curPos = curMove.getEndingCoordinate();
        if (!canMoveTo(curPos, curMove.getDx(), curMove.getDy())) {
            // We can't enter the tile from that direction
            plan.forward();
            return;
        }

        Set<Coordinate> blacklist = plan.getBlacklisted();
        Queue<Move> moveQueue = plan.getMoves();

        if (!this.contains(curPos)) {
            throw new IllegalStateException("Section doesn't contain starting position?");
        }

        for (int dx = -maxDelta; dx <= maxDelta; dx++) {
            for (int dy = -maxDelta; dy <= maxDelta; dy++) {
                if (dx == 0 && dy == 0) {
                    // This tile is the starting tile
                    continue;
                }

                Coordinate nextPos = new Coordinate(curPos.x + dx, curPos.y + dy);
                if (!blacklist.add(nextPos)) {
                    // This position has already been checked
                    continue;
                }

                // Determine if the clip is suitable to leave in this direction
                if (!canMoveFrom(curPos, dx, dy)) {
                    continue;
                }

                Move nextMove = new Move(curMove, curMove.getCost() + 1, dx, dy, nextPos, this, plan.getEnd());
                moveQueue.add(nextMove);
            }
        }

        if (moveQueue.isEmpty()) {
            // TODO: Exception
            return;
        }

        plan.forward();
    }

    public boolean canMoveFrom(Coordinate from, int dx, int dy) {
        // TODO: expensive divide by operation here
        int x1 = from.x % size;
        int y1 = from.y % size;
        int clipFrom = get(x1, y1);

        if ((clipFrom & ClipMasks.BLOCKED_TILE) != 0) {
            return false;
        }

        return true;
    }

    public boolean canMoveTo(Coordinate to, int dx, int dy) {
        //int x1 = from.x % size;
        //int y1 = from.y % size;

        int x2 = to.x; //x1 + dx;
        int y2 = to.y; //y1 + dy;

        Section section;
        if (!this.contains(new Coordinate(to.x, to.y))) {
            // We've moved off of this section with the move!
            // So, get the appropriate neighbour that we want to move to
            int sigX = signum(dx);
            int sigY = signum(dy);

            int size = this.size();
            int sdx = (dx + (sigX * size - sigX)) / size;
            int sdy = (dy + (sigY * size - sigY)) / size;

            section = this.neighbour(sdx, sdy);

            if (section == null) {
                // We're considering moving into a section which doesn't exist
                return false;
            }
        } else {
            section = this;
        }

        // TODO: expensive divide by operation here
        // Wrap these around
        x2 = Math.abs(x2 % size);
        y2 = Math.abs(y2 % size);

        int clipTo = section.get(x2, y2);

        if ((clipTo & ClipMasks.BLOCKED_TILE) != 0) {
            return false;
        }

        return true;
    }
}
