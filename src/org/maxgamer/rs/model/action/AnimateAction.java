package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class AnimateAction extends Action {
	private boolean cancellable;
	private Animation anim;
	private int remaining;
	boolean first = true;
	
	public AnimateAction(Mob mob, int anim, boolean cancellable) {
		super(mob);
		this.anim = new Animation(anim);
		this.cancellable = cancellable;
		this.remaining = this.anim.getDuration(false);
	}
	
	@Override
	protected boolean run() {
		if (first) {
			mob.getUpdateMask().setAnimation(anim, 20);
			first = false;
		}
		
		this.remaining--;
		return this.remaining <= 0;
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return cancellable;
	}
}