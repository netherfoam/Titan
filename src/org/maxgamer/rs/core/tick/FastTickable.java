package org.maxgamer.rs.core.tick;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.ServerTicker;

/**
 * @author netherfoam
 */
public abstract class FastTickable {
	private static class RunRequest implements Runnable {
		private boolean cancel = false;
		private FastTickable ft;
		
		private RunRequest(FastTickable ft) {
			this.ft = ft;
		}
		
		@Override
		public void run() {
			if (cancel) return;
			else {
				ft.req = null;
				ft.run();
			}
		}
	}
	
	private long lastRun = 0;
	private RunRequest req;
	
	public FastTickable() {
	}
	
	public boolean isQueued() {
		return req != null && req.cancel == false;
	}
	
	public void queue(){
		this.queue(0);
	}
	
	public void queue(int minimumDelayMs) {
		if (isQueued()) {
			throw new IllegalStateException("Cannot queue() " + this.getClass().getSimpleName() + " because it is already queued.");
		}
		
		long waited = System.currentTimeMillis() - lastRun;
		long delay = ServerTicker.getTickDuration() - waited;
		if (delay < minimumDelayMs) delay = minimumDelayMs;
		this.req = new RunRequest(this);
		Core.submit(req, delay, false);
	}
	
	public void cancel() {
		if (req == null) return;
		req.cancel = true;
		req = null;
	}
	
	private void run() {
		long now = System.currentTimeMillis();
		/*if (now < lastRun + ServerTicker.getTickDuration()) {
			throw new RuntimeException("May not run FastTickable, as the Cycle time has not expired. Still have to wait " + (ServerTicker.getTickDuration() + lastRun - now) + "ms.");
		}*/
		
		this.lastRun = now;
		try {
			tick();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public abstract void tick();
}