package org.maxgamer.rs.core.server;

import org.hibernate.Session;
import org.maxgamer.rs.command.Command;
import org.maxgamer.rs.command.CommandManager;
import org.maxgamer.rs.command.commands.*;
import org.maxgamer.rs.command.commands.debug.*;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.Scheduler;
import org.maxgamer.rs.event.EventManager;
import org.maxgamer.rs.logon.game.LogonConnection;
import org.maxgamer.rs.model.entity.EntityList;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.npc.NPCGroupLoot;
import org.maxgamer.rs.model.entity.mob.npc.NPCGroupLootGuarantee;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.PersonaList;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.server.ServerShutdownEvent;
import org.maxgamer.rs.model.interact.InteractionManager;
import org.maxgamer.rs.model.item.AmmoType;
import org.maxgamer.rs.model.item.ItemAmmoType;
import org.maxgamer.rs.model.item.ground.GroundItemManager;
import org.maxgamer.rs.model.item.vendor.VendorManager;
import org.maxgamer.rs.model.javascript.DialogueUtil;
import org.maxgamer.rs.model.javascript.JavaScriptCallFiber;
import org.maxgamer.rs.model.javascript.ScriptEnvironment;
import org.maxgamer.rs.model.javascript.WorldControls;
import org.maxgamer.rs.model.lobby.Lobby;
import org.maxgamer.rs.model.map.MapManager;
import org.maxgamer.rs.model.map.StandardMap;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.model.skill.prayer.PrayerListener;
import org.maxgamer.rs.module.ModuleLoader;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.server.RS2Server;
import org.maxgamer.rs.repository.*;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.structure.sql.Database;
import org.maxgamer.rs.structure.sql.Database.ConnectionException;
import org.maxgamer.rs.structure.sql.MySQLC3P0Core;
import org.maxgamer.rs.structure.timings.StopWatch;
import org.maxgamer.rs.tools.ConfigSetup;
import org.maxgamer.rs.util.Files;
import org.maxgamer.rs.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * @author netherfoam
 */
public class Server {
    /**
     * The server logger
     */
    private Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * The {@link MapManager} for instances of WorldMaps.  This holds the primary world "mainland", as well
     * as any other persistant worlds. It does not hold maps which are temporary, such as Pest Control arenas.
     */
    private MapManager maps;

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
    private PersonaList personas = new PersonaList(2047); //2047 is the max players

    /**
     * 32767 is (Possibly?) the max number of NPCs. NPCs are stored separately
     * to players.
     */
    private EntityList<NPC> npcs = new EntityList<>(32767);

    /**
     * The primary server thread wrapper
     */
    private ServerExecutor thread;

    /**
     * The GroundItemManager, this manages all items that are lying on the
     * ground. It is not for gameobjects, just items.
     */
    private GroundItemManager groundItems;

    private LogonConnection logon;

    private ConfigSection config;

    /**
     * The interactions manager for the server
     */
    private InteractionManager interactions;

    /**
     * The epoch time in milliseconds that the server was constructed.
     */
    private long started;

    /**
     * The server-wide vendor manager. This holds references to all vendors and their current quantities in the game world
     */
    private VendorManager vendors;

    /**
     * The database used for world information, such as NPC spawns, ammo types
     * and item definitions
     */
    private Database database;

    /**
     * Handles scheduling of tasks for a later date, optionally on the main
     * thread or on an async thread
     */
    private Scheduler scheduler;

    /**
     * The root JS Scope
     */
    private ScriptEnvironment scriptEnvironment;

    public Server() throws IOException {
        this(null);
    }

    /**
     * Creates a new server.
     *
     * @param cfg The configuration used for this server
     * @throws IOException If the port could not be bound.
     */
    public Server(ConfigSection cfg) throws IOException {
        this.config = cfg;

        //We construct this immediately, it may be required immediately.
        this.thread = new ServerExecutor(this);

        //Immediately opens the port, but does not necessarily begin accepting/reading/writing
        this.network = new RS2Server(getConfig().getInt("world.port"), this);

        //TODO: ConfigSetup.logon() if file not found
        //TODO: Copy the .dist file across automatically
        FileConfig logon = new FileConfig(new File("config", "logon.yml"));
        logon.reload();

        this.logon = new LogonConnection(logon);
        this.started = System.currentTimeMillis();
        this.scheduler = new Scheduler(this.getThread(), Core.getThreadPool());
        this.scriptEnvironment = new ScriptEnvironment(new File("javascripts"));
        this.scriptEnvironment.register(DialogueUtil.class);
        this.scriptEnvironment.register(WorldControls.class);
    }

    public synchronized AutoSave getAutosave() {
        if (autosave == null) {
            int interval = this.getConfig().getInt("autosave-interval", 10000);
            if (interval <= 0) throw new IllegalArgumentException("autosave-interval must be positive");
            this.autosave = new AutoSave(interval);
        }

        return autosave;
    }

    /**
     * Fetches the config file that is used for the world settings
     *
     * @return the config file for the world settings
     */
    public synchronized ConfigSection getConfig() {
        if (config == null) {
            boolean isNew = false;

            File file = new File("config" + File.separatorChar + "world.yml");
            if (!file.exists()) {
                File dist = new File("config" + File.separatorChar + "world.yml.dist");
                if (dist.exists()) {
                    try {
                        Files.copy(dist, file);
                    } catch (IOException e) {
                        Log.warning("Could not copy " + dist + " to " + file);
                    }
                } else {
                    Log.warning(dist + " does not exist. Can't copy server config to " + file + "!");
                }
                isNew = true;
            }

            FileConfig config = new FileConfig(file);
            try {
                config.reload();
            } catch (IOException e) {
                Log.warning("Error parsing world.yml! Exiting...");
                e.printStackTrace();
                System.exit(3);
            }

            if (isNew) {
                ConfigSetup.world(config);
                try {
                    config.save();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.warning("Failed to save world.yml file!");
                }
            }
            this.config = config;
        }
        return config;
    }

    /**
     * The {@link Session} for the World
     *
     * @return The {@link Session} for the World
     */
    public Session getSession() {
        return getDatabase().getSession();
    }

    /**
     * Fetches the SQL database for the world
     *
     * @return the world database
     */
    public synchronized Database getDatabase() {
        if (database == null) {
            try {
                Log.debug("Connecting to database...");
                // Database initialization
                ConfigSection c = getConfig().getSection("database");
                database = new Database(new MySQLC3P0Core(c.getString("host", "localhost"), c.getString("user", "root"), c.getString("pass", ""), c.getString("database", "database"), c.getString("port", "3306")));
                Log.debug("Database connection established.");
            } catch (ConnectionException e) {
                Log.severe("Failed to establish database connection, exiting.");
                e.printStackTrace();
                System.exit(1);
            }

            // Add all of our standard repositories
            database.addRepository(new NPCSpawnRepository());
            database.addRepository(new ItemTypeRepository());
            database.addRepository(new EquipmentRepository());
            database.addRepository(new NPCTypeRepository());
            database.addRepository(new NPCGroupRepository());
            database.addRepository(new VendorRepository());
            database.addRepository(new VendorItemRepository());
            database.addRepository(new Warp.DestinationRepository());

            // We don't really need repositories for these
            database.addEntity(NPCGroupLoot.class);
            database.addEntity(NPCGroupLootGuarantee.class);
            database.addEntity(AmmoType.class);
            database.addEntity(ItemAmmoType.class);
        }
        return database;
    }

    /**
     * The interactions for the server. These are a convenient method of writing Actions without having to create
     * a new class for each one. This lazily initializes the manager, if it's not yet ready.
     *
     * @return the {@link InteractionManager}
     */
    public synchronized InteractionManager getInteractions() {
        if (interactions == null) {
            interactions = new InteractionManager();
        }
        return interactions;
    }

    public long getStartTime() {
        return this.started;
    }

    public synchronized VendorManager getVendors() {
        if (vendors == null) {
            vendors = new VendorManager();
        }
        return vendors;
    }

    public String getRegion() {
        return config.getString("world.region", "");
    }

    public String getActivity() {
        return config.getString("world.activity", "");
    }

    public String getIP() {
        return config.getString("world.ip", "127.0.0.1");
    }

    public int getFlags() {
        return config.getInt("world.flags");
    }

    public int getWorldId() {
        int id = logon.getWorldId();
        if (id == -1) throw new IllegalStateException("Server not yet initialized.");
        return id;
    }

    public void broadcast(String message) {
        for (Persona p : this.getPersonas()) {
            p.sendMessage(message);
        }
    }

    /**
     * Fetches the Map Manager
     *
     * @return the Map Manager
     */
    public synchronized MapManager getMaps() {
        if (this.maps == null) {
            this.maps = new MapManager(new File("maps"));
            getEvents().register(this.maps);

            WorldMap map = this.maps.get("mainland");
            if (map == null) {
                try {
                    map = new StandardMap("mainland");
                    this.maps.persist(map);
                    this.maps.save(map);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        return this.maps;
    }

    /**
     * Fetches the client with the given UUID. This searches the currently
     * online players, and then the lobby players. If none is found with the
     * given UUID, then this returns null.
     *
     * @param uuid the UUID for the session
     * @return the client or null if not found
     */
    public Client getClient(long uuid) {
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

    /**
     * Fetch the active JavaScript scope. Never null
     *
     * @return The root JS scope
     */
    public ScriptEnvironment getScriptEnvironment() {
        return scriptEnvironment;
    }

    /**
     * Loads this server and starts it so that players can begin connecting.
     *
     * @throws IOException  if there is an issue with the config or map
     * @throws SQLException
     */
    public void load() throws IOException, SQLException {
        this.thread.submit(new ServerStartRunnable(this));
        this.thread.start();

        Runnable maskUpdate = new Runnable() {
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
                            if (!pl.isLoaded()) continue;
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.warning("Error processing mask updates.");
                } finally {
                    update.stop();
                }
                Core.submit(this, getConfig().getInt("update-interval", 30), false);
            }
        };
        Core.submit(maskUpdate, getConfig().getInt("update-interval", 30), false);
    }

    public LogonConnection getLogon() {
        return logon;
    }

    /**
     * Fetches the given client by name. This searches currently online players
     * first, and then the lobby players. By nature, this method cannot return
     * any AI players (As they do not implement Client)
     *
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
     *
     * @return the command manager instance
     */
    public synchronized CommandManager getCommands() {
        if (commands == null) {
            Log.info("Loading commands...");
            commands = new CommandManager(events);

            /**
             * Loads the command manager and all of its commands.
             */
            commands.register("animate", new Animate());
            commands.register("announce", new Announce());
            commands.register("ascend", new Ascend());
            commands.register("autocasts", new Autocasts());
            commands.register("bankcmd", new BankCmd());
            commands.register("character", new org.maxgamer.rs.command.commands.Character());
            commands.register("clear", new Clear());
            commands.register("clients", new Clients());
            commands.register("clip", new Clip());
            commands.register("closeinterface", new CloseInterface());
            commands.register("connections", new Connections());
            commands.register("copycat", new CopyCat());
            commands.register("debug", new Debug());
            commands.register("descend", new Descend());
            commands.register("dialoguecmd", new DialogueCmd());
            commands.register("die", new Die());
            commands.register("gc", new GC());
            commands.register("gear", new Gear());
            commands.register("gfx", new GFX());
            commands.register("hide", new Hide());
            commands.register("hideobjects", new HideObjects());
            commands.register("instance", new Instance());
            commands.register("interfacelist", new InterfaceList());
            commands.register("interfaceshow", new InterfaceShow());
            commands.register("item", new Item());
            commands.register("itemscriptdump", new ItemScriptDump());
            commands.register("kick", new Kick());
            commands.register("list", new Who());
            commands.register("logonstatus", new LogonStatus());
            commands.register("modulecmd", new ModuleCmd());
            commands.register("nearby", new Nearby());
            commands.register("position", new GPS());
            commands.register("queues", new Queues());
            commands.register("rangegear", new RangeGear());
            commands.register("rank", new Rank());
            commands.register("reconnect", new Reconnect());
            commands.register("reload", new Reload());
            commands.register("restore", new Restore());
            commands.register("save", new Save());
            commands.register("servers", new Servers());
            commands.register("skilllevel", new SkillLevel());
            commands.register("sort", new Sort());
            commands.register("sound", new Sound());
            commands.register("spawn", new Spawn());
            commands.register("spawnNPC", new SpawnNPC());
            commands.register("spawnobject", new SpawnObject());
            commands.register("spellbookcmd", new org.maxgamer.rs.command.commands.SpellbookCmd());
            commands.register("status", new Status());
            commands.register("stop", new Stop());
            commands.register("sudo", new Sudo());
            commands.register("swapprayer", new SwapPrayer());
            commands.register("teleport", new Teleport());
            commands.register("timings", new Timings());
            commands.register("title", new Title());
            commands.register("tphere", new Tphere());
            commands.register("TPTo", new TPTo());
            commands.register("vendor", new Vendor());
            commands.register("whisper", new Whisper());
            commands.register("config", new Config());
            commands.register("version", new Version());
            commands.register("despawnobject", new DespawnObject());
            commands.register("pcol", new ParticleColour());
            commands.register("whois", new WhoIs());
            commands.register("warp", new Warp());
            commands.register("organise", new Organise());

            ConfigSection config = getConfig().getSection("commands", null);
            if (config != null) {
                for (String alias : config.getKeys()) {
                    Command command = commands.getCommand(config.getString(alias));
                    if (command == null) {
                        continue;
                    }

                    commands.register(alias, command);
                }
            }

            Log.info("...Loaded commands!");
        }
        return commands;
    }

    /**
     * Fetches the GroundItemManager for the server. You generally shouldn't
     * need to use this
     *
     * @return the ground item manager
     */
    public synchronized GroundItemManager getGroundItems() {
        if (this.groundItems == null) {
            this.groundItems = new GroundItemManager();
            this.groundItems.queue(1);
        }

        return groundItems;
    }

    /**
     * Returns the EventManager for the server which allows registration of
     * event listeners and calling of events.
     *
     * @return the event manager.
     */
    public synchronized EventManager getEvents() {
        if (events == null) {
            Log.debug("Loading EventManager...");
            events = new EventManager();
            events.register(new PrayerListener());

            Log.debug("...Loaded EventManager!");
        }
        return events;
    }

    /**
     * Unloads all modules, calls a new ServerShutdownEvent, shuts down the
     * network and save's all player data.
     */
    public void shutdown() throws InterruptedException {
        File shutdown = new File("shutdown.js");
        if (shutdown.exists()) {
            JavaScriptCallFiber js = new JavaScriptCallFiber(getScriptEnvironment(), "shutdown", "run");
            js.start();
            try {
                js.join();
            } catch (ExecutionException e) {
                logger.warn("An exception was raised while running shutdown.js", e);
            }
        }

        ServerShutdownEvent e = new ServerShutdownEvent(this);
        e.call();

        maps.save();

        if (logon.isRunning()) logon.stop(); //Saves all players
        if (modules != null) modules.unload();

        if (network.isRunning()) network.stop();
        thread.shutdown();

        save(); //Also saves all players, but fails since logon is closed

        scheduler.shutdown();
    }

    /**
     * Fetches the player's login lobby
     *
     * @return the player's login lobby
     */
    public synchronized Lobby getLobby() {
        if (lobby == null) {
            this.lobby = new Lobby();
        }

        return lobby;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Collection<Client> getClients() {
        ArrayList<Client> clients = new ArrayList<>(lobby.size() + personas.getCount());
        for (Persona p : personas) {
            if (p instanceof Client) clients.add((Client) p);
        }
        clients.addAll(lobby.getPlayers());
        return clients;
    }

    /**
     * Fetches a list of all player indexes on the server.
     *
     * @return a list of all player indexes on the server.
     */
    public PersonaList getPersonas() {
        return personas;
    }

    /**
     * Fetches a list of all NPC indexes on the server.
     *
     * @return a list of all NPC indexes on the server.
     */
    public EntityList<NPC> getNPCs() {
        return npcs;
    }

    /**
     * Fetches the given player by name, case insensitive. This method does not
     * autocomplete. It only works for online players.
     *
     * @param name the name of the persona
     * @return the Persona or null if they are not found.
     * @throws NullPointerException if the given name is null
     */
    public Persona getPersona(String name) {
        return personas.getPersona(name);
    }

    /**
     * Fetches the network handler for the server
     *
     * @return the network handler for the server
     */
    public RS2Server getNetwork() {
        return network;
    }

    /**
     * Returns the Server Tick handler, which is used for subscribing and
     * unsubscribing Tickable objects to tick() notifications.
     *
     * @return the Server's Tick Handler.
     */
    public synchronized ServerTicker getTicker() {
        if (ticker == null) {
            ticker = new ServerTicker(Server.this);
        }
        return ticker;
    }

    /**
     * Saves all currently online players to disk.
     */
    public void save() {
        int failures = getMaps().save();
        if (failures > 0) {
            Log.warning("Failed to save " + failures + " maps.");
        }

        Core.submit(new Runnable() {
            @Override
            public void run() {
                ServerSaveEvent e = new ServerSaveEvent();
                e.call();

                if (!Core.getServer().getClients().isEmpty()) {
                    Core.getServer().getLogon().getAPI().save(Core.getServer().getClients());
                }
                int errors = getMaps().save();
                if (errors != 0) {
                    Log.warning(errors + " errors occured while saving worlds.");
                }
            }
        }, true);
    }

    /**
     * The plugin system that loads/unloads modules at runtime.
     *
     * @return the module system manager
     */
    public synchronized ModuleLoader getModules() {
        if (modules == null) {
            modules = new ModuleLoader(new File("modules"), "class");
        }

        return modules;
    }

    public ServerExecutor getThread() {
        return thread;
    }

    public int getTicks() {
        if (getTicker() == null) return 0;
        return getTicker().getTicks();
    }
}