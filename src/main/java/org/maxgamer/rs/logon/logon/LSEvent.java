package org.maxgamer.rs.logon.logon;

import org.maxgamer.rs.event.Event;

/**
 * @author netherfoam
 */
public class LSEvent extends Event {
	/** Calls this action through the ActionManager. Convenience method */
	public void call() {
		LogonServer.getLogon().getEvents().callEvent(this);
	}
}