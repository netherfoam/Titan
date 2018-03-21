package org.maxgamer.rs.model.map;

import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;

/**
 * @author netherfoam
 */
public class Position implements MBR {
    /**
     * 14 bits size, or 16383. This is hardcoded in the client
     */
    public static final int MAX_SIZE = 0x3FFF;

    /**
     * The x value for this position
     */
    public final int x;
    /**
     * The y value for this position
     */
    public final int y;

    /**
     * Constructs a new position with the given values. If the given values are
     * outside of the required range (less than 0 or greater than 16383), then
     * they are set to 0 or 16383, respectively.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Position(int x, int y) {
        if (x < 0) x = 0;
        else if (x > MAX_SIZE) x = MAX_SIZE;

        if (y < 0) y = 0;
        else if (y > MAX_SIZE) y = MAX_SIZE;

        this.x = x;
        this.y = y;
    }

    /**
     * Fetches the distance between this point and the given point, squared.
     * This is faster as it does not use Math.sqrt(), which is slow.
     *
     * @param p the target position
     * @return the distance squared.
     */
    public int distanceSq(Position p) {
        int dx = p.x - this.x;
        int dy = p.y - this.y;

        return dx * dx + dy * dy;
    }

    /**
     * Returns true if the distance to the given position is less than or equal
     * to the given amount.
     *
     * @param p   the position
     * @param max the max distance
     * @return true for less than or equal distance, false otherwise.
     */
    public boolean near(Position p, int max) {
        return distanceSq(p) <= max * max;
    }

    /**
     * Adds the given x, y values to this position and returns a new position.
     * This does not modify this existing position.
     *
     * @param x the x value to add
     * @param y the y value to add
     * @return the new position.
     */
    public Position add(int x, int y) {
        return new Position(this.x + x, this.y + y);
    }

    /**
     * Returns true if the given Position is immediately next to this position.
     * If the two Positions are diagonal, then this method returns false. If
     * they are not next to each other at all, this method returns false. This
     * method returns true if one tile is North/East/South/West of the other by
     * 1 tile.
     *
     * @param to the checked tile
     * @return true if neighbours, false if not.
     */
    public boolean isNeighbour(Position to) {
        int dx = Math.abs(to.x - this.x);
        int dy = Math.abs(to.y - this.y);

        //East or west of
        return dx == 1 && dy == 0 || dx == 0 && dy == 1;
    }

    public boolean isDiagonal(Position to) {
        int dx = Math.abs(to.x - this.x);
        int dy = Math.abs(to.y - this.y);

        //The tile is a neighbour
        return !(dx == 0 || dy == 0) && !(dx > 1 || dx < -1 || dy > 1 || dy < -1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        Position p = (Position) o;
        return p.x == this.x && p.y == this.y;

    }

    @Override
    public int hashCode() {
        //If we had negative values for y, then
        //this may cause lots of hash collisions.
        //But, as a rule of thumb, we don't.
        return (x << 16) ^ y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public MutableConfig serialize() {
        MutableConfig map = new MutableConfig();
        map.set("x", this.x);
        map.set("y", this.y);

        return map;
    }

    @Override
    public int getDimension(int axis) {
        return 1;
    }

    @Override
    public int getDimensions() {
        return 2;
    }

    @Override
    public int getMin(int axis) {
        if (axis == 0) {
            return x;
        } else if (axis == 1) {
            return y;
        } else {
            throw new IllegalArgumentException("Bad axis requested, " + axis);
        }
    }
}