package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.util.Calc;

/**
 * @author netherfoam
 */
public abstract class MovementUpdate implements Mask {
    /**
     * Values that the client interprets as directions. The center [2,2] is
     * where the player is initially. The middle box [1,1] to [3,3] is for
     * walking The outer box (remainder) is for running.
     * <p>
     * Each value moves the player in that particular direction.
     */
    private byte[][] RUN_DIRECTIONS = null;

    /**
     * The next directions to move in
     */
    private Direction[] directions;

    /**
     * True if this mob has teleported.
     */
    private boolean teleported;

    /**
     * Constructs a new MovementUpdate. This object is reusable for a Mob. The
     * {@code runDirections} parameter must be a 2D array with the same length
     * (eg, a cube) where the sides are all equal and odd. The index [0][0]
     * represents SOUTH_WEST, hwile the index[len -1][len -1] represents
     * NORTH_EAST. The values are the values that the client interprets as walk
     * directions for the owner of this update. For example, the value 7 for a
     * NPC update means WALK, ONE tile NORTH_WEST
     *
     * @param runDirections the values the client interprets as directions. See
     *                      above
     * @throws IllegalArgumentException if runDirections does not conform to the
     *                                  specifications above.
     */
    public MovementUpdate(byte[][] runDirections) {
        if (runDirections.length % 2 != 1) {
            throw new IllegalArgumentException("RunDirections must conform to... Make n = odd, then runDirections = byte[n][n]");
        }
        for (int i = 0; i < runDirections.length; i++) {
            if (runDirections[i].length != runDirections.length) {
                throw new IllegalArgumentException("RunDirections must conform to... Let n = odd, then runDirections = byte[n][n]");
            }
        }

        this.RUN_DIRECTIONS = runDirections;
        this.reset();
    }

    /**
     * Set this to true if the mob has teleported. You should use Mob.teleport()
     * instead, which modifies this itself.
     *
     * @param tele the return value of hasTeleported() after this call.
     */
    public void setTeleported(boolean tele) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        this.teleported = tele;
    }

    /**
     * Returns true if this mob is teleporting in this tick.
     *
     * @return true if this mob is teleporting in this tick.
     */
    public boolean hasTeleported() {
        return teleported;
    }

    /**
     * Sets the walk direction for this mob. All mobs can walk.
     *
     * @param dir the direction to walk in
     */
    public void setWalk(Direction dir) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        if (dir == null) throw new NullPointerException("Walk direction may not be null.");
        this.directions = new Direction[]{dir};
    }

    /**
     * Sets the direction for this mob to move in. Not all mobs can walk.
     *
     * @param dir1 the direction to walk in
     * @param dir2 the second direction to walk in
     * @throws NullPointerException if both directions are null. Use reset() in
     *                              that case.
     */
    protected void setRun(Direction dir1, Direction dir2) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        if (dir1 == null || dir2 == null) throw new NullPointerException("Walk direction may not be null.");
        this.directions = new Direction[]{dir1, dir2};
    }

    /**
     * Returns true if this is attempting to run (>=2 tiles moved)
     *
     * @return true if running
     */
    public boolean isRun() {
        return directions != null && directions.length >= 2;
    }

    /**
     * Returns the numeric code that the client can interpret as a movement
     * direction, including the number of tiles to move. This will return -1 if
     * no movement has been made, in which case you should not forward the value
     * to the client.
     *
     * @return the numeric code for the client
     */
    public int getDirection() {
        if (directions == null) {
            return -1;
        }

        int dx = directions[0].dx;
        int dy = directions[0].dy;

        if (directions.length >= 2) {
            dx += directions[1].dx;
            dy += directions[1].dy;

            if (Calc.isBetween(dx, -1, 1) && Calc.isBetween(dy, -1, 1)) {
                //They gave us two simple directions which are not the same.
                //Eg, they gave us NORTH + EAST.
                //This does not trigger if they gave us NORTH + NORTH,
                //or gave us NORTH_EAST + EAST
                throw new RuntimeException("Running a walk movement.");
            }
        }

        //Centers the array
        dx += RUN_DIRECTIONS.length / 2;
        dy += RUN_DIRECTIONS.length / 2;

        if (RUN_DIRECTIONS[dx][dy] < 0) {
            throw new RuntimeException("The directions " + directions[0] + ", " + directions[1] + " add up to zero. Eg, there is no movement update!");
        }

        return RUN_DIRECTIONS[dx][dy];
    }

    /**
     * Scraps all changes made to this movement update so that it is blank and
     * ready for new data to be placed. Called after each tick.
     */
    public void reset() {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        this.directions = null;
        this.teleported = false;
    }

    /**
     * Returns true if this mask has changed on this tick. If it has, then the
     * player needs to be updated.
     *
     * @return true if it has changed, false if no update is necessary
     */
    public boolean hasChanged() {
        if (this.directions != null) return true;
        if (this.teleported) return true;

        return false;
    }
}
