package org.maxgamer.rs.model.events.server;

import org.maxgamer.rs.core.server.Server;
import org.maxgamer.rs.model.events.RSEvent;

/**
 * @author netherfoam
 */
public class ServerShutdownEvent extends RSEvent {
	private Server s;
	
	public ServerShutdownEvent(Server s) {
		this.s = s;
	}
	
	public Server getServer() {
		return s;
	}

	@Override
	public boolean isAsync() {
		return true;
	}
}