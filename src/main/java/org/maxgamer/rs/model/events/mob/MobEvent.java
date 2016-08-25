package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.RSEvent;

/**
 * @author netherfoam
 */
public class MobEvent extends RSEvent {
	private Mob mob;
	
	public MobEvent(Mob mob) {
		if (mob == null) throw new NullPointerException("Mob may not be null");
		this.mob = mob;
	}
	
	public Mob getMob() {
		return mob;
	}
}