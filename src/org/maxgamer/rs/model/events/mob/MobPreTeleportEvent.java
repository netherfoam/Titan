package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;

public class MobPreTeleportEvent extends MobEvent {

	private final Location fromLocation, toLocation;

	public MobPreTeleportEvent(Mob mob, Location fromLocation, Location toLocation) {
		super(mob);
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
	}

	public Location getTeleportFromLocation() {
		return fromLocation;
	}

	public Location getTeleportToLocation() {
		return toLocation;
	}

}
