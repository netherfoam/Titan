package org.maxgamer.rs.model.events.mob.persona;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.persona.Persona;

/**
 * @author netherfoam
 */
public class PersonaChatEvent extends PersonaEvent implements Cancellable {
	private boolean cancel;
	private String message;
	
	public PersonaChatEvent(Persona p, String message) {
		super(p);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String s) {
		this.message = s;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}