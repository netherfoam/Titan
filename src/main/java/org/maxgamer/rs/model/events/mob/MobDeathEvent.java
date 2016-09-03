package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;

/**
 * Class represents when a Mob dies. This is after the death animation they
 * perform.
 *
 * @author netherfoam
 * @date 12 Dec 2015
 */
public class MobDeathEvent extends MobEvent {
    /**
     * The location this mob should be respawned at after death, if they
     * respawn. Null if the mob shouldn't be moved after death.
     */
    private Location spawn;

    /**
     * Constructs a new MobDeathEvent
     *
     * @param m the mob that is dying
     */
    public MobDeathEvent(Mob m) {
        super(m);
    }

    /**
     * The location this mob should be respawned at after death, if they
     * respawn. This defaults to {@link Mob#getSpawn()}
     *
     * @return The location this mob should be respawned at after death, if they
     * respawn.
     */
    public Location getSpawn() {
        if (this.spawn == null) {
            return getMob().getSpawn();
        } else {
            return this.spawn;
        }
    }

    /**
     * Sets the location this mob should be respawned at after death. Setting
     * this to null will default to {@link Mob#getSpawn()}
     *
     * @param spawn the new location to respawn at
     */
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}