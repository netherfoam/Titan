package org.maxgamer.rs.core.server;

/**
 * @author netherfoam
 */
public class IllegalThreadException extends RuntimeException {
	private static final long serialVersionUID = -7372301502938861928L;
	
	public IllegalThreadException(String msg) {
		super(msg);
	}
	
	public IllegalThreadException() {
		super();
	}
}