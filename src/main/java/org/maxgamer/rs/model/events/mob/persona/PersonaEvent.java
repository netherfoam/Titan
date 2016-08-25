package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.events.mob.MobEvent;

/**
 * @author netherfoam
 */
public class PersonaEvent extends MobEvent {
	public PersonaEvent(Persona p) {
		super(p);
	}
	
	@Override
	public Persona getMob() {
		return (Persona) super.getMob();
	}
}