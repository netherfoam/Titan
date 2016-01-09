package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;

public class MobPreTeleportEvent extends MobEvent {
	private final Location from;
	private final Location to;

	public MobPreTeleportEvent(Mob mob, Location fromLocation, Location toLocation) {
		super(mob);
		this.from = fromLocation;
		this.to = toLocation;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

}
