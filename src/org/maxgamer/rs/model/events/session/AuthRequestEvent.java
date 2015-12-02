package org.maxgamer.rs.model.events.session;

import org.maxgamer.rs.model.events.RSEvent;
import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.Client;

/**
 * @author netherfoam
 */
public class AuthRequestEvent extends RSEvent {
	private Client c;
	private AuthResult result;
	
	public AuthRequestEvent(Client c, AuthResult result) {
		this.c = c;
		this.result = result;
	}
	
	/**
	 * May be null if auth was failed
	 * @return
	 */
	public Client getClient() {
		return c;
	}
	
	public AuthResult getResult() {
		return result;
	}
	
	public void setResult(AuthResult result) {
		this.result = result;
	}
}