package org.maxgamer.rs.core.server;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.maxgamer.rs.lib.log.Log;

/**
 * @author netherfoam
 */
public class ServerThreadTask implements Future<Void>, Runnable {
	private static final int STATE_WAIT = 0;
	private static final int STATE_RUN = 1;
	private static final int STATE_CANCEL = 2;
	private static final int STATE_DONE = 3;
	
	private Runnable r;
	private int state = STATE_WAIT;
	private Object lock = new Object();
	
	public ServerThreadTask(Runnable r) {
		if (r == null) throw new NullPointerException("Runnable may not be null for a ServerThreadTask!");
		this.r = r;
	}
	
	public void run() {
		synchronized (lock) {
			state = STATE_RUN;
			try {
				r.run();
			}
			catch (Throwable t) {
				Log.warning("ServerThreadTask threw an exception. Runnable: " + r);
				t.printStackTrace();
			}
			state = STATE_DONE;
			lock.notifyAll();
		}
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (state == STATE_WAIT) {
			state = STATE_CANCEL;
			return true;
		}
		return false;
	}
	
	@Override
	public Void get() throws InterruptedException, ExecutionException {
		synchronized (lock) {
			if (state == STATE_DONE) return null;
			if (state == STATE_CANCEL) return null; //Should this throw an Exception?
			lock.wait();
		}
		
		return null;
	}
	
	@Override
	public Void get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
		synchronized (lock) {
			if (state == STATE_DONE) return null;
			if (state == STATE_CANCEL) return null; //Should this throw an Exception?
			lock.wait(unit.toMillis(timeout));
			
			if (state == STATE_CANCEL) return null; //Should this throw an Exception?
			if (state != STATE_DONE) throw new TimeoutException();
		}
		
		return null;
	}
	
	@Override
	public boolean isCancelled() {
		return state == STATE_CANCEL;
	}
	
	@Override
	public boolean isDone() {
		return state == STATE_DONE;
	}
	
	@Override
	public String toString() {
		return "ServerThreadTask: " + r.toString();
	}
}