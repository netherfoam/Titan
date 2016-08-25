package org.maxgamer.rs.core.server;

import org.maxgamer.rs.core.Core;

/**
 * @author netherfoam
 */
public class AutoSave implements Runnable {
	public int interval = 60000; //Interval in MS
	
	public AutoSave(int interval) {
		this.interval = interval;
	}
	
	@Override
	public void run() {
		Core.getServer().save();
		
		Core.submit(this, interval, true);
	}
}