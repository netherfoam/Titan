package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class MobLoadEvent extends MobEvent {

    public MobLoadEvent(Mob mob) {
        super(mob);
    }

}