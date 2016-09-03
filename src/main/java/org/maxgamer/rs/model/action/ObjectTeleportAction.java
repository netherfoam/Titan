package org.maxgamer.rs.model.action;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class ObjectTeleportAction extends ObjectInteractAction {
    private Location dest;

    public ObjectTeleportAction(Mob mob, Animation anim, Location dest) {
        super(mob, anim);
        if (dest == null || dest.getMap() == null) {
            throw new IllegalArgumentException("Destination invalid. May not be null, and map may not be null");
        }
        this.dest = dest;
    }

    @Override
    protected void run() throws SuspendExecution {
        getOwner().getUpdateMask().setAnimation(anim, 5);
        wait(anim.getDelay());
        getOwner().teleport(this.dest);
    }
}