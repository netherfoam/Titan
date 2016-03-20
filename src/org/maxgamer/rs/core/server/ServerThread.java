package org.maxgamer.rs.core.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.structure.Util;

import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.fibers.FiberScheduler;

/**
 * @author netherfoam
 */
public class ServerThread implements Executor {
	private ArrayList<Runnable> queue;
	private Server server;
	private long start;
	
	private Thread thread;
	
	private long working = 0;
	
	private long lastPrint = 0;
	private int lastTicks = 0;
	
	private FiberScheduler fex;
	
	public ServerThread(Server server) {
		this.server = server;
		this.queue = new ArrayList<Runnable>();
		this.fex = new FiberExecutorScheduler("ServerThread-FiberExec", this);
	}
	
	public Server getServer() {
		return this.server;
	}
	
	/**
	 * Fetches the amount of time that the ServerThread has been active as a
	 * decimal. Eg, 1.0 = used 100% of the time, and 0.20 means used 20% of the
	 * time. You may reset this (To sample over a certain time) via resetUsage()
	 * @return the amount of time the ServerThread has been active
	 */
	public double getUsage() {
		long now = System.currentTimeMillis();
		long time = now - this.start;
		return Calc.betweend(0, this.working / (double) time, 1);
	}
	
	/**
	 * Returns true if the current thread is the server thread
	 * @return true if the current thread is the server thread
	 */
	public boolean isServerThread() {
		if (this.thread == null) {
			return false;
		}
		if (Thread.currentThread().getId() == this.thread.getId()) {
			return true;
		}
		return false;
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
	 * Joins with the server thread until it stops
	 */
	public void shutdown() {
		Thread end = this.thread;
		this.thread = null;
		try {
			end.join();
		}
		catch (InterruptedException e) {
			//What can you do?
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the server thread running.
	 * @throws IllegalStateException if the server thread is running
	 */
	public void start() {
		if (this.thread != null) {
			throw new IllegalStateException("ServerThread already started.");
		}
		this.start = System.currentTimeMillis();
		this.thread = new Thread("ServerThread-thread") {
			@Override
			public void run() {
				long time;
				while (ServerThread.this.thread != null) {
					ArrayList<Runnable> tasks;
					synchronized (ServerThread.this.queue) {
						if (ServerThread.this.queue.isEmpty()) {
							try {
								ServerThread.this.queue.wait();
							}
							catch (InterruptedException e) {
							}
						}
						assert ServerThread.this.queue.isEmpty() == false;
						
						time = System.currentTimeMillis();
						tasks = new ArrayList<Runnable>(ServerThread.this.queue);
						ServerThread.this.queue.clear();
					}
					
					//for(Runnable r : tasks){
					for (int i = 0; i < tasks.size(); i++) {
						Runnable r = tasks.get(i);
						try {
							//Log.debug("Running task " + r);
							r.run();
						}
						catch (Throwable t) {
							t.printStackTrace(System.out);
						}
						
						synchronized (r) {
							//Notifies any future's waiting on get() that R is completed.
							r.notifyAll();
						}
					}
					ServerThread.this.working += (System.currentTimeMillis() - time);
					
					if (lastPrint + 120000 < System.currentTimeMillis()) {
						Log.info("-- Server Status at " + new Date().toString() + "--");
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
						
						Log.info("Total level of all players: " + total + ", Uptime: " + Util.toDuration(System.currentTimeMillis() - getServer().getStartTime()));
						lastPrint = System.currentTimeMillis();
						lastTicks = Core.getServer().getTicks();
					}
				}
			}
		};
		this.thread.setContextClassLoader(Core.CLASS_LOADER);
		this.thread.setPriority(Thread.MAX_PRIORITY);
		this.thread.start();
	}
	
	public Future<Void> submit(Runnable r) {
		if(r instanceof ServerTicker){
			Log.debug("submitted serverticker");
		}
		ServerThreadTask t = new ServerThreadTask(r);
		synchronized (this.queue) {
			this.queue.add(r);
			this.queue.notify();
		}
		return t;
	}
	
	public FiberScheduler getFiberScheduler() {
		return fex;
	}
	
	@Override
	public void execute(Runnable command) {
		this.submit(command);
	}

	/**
	 * Asserts that the current thread is the ServerThread / Primary thread.
	 * @throws IllegalThreadException if the assertion fails
	 */
	public void assertThread(){
		if(Thread.currentThread() != this.thread){
			throw new IllegalThreadException("Attempted to run thread " + Thread.currentThread() + " but must be only run on the ServerThread " + this.thread);
		}
	}
}