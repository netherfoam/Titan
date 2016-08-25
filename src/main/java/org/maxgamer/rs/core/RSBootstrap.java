package org.maxgamer.rs.core;

import org.maxgamer.rs.logon.logon.LogonServer;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;
import org.maxgamer.rs.util.log.Log;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author netherfoam
 */
public class RSBootstrap {
	public static void main(String[] args) throws IOException, ConnectionException, URISyntaxException {
		try {
			Core.init();
			
			//Flag that prevents the logon server being started automatically
			boolean gameOnly = false;
			for (String s : args) {
				if (s.equalsIgnoreCase("game-only")) {
					gameOnly = true;
					break;
				}
			}
			
			if(gameOnly == false){
				LogonServer.init(Core.getServer().getCommands(), Core.getServer().getEvents());
			}
			
			Core.getConsole(); //Initializes console
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.severe("Error starting core.");
			System.exit(1);
		}
	}
	
	private RSBootstrap() {
		//Private constructor
	}
}