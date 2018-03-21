package org.maxgamer.rs.tilus.paths;

import org.maxgamer.rs.tilus.Section;
import org.maxgamer.rs.tilus.paths.destination.Destination;

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
    private Move last;

    private Coordinate start;
    private Destination end;
    private Size size;
    private int speed;
    private int z;

    public Plan(PlanBuilder builder) {
        this.start = builder.start;
        this.z = builder.z;
        this.end = builder.end;
        this.size = builder.size;
        this.speed = builder.speed;

        moves.add(new Move(null, 0, 0, 0, this.start, null, this.end));
        blacklisted.add(this.start);
    }

    public void forward() {
        if (moves.isEmpty()) {
            return;
        }

        Move next = moves.remove();

        if (next.getHeuristic() <= 0) {
            // Success! We've found a move that ends here
            this.last = next;
            return;
        }

        Section from = next.getFrom();
        Coordinate endingCoordinate = next.getEndingCoordinate();

        Section section;
        if (!from.contains(endingCoordinate)) {
            Coordinate min = from.getMin();
            if (endingCoordinate.x < 0 || endingCoordinate.y < 0) {
                // We've gone too far off the map!
                forward();
                return;
            }

            // We've moved off of this section with the move!
            // So, get the appropriate neighbour that we want to move to
            int size = from.size();
            int sdx = (endingCoordinate.x / size) - (min.x / size);
            int sdy = (endingCoordinate.y / size) - (min.y / size);

            section = from.neighbour(sdx, sdy);

            if (section == null) {
                // We're considering moving into a section which doesn't exist
                // We do this here because it's faster than checking every single
                // optional move we may or may not pursue.
                forward();
                return;
            }

            if (!section.contains(endingCoordinate)) {
                throw new IllegalArgumentException("Section doesn't contain starting position??");
            }
        } else {
            // We're still running in the same section
            section = from;
        }

        section.visit(this, next);
    }

    public boolean isFailed() {
        return moves.isEmpty();
    }

    public Queue<Move> getMoves() {
        return moves;
    }

    public Set<Coordinate> getBlacklisted() {
        return blacklisted;
    }

    public Move getLast() {
        return last;
    }

    public Coordinate getStart() {
        return start;
    }

    public Destination getEnd() {
        return end;
    }

    public Size getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public int getZ() {
        return z;
    }
}
