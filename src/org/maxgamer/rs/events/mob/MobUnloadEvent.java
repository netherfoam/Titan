package org.maxgamer.rs.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class MobUnloadEvent extends MobEvent {
	
	public MobUnloadEvent(Mob mob) {
		super(mob);
	}
	
}