package org.maxgamer.rs.model.events;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.IllegalThreadException;
import org.maxgamer.rs.event.Event;

/**
 * @author netherfoam
 */
public class RSEvent extends Event {
	/** Calls this action through the ActionManager. Convenience method */
	public void call() {
		if (this.isAsync() == false && Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Events must be called from the Server thread");
		}
		Core.getServer().getEvents().callEvent(this);
	}
	
	public boolean isAsync() {
		return false;
	}
}