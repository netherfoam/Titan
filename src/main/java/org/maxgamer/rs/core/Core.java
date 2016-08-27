package org.maxgamer.rs.core;

import org.maxgamer.rs.cache.Cache;
import org.maxgamer.rs.cache.CacheFile;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.MapCache;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;
import org.maxgamer.rs.command.ConsoleSender;
import org.maxgamer.rs.core.server.Server;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.structure.timings.NullTimings;
import org.maxgamer.rs.structure.timings.Timings;
import org.maxgamer.rs.util.log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Represents the core, responsible for running the server. Should keep this
 * open to allow for running of multiple servers.
 * <p>
 * This class contains a static thread pool, shared across the JVM.
 *
 * @author netherfoam
 */
public class Core {
    static {
        AUTHOR = Core.class.getPackage().getImplementationVendor();
        BUILD = Core.class.getPackage().getImplementationVersion();
    }

    /**
     * This class loader is used so that the Module system works. It is a shared
     * ClassLoader that allows the Module system to load classes, independant of
     * where the class is stored. For example, AIModule should be able to access
     * a class from MusicModule using this ClassLoader.
     */
    public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    /**
     * The hostname that built the project and build number
     */
    public static final String AUTHOR;

    /**
     * The version
     */
    public static final String BUILD;

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
     * The RS cache that is to be loaded and used.
     */
    private static Cache cache;

    /**
     * Initializes the core of the server
     *
     * @throws Exception If there was an error binding the port or loading the
     *                   cache.
     */
    public static void init() throws Exception {
        // This prevents Quasar from warning us about a missing JavaAgent, since we instrument as part of
        // the build process, and using a URLClassLoader for the modules.
        System.setProperty("co.paralleluniverse.fibers.disableAgentWarning", "true");

        Log.info("Author: " + Core.AUTHOR + " Build: " + Core.BUILD);

        final long start = System.currentTimeMillis();

        server = new Server(); //Binds port port
        server.load();

        // This is run when we get CTRL + C as well
        Runtime.getRuntime().addShutdownHook(new Thread(new CoreShutdownHook(), "Shutdown Hook"));

        getServer().getThread().submit(new Runnable() {
            @Override
            public void run() {
                Log.info("Booted in " + (System.currentTimeMillis() - start) + "ms.");
            }
        });
    }

    /**
     * Fetches the cache interface for the server.
     *
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
                            } catch (FileNotFoundException e) {
                                continue;
                            }
                            try {
                                MapCache.getObjects(x, y);
                            } catch (IOException e) {
                                //File is broken or encrypted and we don't have the key.
                                r.remove(ref.getId());
                                //Blacklist the file
                                cacheCfg.set("encryptedMaps." + ref.getId(), ref.getId());
                            }
                        }
                    }
                    // Update config & save
                    cacheCfg.set("modified." + main.getName(), main.lastModified());
                    cacheCfg.set("modified." + xtea.getName(), xtea.lastModified());
                    cacheCfg.save();
                } else {
                    // We previously worked on this cache, and listed all broken files.
                    // This is faster than testing each file if it's broken or not.
                    for (String refId : cacheCfg.getSection("encryptedMaps").getKeys()) {
                        int referenceId = cacheCfg.getInt("encryptedMaps." + refId, -1);
                        r.remove(referenceId);
                    }
                }

                // Now we re-encode the raw version of the data
                r.setVersion(r.getVersion() + 1);
                f.setData(r.encode()); //Set the file data to the reference table
                cache.setRaw(255, IDX.LANDSCAPES, f.encode());
                cache.rebuildChecksum();
            } catch (IOException e) {
                Log.severe("There was an error loading the cache. Please ensure you placed it in the cache/ folder and that it is in tact and readable.");
                e.printStackTrace();
                System.exit(2);
            }
        }

        return cache;
    }


    /**
     * Fetches the console command sender
     *
     * @return The console as a command sender.
     */
    public static ConsoleSender getConsole() {
        if (console == null) {
            console = new ConsoleSender();
        }

        return console;
    }

    /**
     * Submits a Runnable task for execution and returns a Future representing
     * that task. The Future's get method will return null upon successful
     * completion. The task is scheduled as soon as possible by the thread pool,
     * and may not be done in sync.
     *
     * @param r The runnable task to execute
     */
    public synchronized static Future<?> submit(Runnable r, boolean async) {
        if (async) return getThreadPool().submit(r);
        else {
            return getServer().getThread().submit(r);
        }
    }

    /**
     * Submits the given task for execution after the given number of
     * milliseconds delay. The task is guaranteed to wait at least delay
     * milliseconds, but is not guaranteed to be executed if the task list is
     * saturated.
     *
     * @param r     The runnable
     * @param delay The task delay in milliseconds.
     */
    public synchronized static Future<Void> submit(Runnable r, long delay, boolean async) {
        return getServer().getScheduler().queue(r, delay, async);
    }

    /**
     * Retrieves the current server that is running.
     *
     * @return the server
     */
    public static Server getServer() {
        return server;
    }

    /**
     * Shuts down the server
     */
    protected static void shutdown() throws InterruptedException {
        Log.info("Shutting down...");
        getServer().shutdown();
        getConsole().stop();
        getThreadPool().shutdown();
        getThreadPool().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Fetch the timer for events
     * @return the timer for events
     */
    public synchronized static Timings getTimings() {
        if (timings == null) {
            if (getServer().getConfig().getBoolean("timings")) {
                timings = new Timings();
            } else {
                timings = new NullTimings();
            }
        }

        return timings;
    }

    /**
     * Fetch the server's thread pool for async tasks
     * @return the async task thread pool
     */
    public static ExecutorService getThreadPool() {
        if (threadPool == null) {
            int threads = Runtime.getRuntime().availableProcessors() - 1;
            if (threads <= 0) threads = 1;
            threadPool = Executors.newFixedThreadPool(threads, new CoreThreadFactory());
        }

        return threadPool;
    }

    private Core() {
        //Private Constructor
    }
}
