package org.maxgamer.rs.core;

import java.io.IOException;
import java.net.URISyntaxException;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.logonv4.logon.LogonServer;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;

/**
 * @author netherfoam
 */
public class RSBootstrap {
	public static void main(String[] args) throws IOException, ConnectionException, URISyntaxException {
		try {
			Core.init(Runtime.getRuntime().availableProcessors() - 1, args);
			
			for (String s : args) {
				if (s.equalsIgnoreCase("standalone")) {
					LogonServer.init(Core.getServer().getCommands());
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.severe("Error starting core.");
		}
	}
	
	private RSBootstrap() {
		//Private constructor
	}
}