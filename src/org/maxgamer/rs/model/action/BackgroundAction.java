package org.maxgamer.rs.model.action;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

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
	protected final void run() throws SuspendExecution {
		Core.submit(new Runnable() {
			@Override
			public void run() {
				calculate();
				
				if (isCancelRequested() == false) {
					state = 2;
				}
			}
		}, true);
		
		while(state == 1){
			wait(1);
		}
		
		while(!actuate()){
			wait(1);
		}
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