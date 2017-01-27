package org.maxgamer.rs.core.server;

import org.hibernate.Transaction;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.structure.timings.StopWatch;
import org.maxgamer.rs.util.Log;

import java.util.*;

/**
 * @author netherfoam
 */
public class ServerTicker implements Runnable {
    /**
     * The pause between ticks in milliseconds.
     */
    private static int TICK_DURATION = 600;
    /**
     * Number of ticks passed
     */
    private int ticks;
    /**
     * The queue of tasks, sorted in ascending order by the tick they're to be
     * executed on
     */
    private PriorityQueue<TickableWrapper> tickables;
    /**
     * The Server this ServerTicker manages
     */
    private Server server;

    /**
     * Constructs a new ServerTicker for the given server
     *
     * @param server the server to tick
     */
    public ServerTicker(Server server) {
        this.server = server;
        this.tickables = new PriorityQueue<TickableWrapper>(256);
        this.ticks = 0;
    }

    public static int getTickDuration() {
        return TICK_DURATION;
    }

    public static void setTickDuration(int duration) {
        TICK_DURATION = Math.max(0, duration);
    }

    /**
     * The server that this ticker corresponds to
     *
     * @return The server that this ticker corresponds to
     */
    public Server getServer() {
        return server;
    }

    /**
     * Submits the given tickable object to the server, allowing it to have it's
     * tick() method called after the given delay has passed. If the delay is 0,
     * the task will be executed on the next tick. If the delay is 1, the task
     * will be executed on the tick after the next.
     *
     * @param delay the tick delay.
     * @param t     the tickable object.
     * @throws IllegalArgumentException if the delay is less than 0.
     * @throws NullPointerException     if the tickable object is null
     */
    public void submit(int delay, Runnable t) {
        if (delay <= 0) throw new IllegalArgumentException("Tick delay must be > 0! Given " + delay);
        if (t == null) throw new NullPointerException("Tickable must not be null!");
        TickableWrapper task = new TickableWrapper(this.ticks + delay, t);

        synchronized (tickables) {
            tickables.add(task);
        }
    }

    @Override
    public void run() {
        StopWatch tickTimer = Core.getTimings().start("tick");

        ticks++;

        TickableWrapper task;
        // Add all of the tickables to a list of ticks we will execute.
        LinkedList<TickableWrapper> shortlist = new LinkedList<TickableWrapper>();

        synchronized (this.tickables) {
            while ((task = this.tickables.peek()) != null && task.getPeriod() <= ticks) {
                shortlist.add(task);
                this.tickables.remove(); //Remove the task, it will not be re-queued.
            }
        }

        // Every second tick, we go through the tick tasks and run them in opposite order
        Iterator<TickableWrapper> taskIt = shortlist.iterator();

        org.hibernate.Session session = server.getSession();

        Transaction transaction = session.beginTransaction();

        while (taskIt.hasNext()) {
            task = taskIt.next();

            try {
                task.getTick().run();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                Log.warning("Error ticking tickable object. Class: " + task.getTick().getClass().getCanonicalName());
                Log.warning("toString(): " + task.getTick().toString());
                Log.warning("Tick#: " + task.getPeriod());
                Log.warning("Exact task: " + task);
                Log.warning("Queued from: ");
                task.getTrace().printStackTrace(System.out);
            }
        }

        transaction.commit();

        Collection<Session> sessions = server.getNetwork().getSessions();
        sessions = new ArrayList<Session>(sessions);
        Iterator<Session> sit = sessions.iterator();
        while (sit.hasNext()) {
            Session s = sit.next();
            if (System.currentTimeMillis() - 20000 > s.getLastPing()) {
                // Disconnect, timeout.
                Log.debug("Disconnecting " + s + ", timeout (no packets received in " + (System.currentTimeMillis() - s.getLastPing()) + "ms)");
                s.close(false);
            }
        }

        Core.getServer().getDatabase().flush();

        // Say we took 37ms to perform this tick, we want to perform the next
        // tick in 563ms, not 600ms. (37 + 563 = 600)
        int duration = (int) tickTimer.getTime() / 1000000;

        if (duration > getTickDuration() && getTickDuration() >= 600) {
            Log.info("Warning, tick took " + duration + "ms to finish");
        }

        int schedule = getTickDuration() - duration;
        if (schedule < 0) schedule = 0;
        tickTimer.stop();

        Core.submit(this, schedule, false);
    }

    /**
     * Returns the number of ticks that have passed since starting the server.
     *
     * @return The number of ticks.
     */
    public int getTicks() {
        return this.ticks;
    }
}