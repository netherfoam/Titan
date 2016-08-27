package org.maxgamer.rs.core;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import org.maxgamer.rs.core.server.ServerExecutor;
import org.maxgamer.rs.core.server.ServerThreadTask;

/**
 * Thread class that handles scheduling of tasks into a thread pool.
 *
 * @author netherfoam
 */
public class Scheduler extends Timer {
	private ExecutorService pool;
	private ServerExecutor primary;
	
	/**
	 * Constructs a new Scheduler.
	 * @param pool the execution service to use to make tasks execute.
	 */
	public Scheduler(ServerExecutor primary, ExecutorService pool) {
		super("Scheduler Service");
		if (primary == null) throw new IllegalArgumentException("Primary ServerThread may not be null");
		if (pool == null) throw new IllegalArgumentException("ExecutorService may not be null");
		
		this.primary = primary;
		this.pool = pool;
	}
	
	/**
	 * Queues the given task for execution after the given delay has passed.
	 * @param r the runnable to execute
	 * @param delayMs the number of milliseconds to execute it after.
	 * @throws NullPointerException if the runnable is null
	 * @throws IllegalArgumentException if the delay is less than 0.
	 */
	public ServerThreadTask queue(Runnable r, long delayMs, final boolean async) {
		if (r == null) throw new NullPointerException("Runnable may not be null");
		if (delayMs < 0) throw new IllegalArgumentException("Delay must be >= 0ms, given " + delayMs);
		
		final ServerThreadTask task = new ServerThreadTask(r);
		this.schedule(new TimerTask() {
			@Override
			public void run() {
				if (async) {
					pool.submit(task);
				}
				else {
					primary.submit(task);
				}
			}
		}, delayMs);
		
		return task;
	}
	
	/**
	 * This cancels all scheduled tasks (Except those currently running) and
	 * returns. This thread will cease work.
	 */
	public void shutdown() {
		this.cancel();
	}
}