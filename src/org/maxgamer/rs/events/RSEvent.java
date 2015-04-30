package org.maxgamer.rs.events;

import org.maxgamer.event.Event;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.IllegalThreadException;

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