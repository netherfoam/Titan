package org.maxgamer.rs.tiler.paths;

import org.maxgamer.rs.tiler.Dimension;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * TODO: Document this
 */
public class Plan {
    private Queue<Move> moves = new PriorityQueue<>();
    private Set<Coordinate> blacklisted = new HashSet<>(32);

    private PlanParameters parameters;

    public Plan(PlanParameters parameters) {
        this.parameters = parameters;

        moves.add(new Move(null, 0, 0, 0));
    }

    public Move path(Dimension dimension) {
        // TODO: find a path
        return null;
    }

    public boolean isFailed() {
        return moves.isEmpty();
    }
}
