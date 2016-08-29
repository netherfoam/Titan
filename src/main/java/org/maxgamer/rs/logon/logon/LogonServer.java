package org.maxgamer.rs.logon.logon;

import org.maxgamer.rs.command.CommandManager;
import org.maxgamer.rs.command.commands.Stop;
import org.maxgamer.rs.event.EventManager;
import org.maxgamer.rs.logon.LSOutgoingPacket;
import org.maxgamer.rs.logon.ProfileRepository;
import org.maxgamer.rs.module.ModuleLoader;
import org.maxgamer.rs.structure.ServerHost;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.structure.sql.Database;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;
import org.maxgamer.rs.structure.sql.MySQLC3P0Core;
import org.maxgamer.rs.tools.ConfigSetup;
import org.maxgamer.rs.util.Files;
import org.maxgamer.rs.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author netherfoam
 */
public class LogonServer extends ServerHost<WorldHost> {
	private static LogonServer LOGON;
	
	public static LogonServer getLogon() {
		return LOGON;
	}
	
	public static void init(CommandManager commands, EventManager events) throws IOException, ConnectionException {
		File cfgFile = new File("config", "logon.yml");
		
		boolean isNew = false;
		
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
			isNew = true;
		}
		
		FileConfig config = new FileConfig(cfgFile);
		config.reload();
		
		if(isNew){
			ConfigSetup.logon(config);
			try {
				config.save();
			}
			catch (IOException e) {
				e.printStackTrace();
				Log.warning("Failed to save logon.yml file!");
			}
		}
		
		LOGON = new LogonServer(config);
		LOGON.events = events;
		LOGON.commands = commands;
		
		LOGON.start();
		commands.register("stop", new Stop());
	}
	
	/**
	 * The password to connect to this logon server.
	 */
	private String hostPass;
	
	private EventManager events;
	
	private CommandManager commands;
	
	private ModuleLoader modules;
	
	private Database database;
	
	public LogonServer(ConfigSection config) throws IOException, ConnectionException {
		super(config.getInt("port", 2709));
		this.hostPass = config.getString("pass");
		
		// TODO:
		this.modules = new ModuleLoader(new File("modules"), "logon");
		
		try {
			//Database initialization
			ConfigSection c = config.getSection("database");
			String type = c.getString("type", "sqlite");
			
			//Logon Database
			if (type.equalsIgnoreCase("mysql")) {
				Log.debug("Logon using MySQL Database.");
				database = new Database(new MySQLC3P0Core(c.getString("host", "localhost"), c.getString("user", "root"), c.getString("pass", ""), c.getString("database", "titan"), c.getString("port", "3306")));
			}
			else {
				throw new IllegalArgumentException("Bad configuration, database type " + type + " not supported");
			}
			Log.debug("Database connection established.");
		}
		catch (Exception e) {
			Log.severe("Failed to establish database connection, exitting.");
			e.printStackTrace();
			System.exit(1);
			return;
		}

		this.database.addRepository(new ProfileRepository());
	}
	
	public EventManager getEvents(){
		return events;
	}
	
	public CommandManager getCommands(){
		return commands;
	}
	
	public Database getDatabase(){
		return this.database;
	}
	
	public boolean isOnline(String player) {
		for (WorldHost host : this.getSessions()) {
			if (host.getPlayer(player) != null) return true;
		}
		return false;
	}
	
	@Override
	public void start() {
		this.modules.load();
		
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