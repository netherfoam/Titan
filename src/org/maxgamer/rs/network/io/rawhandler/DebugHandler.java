package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * @author netherfoam
 */
public class DebugHandler extends RawHandler {
	public DebugHandler(Session s) {
		super(s);
	}
	
	@Override
	public void handle(RSByteBuffer b) {
		Log.debug("Reading data...");
		int i = 0;
		while (b.isEmpty() == false) {
			Log.debug(String.format("%X ", b.readByte()));
			i++;
			if (i >= 20) {
				Log.debug("\n");
				i = 0;
			}
		}
	}
}