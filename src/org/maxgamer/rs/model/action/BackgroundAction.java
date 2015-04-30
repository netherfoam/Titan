package org.maxgamer.rs.model.action;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public abstract class BackgroundAction extends Action {
	private boolean cancelRequested;
	private int state = 0;
	
	public BackgroundAction(Mob mob) {
		super(mob);
	}
	
	@Override
	protected final boolean run() {
		if (state == 0) {
			state = 1;
			Core.submit(new Runnable() {
				@Override
				public void run() {
					calculate();
					
					if (isCancelRequested() == false) {
						state = 2;
					}
				}
			}, true);
			return false;
		}
		if (state == 1) {
			return false; //Calculating
		}
		if (state == 2) {
			return actuate();
		}
		throw new IllegalStateException();
	}
	
	public abstract void calculate();
	
	public abstract boolean actuate();
	
	@Override
	protected void onCancel() {
		cancelRequested = true;
	}
	
	public boolean isCancelRequested() {
		return cancelRequested;
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}