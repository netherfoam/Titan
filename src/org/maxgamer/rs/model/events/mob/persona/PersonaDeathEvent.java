package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.model.entity.mob.persona.Persona;

/**
 * Called during the start() call of a Persona for the first time
 * @author netherfoam
 *
 */
public class PersonaDeathEvent extends PersonaEvent {
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
}