package org.maxgamer.rs.model.action;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class ObjectInteractAction extends Action {
	private int end = -1;
	private Animation anim;
	
	public ObjectInteractAction(Mob mob, Animation anim) {
		super(mob);
		if (anim == null) {
			throw new NullPointerException("Null animation given!");
		}
		this.anim = anim;
	}
	
	@Override
	protected boolean run() {
		if (end == -1) {
			//First time run() has been invoked
			end = Core.getServer().getTicker().getTicks() + anim.getDelay();
			getOwner().getUpdateMask().setAnimation(anim, 5);
		}
		
		if (Core.getServer().getTicker().getTicks() - 1 >= end) {
			return true; //Done
		}
		else {
			return false; //Not done.
		}
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