package org.maxgamer.rs.events.mob;

import org.maxgamer.rs.events.RSEvent;
import org.maxgamer.rs.model.entity.mob.Mob;

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