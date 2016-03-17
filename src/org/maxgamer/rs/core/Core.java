package org.maxgamer.rs.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.maxgamer.rs.cache.Cache;
import org.maxgamer.rs.cache.CacheFile;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.MapCache;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;
import org.maxgamer.rs.command.ConsoleSender;
import org.maxgamer.rs.core.server.Server;
import org.maxgamer.rs.lib.Files;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.lib.log.Logger.LogLevel;
import org.maxgamer.rs.model.entity.mob.combat.RangeAttack;
import org.maxgamer.rs.model.entity.mob.npc.NPCGroup;
import org.maxgamer.rs.model.item.ItemDefinition;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.structure.sql.Database;
import org.maxgamer.rs.structure.sql.MySQLC3P0Core;
import org.maxgamer.rs.structure.sql.SQLiteCore;
import org.maxgamer.rs.structure.timings.Timings;
import org.maxgamer.rs.tools.ConfigSetup;

/**
 * Represents the core, responsible for running the server. Should keep this
 * open to allow for running of multiple servers.
 * 
 * This class contains a static thread pool, shared across the JVM.
 * @author netherfoam
 *
 */
public class Core {
	/**
	 * The hostname that built the project and build number
	 */
	public static final String AUTHOR;
	
	/**
	 * The version
	 */
	public static final String BUILD;
	
	static{
		String vendor = Core.class.getPackage().getImplementationVendor();
		AUTHOR = vendor;
		
		String version = Core.class.getPackage().getImplementationVersion();
		BUILD = version;
	}
	
	/**
	 * Handles scheduling of tasks for a later date, optionally on the main
	 * thread or on an async thread
	 */
	private static Scheduler scheduler;
	
	/**
	 * A thread pool for handling async tasks that are not on the main server
	 * thread.
	 */
	private static ExecutorService threadPool;
	
	/**
	 * The server that is currently running.
	 */
	private static Server server;
	
	/**
	 * The command sender that the console uses, this has admin rights.
	 */
	private static ConsoleSender console;
	
	/**
	 * The timings for tracking lag and expensive operations down
	 */
	private static Timings timings;
	
	/**
	 * The database used for world information, such as NPC spawns, ammo types
	 * and item definitions
	 */
	private static Database world;
	
	/**
	 * The config for the world/servers
	 */
	private static FileConfig worldCfg;
	
	/**
	 * The RS cache that is to be loaded and used.
	 */
	private static Cache cache;
	
	/**
	 * This class loader is used so that the Module system works. It is a shared
	 * ClassLoader that allows the Module system to load classes, independant of
	 * where the class is stored. For example, AIModule should be able to access
	 * a class from MusicModule using this ClassLoader.
	 */
	public static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
	
	/**
	 * Initializes the core of the server
	 * @param threads the max number of threads to run with, or <= 0 to use the
	 *        number of processors available to the runtime as the number of
	 *        threads.
	 * @throws Exception If there was an error binding the port or loading the
	 *         cache.
	 */
	public static void init(int threads, String[] args) throws Exception {
		getTimings(); //Force load timings
		getWorldConfig(); //Force load worldCfg
		
		//We allow args to override the config parameters eg world.port=xxx will override the config value
		for (String arg : args) {
			if (arg.contains("=") == false) continue;
			String[] parts = arg.split("=");
			if (parts.length != 2) {
				Log.info("Bad program argument given: " + arg);
				continue;
			}
			
			getWorldConfig().set(parts[0], parts[1]);
		}
		
		if (threads <= 0) threads = Runtime.getRuntime().availableProcessors();
		
		Log.init(LogLevel.valueOf(getWorldConfig().getString("log.level", LogLevel.INFO.toString())));
		Log.info("Booting on " + new Date().toString() + " --");
		Log.info("Author: " + Core.AUTHOR + ", Build: " + Core.BUILD + ", Threads: " + threads);
		
		final long start = System.currentTimeMillis();
		
		threadPool = Executors.newFixedThreadPool(threads, new ThreadFactory() {
			private int nextThreadId = 0;
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "ExecutorService " + nextThreadId++);
				t.setContextClassLoader(CLASS_LOADER);
				t.setPriority(Thread.NORM_PRIORITY);
				t.setDaemon(true);
				return t;
			}
		});
		
		getCache(); //Force load the cache
		getWorldDatabase(); //Force load the database
		
		boolean lazy = getWorldConfig().getBoolean("loading.lazy", false);
		
		//Loading
		ItemDefinition.init();
		Log.info("RangeAttack Loading...");
		RangeAttack.init();
		Log.info("NPCGroup Loading...");
		if (!lazy) NPCGroup.reload();
		
		ConfigSection cfg = Core.getWorldConfig().getSection("world");
		server = new Server(cfg); //Binds port port
		scheduler = new Scheduler(server.getThread(), threadPool);
		server.load();
		
		//This is run when we get CTRL + C as well
		Runtime.getRuntime().addShutdownHook(new Thread("Shutdown Hook") {
			@Override
			public void run() {
				Core.submit(new Runnable() {
					@Override
					public void run() {
						//This must be done by the server thread.
						shutdown();
					}
				}, false);
			}
		});
		
		//Hint to the garbage collector it should run now.
		System.gc();
		
		getServer().getThread().submit(new Runnable() {
			@Override
			public void run() {
				Log.info("Booted in " + (System.currentTimeMillis() - start) + "ms.");
			}
		});
	}
	
	/**
	 * Fetches the file that contains org.maxgamer.rs.Core, whether it be a JAR file or a compiled
	 * BIN folder containing .class files.
	 * @return the location of the main program.
	 */
	public static File getCodeSource(){
		try{
			URL url = Core.class.getProtectionDomain().getCodeSource().getLocation();
			return new File(url.toURI().getPath());
			
		}
		catch(URISyntaxException e){
			throw new RuntimeException("Couldn't locate Code Source. Source " + Core.class.getProtectionDomain().getCodeSource().getLocation().toString());
		}
	}
	
	/**
	 * Fetches the cache interface for the server.
	 * @return the cache interface
	 */
	public synchronized static Cache getCache() {
		if (cache == null) {
			try {
				Log.debug("Loading Cache...");
				cache = new Cache();
				cache.load(new File("cache"));
				
				//We store a file as data/cache.yml, this file contains information on files which we should
				//delete from our cache (Encrypted maps). This is done to avoid sending the player maps that
				//we do not have an XTEA key for.
				FileConfig cacheCfg = new FileConfig(new File("data", "cache.yml"));
				cacheCfg.reload();
				//The reference table as a ByteBuffer.
				CacheFile f = cache.getFile(255, IDX.LANDSCAPES);
				//The decoded reference table
				ReferenceTable r = cache.getReferenceTable(IDX.LANDSCAPES);
				
				//Now we figure out if our files have changed
				File main = new File("cache", "main_file_cache.dat2");
				File xtea = cache.getXTEA().getFile();
				//Quick way to check if the file has changed. This does not check the idx files, which may cause issues, but the .dat file and the xtea file are
				//key to allowing/disallowing files from the map cache
				if (main.lastModified() != cacheCfg.getLong("modified." + main.getName()) || xtea.lastModified() != cacheCfg.getLong("modified." + xtea.getName())) {
					Log.debug("Cache change detected. Recalculating!");
					//So we must scan through all of the map files, attempt to parse them, and blacklist broken ones
					for (int x = 0; x < 256; x++) {
						for (int y = 0; y < 256; y++) {
							Reference ref;
							try {
								ref = r.getReferenceByHash("l" + x + "_" + y);
							}
							catch (FileNotFoundException e) {
								continue;
							}
							try {
								MapCache.getObjects(x, y);
							}
							catch (IOException e) {
								//File is broken or encrypted and we don't have the key.
								r.remove(ref.getId());
								//Blacklist the file
								cacheCfg.set("encryptedMaps." + ref.getId(), ref.getId());
							}
						}
					}
					//Update config & save
					cacheCfg.set("modified." + main.getName(), main.lastModified());
					cacheCfg.set("modified." + xtea.getName(), xtea.lastModified());
					cacheCfg.save();
				}
				else {
					//We previously worked on this cache, and listed all broken files.
					//This is faster than testing each file if it's broken or not.
					for (String refId : cacheCfg.getSection("encryptedMaps").getKeys()) {
						int referenceId = cacheCfg.getInt("encryptedMaps." + refId, -1);
						r.remove(referenceId);
					}
				}
				
				//Now we re-encode the raw version of the data
				r.setVersion(r.getVersion() + 1);
				f.setData(r.encode()); //Set the file data to the reference table
				cache.setRaw(255, IDX.LANDSCAPES, f.encode());
				cache.rebuildChecksum();
			}
			catch (IOException e) {
				Log.severe("There was an error loading the cache. Please ensure you placed it in the cache/ folder" + 
						   "and that it is in tact and readable.");
				Log.severe("Failed to load cache");
				Log.severe("Failed to start.");
				e.printStackTrace();
				System.exit(2);
			}
		}
		return cache;
	}
	
	/**
	 * Fetches the config file that is used for the world settings
	 * @return the config file for the world settings
	 */
	public synchronized static FileConfig getWorldConfig() {
		if (worldCfg == null) {
			boolean isNew = false;
			
			File cfgFile = new File("config" + File.separatorChar + "world.yml");
			if(cfgFile.exists() == false){
				File dist = new File("config" + File.separatorChar + "world.yml.dist");
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
			
			worldCfg = new FileConfig(cfgFile);
			try {
				worldCfg.reload();
			}
			catch (IOException e) {
				Log.warning("Error parsing world.yml! Exiting...");
				e.printStackTrace();
				System.exit(3);
			}
			
			if(isNew){
				ConfigSetup.world(worldCfg);
				try {
					worldCfg.save();
				}
				catch (IOException e) {
					e.printStackTrace();
					Log.warning("Failed to save world.yml file!");
				}
			}
		}
		return worldCfg;
	}
	
	/**
	 * Fetches the SQL database for the world
	 * @return the world database
	 */
	public synchronized static Database getWorldDatabase() {
		if (world == null) {
			try {
				//Database initialization
				ConfigSection c = getWorldConfig().getSection("database");
				String type = c.getString("type", "sqlite");
				
				//Logon Database
				if (type.equalsIgnoreCase("mysql")) {
					Log.debug("World using MySQL Database.");
					world = new Database(new MySQLC3P0Core(c.getString("host", "localhost"), c.getString("user", "root"), c.getString("pass", ""), c.getString("database", "database"), c.getString("port", "3306")));
				}
				else {
					Log.debug("World using SQLite Database: " + c.getString("file", "sql" + File.separator + "sqlite.db"));
					world = new Database(new SQLiteCore(new File(c.getString("file", "sql" + File.separator + "sqlite.db"))));
				}
				Log.debug("Database connection established.");
			}
			catch (Exception e) {
				Log.severe("Failed to establish database connection, exitting.");
				e.printStackTrace();
				System.exit(1);
			}
		}
		return world;
	}
	
	/**
	 * Fetches the console command sender
	 * @return The console as a command sender.
	 */
	public static ConsoleSender getConsole() {
		if(console == null){
			console = new ConsoleSender();
		}
		
		return console;
	}
	
	/**
	 * Submits a Runnable task for execution and returns a Future representing
	 * that task. The Future's get method will return null upon successful
	 * completion. The task is scheduled as soon as possible by the thread pool,
	 * and may not be done in sync.
	 * @param r The runnable task to execute
	 */
	public synchronized static Future<?> submit(Runnable r, boolean async) {
		if (async) return threadPool.submit(r);
		else {
			return getServer().getThread().submit(r);
		}
	}
	
	/**
	 * Submits the given task for execution after the given number of
	 * milliseconds delay. The task is guaranteed to wait at least delay
	 * milliseconds, but is not guaranteed to be executed if the task list is
	 * saturated.
	 * @param r The runnable
	 * @param delay The task delay in milliseconds.
	 */
	public synchronized static Future<Void> submit(Runnable r, long delay, boolean async) {
		return scheduler.queue(r, delay, async);
	}
	
	/**
	 * Retrieves the current server that is running.
	 * @return the server
	 */
	public static Server getServer() {
		return server;
	}
	
	/**
	 * Shuts down the server
	 */
	private static void shutdown() {
		Log.info("Shutting down...");
		getServer().shutdown();
		console.stop();
		scheduler.shutdown();
		threadPool.shutdown();
		try {
			threadPool.shutdown();
			threadPool.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {}
		Log.close();
	}
	
	public synchronized static Timings getTimings() {
		if (timings == null) {
			timings = new Timings();
		}
		return timings;
	}
	
	private Core() {
		//Private Constructor
	}
}
