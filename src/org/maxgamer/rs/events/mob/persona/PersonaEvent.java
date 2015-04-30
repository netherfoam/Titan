package org.maxgamer.rs.events.mob.persona;

import org.maxgamer.rs.events.mob.MobEvent;
import org.maxgamer.rs.model.entity.mob.persona.Persona;

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