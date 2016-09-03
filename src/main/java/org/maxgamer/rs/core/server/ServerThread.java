package org.maxgamer.rs.core.server;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.structure.Util;
import org.maxgamer.rs.util.Calc;
import org.maxgamer.rs.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * @author netherfoam
 */
public class ServerThread extends Thread {
    /**
     * The server executor
     */
    private final ServerExecutor sex;

    /**
     * The queue of runnables we need to run for the current tick
     */
    private final ArrayList<Runnable> queue;

    /**
     * The last time we printed the server status
     */
    private long lastPrint;

    /**
     * The last tick we printed the server status on
     */
    private int lastTicks;

    /**
     * The amount of time we've been "busy" processing runnables for, since the start
     */
    private long working;

    /**
     * The time when the run() method was entered
     */
    private long start;

    /**
     * True if we're shutting down and would like to terminate soon
     */
    private boolean finishing = false;

    /**
     * Constructs a new {@link ServerThread}
     *
     * @param sex  the server executor
     * @param name the name of the thread
     */
    public ServerThread(ServerExecutor sex, String name) {
        super(name);
        this.sex = sex;
        this.queue = new ArrayList<Runnable>();
    }

    @Override
    public void run() {
        this.start = System.currentTimeMillis();
        long time;
        while (!finishing) {
            ArrayList<Runnable> tasks;
            synchronized (queue) {
                if (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                    }
                }

                time = System.currentTimeMillis();
                tasks = new ArrayList<Runnable>(queue);
                queue.clear();
            }

            int size = tasks.size();
            for (int i = 0; i < size; i++) {
                Runnable r = tasks.get(i);
                try {
                    r.run();
                } catch (Throwable t) {
                    t.printStackTrace(System.out);
                }

                synchronized (r) {
                    //Notifies any future's waiting on get() that R is completed.
                    r.notifyAll();
                }
            }
            this.working += (System.currentTimeMillis() - time);

            if (lastPrint + 120000 < System.currentTimeMillis()) {
                Log.info("Server Status:");
                Log.info("Players: " + Core.getServer().getPersonas().getCount() + "/" + Core.getServer().getPersonas().getMax() + ", NPCs: " + Core.getServer().getNPCs().getCount() + "/" + Core.getServer().getNPCs().getMax());
                Log.info("Primary Thread Load: " + String.format("%.2f", (getUsage() * 100)) + "%, " + ", Ticks/sec: " + ((double) (Core.getServer().getTicks() - lastTicks) / (double) ((System.currentTimeMillis() - lastPrint) / 1000.0)) + ", Active Threads: " + Thread.activeCount());
                Log.info("RAM (JVM): " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB, RAM (Used): " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "MB");
                long period = 0;
                int up = 0;
                int down = 0;
                for (Session s : Core.getServer().getNetwork().getSessions()) {
                    period += System.currentTimeMillis() - s.getLastBandwidthReset();
                    up += s.getUpload();
                    down += s.getDownload();
                    s.resetBandwidth();
                }
                if (period != 0) {
                    Log.info(String.format("Network Upload: %.2fkBps, Down: %.2fkBps", up * 1000 / period / 1000.0, down * 1000 / period / 1000.0));
                }
                long total = 0;
                for (Persona p : Core.getServer().getPersonas()) {
                    for (SkillType t : SkillType.values()) {
                        total += p.getSkills().getLevel(t);
                    }
                }

                Log.info("Total level of all players: " + total + ", Uptime: " + Util.toDuration(System.currentTimeMillis() - start));
                lastPrint = System.currentTimeMillis();
                lastTicks = Core.getServer().getTicks();
            }
        }
    }

    /**
     * Fetches the amount of time that the ServerThread has been active as a
     * decimal. Eg, 1.0 = used 100% of the time, and 0.20 means used 20% of the
     * time. You may reset this (To sample over a certain time) via resetUsage()
     *
     * @return the amount of time the ServerThread has been active
     */
    public double getUsage() {
        long now = System.currentTimeMillis();
        long time = now - this.start;
        return Calc.betweend(0, this.working / (double) time, 1);
    }

    /**
     * Resets the current usage / running / uptime information for the
     * ServerThread.
     */
    public void resetUsage() {
        this.working = 0;
        this.start = System.currentTimeMillis();
    }

    /**
     * The queue of runnables we want to run. This is a reference, not a copy.
     * Adding runnables here will safely update the thread.
     *
     * @return the runnables
     */
    public ArrayList<Runnable> getQueue() {
        return queue;
    }

    /**
     * Requests that the given runnable be run in the next tick
     *
     * @param r the runnable
     * @return the future object
     */
    public Future<Void> submit(Runnable r) {
        ServerThreadTask t = new ServerThreadTask(r);
        synchronized (this.queue) {
            this.queue.add(r);
            this.queue.notify();
        }

        return t;
    }

    /**
     * Begins terminating this {@link ServerThread}. This is non-blocking
     */
    public void terminate() {
        finishing = true;
    }
}
