package org.maxgamer.rs.logon.logon;

import java.io.IOException;
import java.util.Scanner;

import org.maxgamer.rs.command.CommandManager;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.event.EventManager;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;

public class LSBootstrap{
	public static void main(String[] args) throws IOException, ConnectionException {
		final CommandManager cm = new CommandManager(null);
		LogonServer.init(cm, new EventManager());
		
		final Thread reader = new Thread("Logon-Console") {
			@Override
			public void run() {
				Scanner sc = new Scanner(System.in);
				while (sc.hasNextLine()) {
					final String line = sc.nextLine();
					
					cm.handle(new CommandSender() {
						
						@Override
						public void sendMessage(String msg) {
							System.out.println(msg);
						}
						
						@Override
						public String getName() {
							return "Console";
						}
					}, CommandManager.COMMAND_PREFIX + line);
				}
				sc.close();
			}
		};
		reader.setDaemon(true);
		reader.start();
	}
}