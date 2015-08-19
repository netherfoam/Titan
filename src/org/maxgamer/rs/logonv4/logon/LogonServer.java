package org.maxgamer.rs.logonv4.logon;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import org.maxgamer.rs.command.CommandManager;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.lib.Files;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.logonv4.LSOutgoingPacket;
import org.maxgamer.rs.logonv4.ProfileManager;
import org.maxgamer.rs.structure.ServerHost;
import org.maxgamer.rs.structure.sql.Database;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;
import org.maxgamer.rs.structure.sql.MySQLC3P0Core;
import org.maxgamer.rs.structure.sql.SQLiteCore;
import org.maxgamer.structure.configs.ConfigSection;
import org.maxgamer.structure.configs.FileConfig;

/**
 * @author netherfoam
 */
public class LogonServer extends ServerHost<WorldHost> {
	public static void main(String[] args) throws IOException, ConnectionException {
		final CommandManager cm = new CommandManager(null);
		LogonServer.init(cm);
		
		final Thread reader = new Thread("Logon-Reader") {
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
	
	private static LogonServer LOGON;
	
	public static LogonServer getLogon() {
		return LOGON;
	}
	
	public static void init(CommandManager commands) throws IOException, ConnectionException {
		File cfgFile = new File("config", "logon.yml");
		if(cfgFile.exists() == false){
			File dist = new File("config" + File.separatorChar + "logon.yml.dist");
			if(dist.exists()){
				try{
					Files.copy(dist, cfgFile);
				}
				catch(IOException e){
					Log.warning("Could not copy " + dist + " to " + cfgFile);
				}
			}
			else{
				Log.warning(dist + " does not exist. Can't copy server config to " + cfgFile + "!");
			}
		}
		
		FileConfig config = new FileConfig(cfgFile);
		config.reload();
		
		LOGON = new LogonServer(config);
		LOGON.start();
		commands.load(new File("bin" + File.separator + "org" + File.separator + "maxgamer" + File.separator + "rs" + File.separator + "logonv4" + File.separator + "logon" + File.separator + "commands"));
	}
	
	/**
	 * The password to connect to this logon server.
	 */
	private String hostPass;
	
	/**
	 * The profiles which are to be used for this logon server
	 */
	private ProfileManager profiles;
	
	public LogonServer(ConfigSection config) throws IOException, ConnectionException {
		super(config.getInt("port", 2709));
		this.hostPass = config.getString("pass");
		
		Database world;
		try {
			//Database initialization
			ConfigSection c = config.getSection("database");
			String type = c.getString("type", "sqlite");
			
			//Logon Database
			if (type.equalsIgnoreCase("mysql")) {
				Log.debug("World using MySQL Database.");
				world = new Database(new MySQLC3P0Core(c.getString("host", "localhost"), c.getString("user", "root"), c.getString("pass", ""), c.getString("database", "titan"), c.getString("port", "3306")));
			}
			else {
				Log.debug("World using SQLite Database: " + c.getString("file", "sql" + File.separator + "profiles.db"));
				world = new Database(new SQLiteCore(new File(c.getString("file", "sql" + File.separator + "profiles.db"))));
			}
			Log.debug("Database connection established.");
		}
		catch (Exception e) {
			Log.severe("Failed to establish database connection, exitting.");
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		this.profiles = new ProfileManager(world);
	}
	
	public boolean isOnline(String player) {
		for (WorldHost host : this.getSessions()) {
			if (host.getPlayer(player) != null) return true;
		}
		return false;
	}
	
	@Override
	public void start() {
		super.start();
		Log.debug("Starting logon...");
		Thread pinger = new Thread("Logon.LogonServer.Pinger Thread") {
			@Override
			public void run() {
				while (true) { //Daemon thread, so this will stop eventually
					try {
						for (WorldHost world : getSessions()) {
							LSOutgoingPacket out = new LSOutgoingPacket(4); //Ping
							world.write(out);
						}
						
						for (WorldHost host : getSessions()) {
							if (host.hasTimedOut()) {
								Log.debug("Host " + host + " has timed out.");
								host.close(false);
							}
						}
						
						try {
							Thread.sleep(3000);
						}
						catch (InterruptedException e) {
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		pinger.setDaemon(true);
		pinger.start();
		Log.info("LogonServer Started!");
	}
	
	public int getFreeWorldId() {
		for (int i = 1; i <= 255; i++) {
			boolean taken = false;
			for (WorldHost host : getSessions()) {
				if (host.getId() > 0 && host.getId() == i) {
					taken = true;
					break;
				}
			}
			//This world ID is free.
			if (taken == false) {
				return i;
			}
		}
		return -1; // No free ID available
	}
	
	public boolean isHostPass(String pass) {
		return this.hostPass.equals(pass);
	}
	
	public ProfileManager getProfiles() {
		return profiles;
	}
	
	@Override
	public WorldHost connect(SocketChannel channel, SelectionKey key) {
		try {
			Log.info("Incoming connection from " + ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		WorldHost w = new WorldHost(channel, key, this);
		return w;
	}
}