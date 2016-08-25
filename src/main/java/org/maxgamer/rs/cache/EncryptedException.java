package org.maxgamer.rs.cache;

import java.io.IOException;

/**
 * @author netherfoam
 */
public class EncryptedException extends IOException {
	public EncryptedException(String message, IOException e) {
		super(message, e);
	}
	
	private static final long serialVersionUID = 2034720445866447527L;
	
}