package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class ObjectInteractAction extends Action {
	protected Animation anim;
	
	public ObjectInteractAction(Mob mob, Animation anim) {
		super(mob);
		if (anim == null) {
			throw new NullPointerException("Null animation given!");
		}
		this.anim = anim;
	}
	
	@Override
	protected void run() throws SuspendExecution {
		/*
		if (end == -1) {
			//First time run() has been invoked
			end = Core.getServer().getTicks() + anim.getDelay();
			getOwner().getUpdateMask().setAnimation(anim, 5);
		}
		
		if (Core.getServer().getTicks() - 1 >= end) {
			return true; //Done
		}
		else {
			return false; //Not done.
		}*/
		
		getOwner().getUpdateMask().setAnimation(anim, 5);
		wait(anim.getDelay());
	}
	
	@Override
	protected void onCancel() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean isCancellable() {
		// TODO Auto-generated method stub
		return false;
	}
	
}