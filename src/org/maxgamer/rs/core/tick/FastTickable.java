package org.maxgamer.rs.core.tick;

import org.maxgamer.rs.core.Core;

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
	private int cycleMs;
	private RunRequest req;
	
	public FastTickable(int cycleMs) {
		this.cycleMs = cycleMs;
	}
	
	public boolean isQueued() {
		return req != null && req.cancel == false;
	}
	
	public void queue() {
		if (isQueued()) {
			throw new IllegalStateException("Cannot queue() " + this.getClass().getSimpleName() + " because it is already queued.");
		}
		
		long waited = System.currentTimeMillis() - lastRun;
		long delay = cycleMs - waited;
		if (delay < 0) delay = 0;
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
		if (now < lastRun + cycleMs) {
			throw new RuntimeException("May not run FastTickable, as the Cycle time has not expired. Still have to wait " + (cycleMs + lastRun - now) + "ms.");
		}
		
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