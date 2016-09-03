package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.map.path.Direction;

/**
 * @author netherfoam
 */
public class NPCMovementUpdate extends MovementUpdate {
    /**
     * Values that the client interprets as directions. The center [2,2] is
     * where the player is initially. The middle box [1,1] to [3,3] is for
     * walking The outer box (remainder) is for running.
     * <p>
     * Each value moves the player in that particular direction.
     */
    private static final byte[][] RUN_DIRECTIONS = {
            //This represents the NPC's walk directions.
            //An NPC can only walk, not run. (TODO: Says dementhium,
            //NPC's may be able to run in the client code?)

            //This matrix can be described as:
            // -> North (Eg towards the Wilderness)
            // <- South (Eg towards Tutorial Island/Lumbridge)
            // ^ West (Eg towards Ardougne)
            // v East (Eg towards Al'kharid
            {5, 6, 7}, {4, -1, 0}, {3, 2, 1},};

    public NPCMovementUpdate() {
        super(RUN_DIRECTIONS);
        this.reset();
    }

    public void setRun(Direction dir1, Direction dir2) {
        super.setRun(dir1, dir2); //Make method public
    }
}
