package org.maxgamer.rs.core;

import org.maxgamer.rs.cache.Cache;
import org.maxgamer.rs.command.ConsoleSender;
import org.maxgamer.rs.core.server.Server;
import org.maxgamer.rs.structure.timings.NullTimings;
import org.maxgamer.rs.structure.timings.Timings;
import org.maxgamer.rs.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Represents the core, responsible for running the server. Should keep this
 * open to allow for running of multiple servers.
 * <p>
 * This class contains a static thread pool, shared across the JVM.
 *
 * @author netherfoam
 */
public class Core {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Core.class);
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

    static {
        AUTHOR = Core.class.getPackage().getImplementationVendor();
        BUILD = Core.class.getPackage().getImplementationVersion();
    }

    private Core() {
        //Private Constructor
    }

    // TODO: Doc
    public static void start() throws IOException, SQLException {
        // This prevents Quasar from warning us about a missing JavaAgent, since we instrument as part of
        // the build process, and using a URLClassLoader for the modules.
        System.setProperty("co.paralleluniverse.fibers.disableAgentWarning", "true");

        Log.info("Author: " + Core.AUTHOR + " Build: " + Core.BUILD);

        final long start = System.currentTimeMillis();

        int threads = Runtime.getRuntime().availableProcessors() - 1;
        if (threads <= 0) threads = 1;
        threadPool = Executors.newFixedThreadPool(threads, new CoreThreadFactory());

        console = new ConsoleSender();
        cache = Cache.init();
        server = new Server(); //Binds port port

        if (getServer().getConfig().getBoolean("timings")) {
            timings = new Timings();
        } else {
            timings = new NullTimings();
        }

        getServer().load();
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
    public static Cache getCache() {
        return cache;
    }

    /**
     * Fetches the console command sender
     *
     * @return The console as a command sender.
     */
    public static ConsoleSender getConsole() {
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
     *
     * @return the timer for events
     */
    public static Timings getTimings() {
        return timings;
    }

    /**
     * Fetch the server's thread pool for async tasks
     *
     * @return the async task thread pool
     */
    public static ExecutorService getThreadPool() {
        return threadPool;
    }
}
