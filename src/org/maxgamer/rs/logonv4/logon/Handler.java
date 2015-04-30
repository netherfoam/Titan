package org.maxgamer.rs.logonv4.logon;

import org.maxgamer.rs.network.io.stream.RSInputBuffer;

/**
 * @author netherfoam
 */
public interface Handler {
	public abstract void handle(RSInputBuffer in);
}