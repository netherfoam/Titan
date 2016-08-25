package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.model.events.RSEvent;
import org.maxgamer.rs.network.Client;

/**
 * @author Alva
 */
public class ClanMessageEvent extends RSEvent {
	
	private Client c;
	private String s;
	private int rights;
	
	public ClanMessageEvent(Client c, String s, int rights) {
		this.c = c;
		this.s = s;
		this.rights = rights;
	}
	
	public Client getClient() {
		return c;
	}
	
	public String getMessage() {
		return s;
	}
	
	public int getRights() {
		return rights;
	}
}
