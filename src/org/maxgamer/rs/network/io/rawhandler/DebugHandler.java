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
		
		StringBuilder sb = new StringBuilder();
		while (b.isEmpty() == false) {
			sb.append(String.format("%X ", b.readByte()));
			i++;
			if (i >= 20) {
				Log.debug(sb.toString());
				sb = new StringBuilder();
				i = 0;
			}
		}
		if(sb.length() > 0){
			Log.debug(sb.toString());
		}
	}
}