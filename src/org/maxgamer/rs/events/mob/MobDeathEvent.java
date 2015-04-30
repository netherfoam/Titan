package org.maxgamer.rs.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class MobDeathEvent extends MobEvent {
	public MobDeathEvent(Mob m) {
		super(m);
	}
}