package org.maxgamer.rs.model.action;

import org.maxgamer.rs.events.mob.MobDeathEvent;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class DeathAction extends Action {
	private Animation anim;
	private int ticks = 0;
	
	public DeathAction(Mob mob) {
		super(mob);
		if (getOwner().getCombatStats().getDeathAnimation() >= 0) {
			anim = new Animation(getOwner().getCombatStats().getDeathAnimation());
		}
	}
	
	@Override
	protected boolean run() {
		ticks++;
		
		if (ticks == 1 && anim != null) {
			//First run
			getOwner().getUpdateMask().setAnimation(anim, 50);
		}
		
		//Hide the NPC
		if ((ticks == 4 && anim == null) || (anim != null && ticks == anim.getDuration(true))) {
			MobDeathEvent e = new MobDeathEvent(getOwner());
			e.call();
			getOwner().onDeath();
			getOwner().hide();
		}
		
		//Spawn the NPC in the world again
		if (ticks > getOwner().getRespawnTicks()) {
			getOwner().respawn();
			return true; //Done
		}
		
		return false;
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