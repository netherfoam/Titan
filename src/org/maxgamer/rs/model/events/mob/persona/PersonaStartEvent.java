package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.model.entity.mob.persona.Persona;

/**
 * Called during the start() call of a Persona for the first time
 * @author netherfoam
 *
 */
public class PersonaStartEvent extends PersonaEvent {
	public PersonaStartEvent(Persona p) {
		super(p);
	}
}