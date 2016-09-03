package org.maxgamer.rs.model.entity.mob.persona;

import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.map.path.Direction;

/**
 * @author netherfoam
 */
public class PersonaMovementUpdate extends MovementUpdate {
    /**
     * Values that the client interprets as directions. The center [2,2] is
     * where the player is initially. The middle box [1,1] to [3,3] is for
     * walking The outer box (remainder) is for running.
     * <p>
     * Each value moves the player in that particular direction.
     */
    private static final byte[][] RUN_DIRECTIONS = {
            //13 is north
            //8 is east
            //7 is west
            {0, 5, 7, 9, 11}, {1, 0, 3, 5, 12}, {2, 1, -1, 6, 13}, {3, 2, 4, 7, 14}, {4, 6, 8, 10, 15},};

    public PersonaMovementUpdate() {
        super(RUN_DIRECTIONS);
        this.reset();
    }

    public void setRun(Direction dir1, Direction dir2) {
        super.setRun(dir1, dir2); //Make method public
    }
}
