package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.map.Location;

/**
 * Called during the start() call of a Persona for the first time
 * 
 * @author netherfoam
 *
 */
public class PersonaDeathEvent extends PersonaEvent {
	
	private Location spawn;
	private int kept;
	private boolean safe = false;

	public PersonaDeathEvent(Persona p) {
		super(p);
		setKeepSize(3);
	}

	public boolean isSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public void setKeepSize(int num) {
		this.kept = num;
	}

	public int getKeepSize() {
		return this.kept;
	}
	public Location getSpawn() {
		if (this.spawn == null) {
			return getMob().getSpawn();
		}
		else {
			return this.spawn;
		}
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

}