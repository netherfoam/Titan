package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;

import co.paralleluniverse.fibers.SuspendExecution;

public class AttackAction extends Action {
	private Mob target;
	private Attack attack;
	
	public AttackAction(Mob mob, Mob target, Attack attack) {
		super(mob);
		if(attack == null){
			throw new NullPointerException("Attack may not be null");
		}
		this.target = target;
		this.attack = attack;
	}
	
	public AttackAction(Mob mob, Mob target){
		this(mob, target, mob.nextAttack());
	}
	
	public Mob getTarget() {
		if(this.target != null){
			if(this.target.isDead()){
				this.target = null;
			}
			else if(this.target.isAttackable(getOwner()) == false){
				this.target = null;
			}
			else if(this.target.isVisible(getOwner()) == false){
				this.target = null;
			}
			else if(this.target.getLocation().z != getOwner().getLocation().z){
				this.target = null;
			}
			else if(this.target.getLocation().near(getOwner().getLocation(), 25) == false){
				this.target = null;
			}
		}
		return this.target;
	}

	public Attack getAttack() {
		return attack;
	}

	@Override
	protected void run() throws SuspendExecution {
		long warmup = Core.getServer().getTicks() - getOwner().getDamage().getLastAttack();
		
		while(++warmup < getAttack().getWarmupTicks()){
			if(getTarget() == null){
				return;
			}
			
			int range = attack.getMaxDistance();
			if (getTarget().getLocation().near(getOwner().getLocation(), range) == false) {
				this.yield(); //Assumably, yield to CombatFollow
				wait(1);
				continue; //Couldn't reach the target yet. Continue the CombatFollow.
			}
			
			getOwner().setFacing(Facing.face(getTarget()));
			wait(1);
		}
		
		if(getAttack().run(getTarget())){
			getOwner().getDamage().setLastAttack(Core.getServer().getTicks());
			getTarget().getDamage().setLastAttacker(getOwner());
		}
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
	
}
