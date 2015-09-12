package org.maxgamer.rs.core.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.maxgamer.event.EventManager;
import org.maxgamer.rs.command.CommandManager;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.events.server.ServerShutdownEvent;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.logonv4.game.LogonConnection;
import org.maxgamer.rs.model.entity.EntityList;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.npc.SpawnManager;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.PersonaList;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Viewport;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ground.GroundItemManager;
import org.maxgamer.rs.model.lobby.Lobby;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.StandardMap;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.module.ModuleLoader;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.server.RS2Server;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;
import org.maxgamer.rs.structure.timings.StopWatch;
import org.maxgamer.structure.configs.ConfigSection;
import org.maxgamer.structure.configs.FileConfig;

/**
 * @author netherfoam
 */
public class Server {
	/**
	 * The world map, this is not a dynamically generated one and is parsed
	 * directly from the cache.
	 */
	private StandardMap map;
	
	/**
	 * The network object this server runs off
	 */
	private RS2Server network;
	
	/**
	 * The ticker, this is responsible for calling tick() on various subscribed
	 * Tickable objects
	 */
	private ServerTicker ticker;
	
	/**
	 * The player's login lobby
	 */
	private Lobby lobby;
	
	/**
	 * A task which saves the server periodically.
	 */
	private AutoSave autosave;
	
	/**
	 * The module manager which also holds references to all of the currently
	 * loaded modules
	 */
	private ModuleLoader modules;
	
	/**
	 * The command manager for the server, which holds all commands and allows
	 * for registering and unregistering of commands.
	 */
	private CommandManager commands;
	
	/**
	 * The Event manager for the server, which holds all active event listener
	 * registrations, and allows for events to be called.
	 */
	private EventManager events;
	
	/**
	 * 2047 is the max number of players. 2046 is the max number of *other*
	 * players. The client represents playerId 0 as a null player.
	 */
	private PersonaList personas = new PersonaList(2047);//2047 is the max players
	
	/**
	 * 32767 is (Possibly?) the max number of NPCs. NPCs are stored separately
	 * to players.
	 */
	private EntityList<NPC> npcs = new EntityList<NPC>(32767);
	
	/**
	 * The primary server thread wrapper
	 */
	private ServerThread thread;
	
	/**
	 * The GroundItemManager, this manages all items that are lying on the
	 * ground. It is not for gameobjects, just items.
	 */
	private GroundItemManager groundItems;
	
	/**
	 * The PHP-5 script engine for handling game actions like interacting with
	 * an item, NPC or gameobject.
	 */
	//private BeanScriptEngine scripts;
	
	//private ServerLogonWatcher logon;
	private LogonConnection logon;
	
	private ConfigSection config;
	
	/**
	 * Creates a new server.
	 * @param port The port to run the server on. This must be >= 0.
	 * @param definition The WorldDefinition to use. This may not be null.
	 * @throws IOException If the port could not be bound.
	 * @throws ConnectionException
	 */
	public Server(ConfigSection cfg) throws IOException, ConnectionException {
		if (cfg == null) cfg = new ConfigSection(); //We will use default values.
		this.config = cfg;
		
		//We construct this immediately, it may be required immediately.
		this.thread = new ServerThread(this);
		
		//Immediately opens the port, but does not necessarily begin accepting/reading/writing
		this.network = new RS2Server(config.getInt("port"), this);
		
		FileConfig logon = new FileConfig(new File("config", "logon.yml"));
		logon.reload();
		//this.logon = new ServerLogonWatcher(logon);
		this.logon = new LogonConnection(logon);
	}
	
	public String getRegion() {
		return config.getString("region", "");
	}
	
	public String getActivity() {
		return config.getString("activity", "");
	}
	
	public String getIP() {
		return config.getString("ip", "127.0.0.1");
	}
	
	public int getFlags() {
		return config.getInt("flags");
	}
	
	public int getWorldId() {
		int id = logon.getWorldId();
		if (id == -1) throw new IllegalStateException("Server not yet initialized.");
		return id;
	}
	
	/**
	 * Loads this server and starts it so that players can begin connecting.
	 * @throws IOException if there is an issue with the config or map
	 * @throws SQLException
	 */
	public void load() throws IOException, SQLException {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				//This should be done in the main thread, because if the world is modified while we're sending masks,
				//we will be sending the modified masks!
				StopWatch update = Core.getTimings().start("sync-mask-update");
				try {
					//Update our players, our Personas don't need to be updated though.
					for (Persona p : Server.this.getPersonas()) {
						if (p instanceof Player) {
							Player pl = (Player) p;
							if (pl.isLoaded() == false) continue;
							//The way NIO works is the data is queued to be written,
							//instead of actually being written to the client.
							//Thus this is actually much faster because this thread isn't
							//performing any IO.
							pl.getProtocol().sendUpdates();
						}
					}
					
					//Reset our masks - Personas
					for (Persona p : Server.this.getPersonas()) {
						//Generally the server is not doing anything here,
						//so we can do this in another thread.
						p.getUpdateMask().reset();
						p.getModel().setChanged(false);
					}
					
					//Reset our masks - NPCs
					for (NPC n : Server.this.getNPCs()) {
						n.getUpdateMask().reset();
						n.getModel().setChanged(false);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					Log.warning("Error processing mask updates.");
				}
				finally {
					update.stop();
				}
				Core.submit(this, Core.getWorldConfig().getInt("update-interval", 30), false);
			}
		};
		Core.submit(r, Core.getWorldConfig().getInt("update-interval", 30), false);
		
		this.thread.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Server.this.lobby = new Lobby();
					
					//We should initialize everything before loading user content
					//Eg commands, events, modules
					Log.info("Loading StandardMap...");
					Server.this.map = new StandardMap("World", 16384, 16384); //this costs around 50mb of RAM.
					Log.info("...StandardMap Loaded!");
					Server.this.modules = new ModuleLoader();
					Server.this.ticker = new ServerTicker(Server.this);
					Server.this.groundItems = new GroundItemManager();
					//Server.this.scripts = new BeanScriptEngine();
					
					getEvents();
					
					Log.debug("Loading Commands...");
					getCommands();
					Log.debug("Loading Modules...");
					Server.this.modules.load();
					Log.debug("Modules Loaded!");
					
					Server.this.logon.start();
					if(Core.getWorldConfig().getBoolean("load.npcs", true)){
						events.register(new SpawnManager());
					}
					//SpawnManager.loadAll();
					
					//Autosave
					int interval = Core.getWorldConfig().getInt("autosave-interval", 10000);
					if (interval > 0) {
						Server.this.autosave = new AutoSave(interval);
						Core.submit(Server.this.autosave, interval, true);
					}
					
					//Log.debug("Loading BeanScriptEngine...");
					//Server.this.scripts.reload();
					//Log.debug("Loaded BeanScriptEngine!");
					
					Server.this.thread.submit(ticker);
					//Note that Server.this doesn't start the network, that is done by the
					//logon server when a connection is established. The logon server
					//will also drop the connection if the logon connection is lost
					Log.info("Server initialized!");
					getThread().resetUsage();
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
		this.thread.start();
	}
	
	/**
	 * Fetches the client with the given UUID. This searches the currently
	 * online players, and then the lobby players. If none is found with the
	 * given UUID, then this returns null.
	 * @param uuid the UUID for the session
	 * @return the client or null if not found
	 */
	public Client getClient(int uuid) {
		for (Persona p : personas) {
			if (p instanceof Client) {
				Client c = (Client) p;
				
				if (c.getUUID() == uuid) {
					return c;
				}
			}
		}
		
		for (LobbyPlayer player : lobby.getPlayers()) {
			if (player.getUUID() == uuid) return player;
		}
		
		return null; //Not found
	}
	
	public LogonConnection getLogon() {
		return logon;
	}
	
	/**
	 * Fetches the given client by name. This searches currently online players
	 * first, and then the lobby players. By nature, this method cannot return
	 * any AI players (As they do not implement Client)
	 * @param name the name of the client to search for.
	 * @return the client or null if not found.
	 */
	public Client getClient(String name, boolean autocomplete) {
		Persona p = personas.getPersona(name, autocomplete);
		if (p != null && p instanceof Client) {
			return (Client) p;
		}
		
		LobbyPlayer pl = lobby.getPlayer(name, autocomplete);
		if (pl != null) {
			return pl;
		}
		
		return null; //Not found
	}
	
	/**
	 * Fetches the command manager which allows registration of and dispatching
	 * of commands.
	 * @return the command manager instance
	 */
	public CommandManager getCommands() {
		if(commands == null){
			Log.info("Loading commands...");
			commands = new CommandManager(events);
			commands.load(new File("bin" + File.separator + "org" + File.separator + "maxgamer" + File.separator + "rs" + File.separator + "command" + File.separator + "commands"));
			Log.info("...Loaded commands!");
		}
		return commands;
	}
	
	/**
	 * Fetches the GroundItemManager for the server. You generally shouldn't
	 * need to use this
	 * @return the ground item manager
	 */
	public GroundItemManager getGroundItems() {
		return groundItems;
	}
	
	/**
	 * Returns the EventManager for the server which allows registration of
	 * event listeners and calling of events.
	 * @return the event manager.
	 */
	public EventManager getEvents() {
		if(events == null){
			Log.debug("Loading EventManager...");
			events = new EventManager();
			events.reload(Core.getCodeSource());
			Log.debug("...Loaded EventManager!");
		}
		return events;
	}
	
	/**
	 * Fetches the primary world map where most activities take place. You can
	 * construct your own dynamic maps, though you must manage them on your own.
	 * This is not a dynamic map, and costs at least 50MB of RAM to hold. A
	 * dynamic map is much smaller.
	 * @return the world map
	 */
	public WorldMap getMap() {
		if (map == null) {
			throw new RuntimeException("Server Map hasn't been initialized!");
		}
		return map;
	}
	
	/**
	 * Unloads all modules, calls a new ServerShutdownEvent, shuts down the
	 * network and save's all player data.
	 */
	public void shutdown() {
		if (logon.isRunning()) logon.stop(); //Saves all players
		modules.unload();
		ServerShutdownEvent e = new ServerShutdownEvent(this);
		e.call();
		
		if (network.isRunning()) network.stop();
		thread.shutdown();
		
		save(); //Also saves all players, but fails since logon is closed
	}
	
	/**
	 * Fetches the player's login lobby
	 * @return the player's login lobby
	 */
	public Lobby getLobby() {
		return lobby;
	}
	
	public Collection<Client> getClients() {
		ArrayList<Client> clients = new ArrayList<Client>(lobby.size() + personas.getCount());
		for (Persona p : personas) {
			if (p instanceof Client) clients.add((Client) p);
		}
		clients.addAll(lobby.getPlayers());
		return clients;
	}
	
	/**
	 * Fetches a list of all player indexes on the server.
	 * @return a list of all player indexes on the server.
	 */
	public PersonaList getPersonas() {
		return personas;
	}
	
	/**
	 * Fetches a list of all NPC indexes on the server.
	 * @return a list of all NPC indexes on the server.
	 */
	public EntityList<NPC> getNPCs() {
		return npcs;
	}
	
	/**
	 * Fetches the given player by name, case insensitive. This method does not
	 * autocomplete. It only works for online players.
	 * @param name the name of the persona
	 * @return the Persona or null if they are not found.
	 * @throws NullPointerException if the given name is null
	 */
	public Persona getPersona(String name) {
		return personas.getPersona(name);
	}
	
	/**
	 * Fetches the network handler for the server
	 * @return the network handler for the server
	 */
	public RS2Server getNetwork() {
		return network;
	}
	
	/**
	 * Returns the Server Tick handler, which is used for subscribing and
	 * unsubscribing Tickable objects to tick() notifications.
	 * @return the Server's Tick Handler.
	 */
	public ServerTicker getTicker() {
		return ticker;
	}
	
	/**
	 * Fetches the ConfigSection for the server. This is under a section labeled
	 * "server" in the config file.
	 * @return the ConfigSection for this server.
	 */
	public ConfigSection getConfig() {
		return Core.getWorldConfig().getSection("server");
	}
	
	/**
	 * Saves all currently online players to disk.
	 */
	public void save() {
		Core.submit(new Runnable() {
			@Override
			public void run() {
				if (Core.getServer().getClients().isEmpty() == false) {
					Core.getServer().getLogon().getAPI().save(Core.getServer().getClients());
				}
			}
		}, true);
	}
	
	/**
	 * Highlights the given locations to all nearby players for the given
	 * duration
	 * @param locations
	 */
	public void highlight(int duration, final Location... locations) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration must be >= 0");
		}
		
		for (Location l : locations) {
			for (Viewport view : l.getNearby(Viewport.class, 0)) {
				Player p = view.getOwner();
				p.getProtocol().sendGroundItem(l, ItemStack.create(995, 5000));
			}
		}
		
		//this.getTicker().submit(duration, new Tickable(){
		new Tickable() {
			@Override
			public void tick() {
				for (Location l : locations) {
					for (Viewport view : l.getNearby(Viewport.class, 0)) {
						Player p = view.getOwner();
						p.getProtocol().removeGroundItem(l, ItemStack.create(995, 5000));
					}
				}
			}
		}.queue(duration);
	}
	
	/**
	 * The plugin system that loads/unloads modules at runtime.
	 * @return the module system manager
	 */
	public ModuleLoader getModules() {
		return modules;
	}
	
	public ServerThread getThread() {
		return thread;
	}
}