package org.maxgamer.rs.events.mob.persona;

import org.maxgamer.event.Cancellable;
import org.maxgamer.rs.events.RSEvent;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;

/**
 * @author netherfoam
 */
public class PersonaInteractMobEvent extends RSEvent implements Cancellable {
	private Persona p;
	private Mob target;
	private int option;
	private boolean cancel;
	
	public PersonaInteractMobEvent(Persona p, Mob target, int option) {
		this.p = p;
		this.target = target;
		this.option = option;
	}
	
	public Persona getPersona() {
		return p;
	}
	
	public Mob getTarget() {
		return target;
	}
	
	public int getOption() {
		return option;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}