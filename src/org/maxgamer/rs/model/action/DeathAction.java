package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobDeathEvent;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class DeathAction extends Action {
	private Animation anim;
	
	public DeathAction(Mob mob) {
		super(mob);
		if (getOwner().getCombatStats().getDeathAnimation() >= 0) {
			anim = new Animation(getOwner().getCombatStats().getDeathAnimation());
		}
	}
	
	@Override
	protected void run() throws SuspendExecution {
		getOwner().animate(anim, 50);
		
		wait(anim == null ? 3 : anim.getDuration(true));
		
		MobDeathEvent e = new MobDeathEvent(getOwner());
		e.call();
		getOwner().onDeath();
		getOwner().hide();
		
		wait(getOwner().getRespawnTicks());
		getOwner().respawn();
	}
	
	@Override
	protected void onCancel() {
		//Difficult task to cancel, really.
		getOwner().setHealth(1);
		getOwner().getUpdateMask().setAnimation(new Animation(-1), 50);
	}
	
	@Override
	protected boolean isCancellable() {
		return false; //We really don't want this one cancelled.
	}
}