package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.action.CombatFollow;
import org.maxgamer.rs.model.entity.mob.Mob;

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
	
	private boolean inRange() throws SuspendExecution{
		int range = attack.getMaxDistance();
		
		if(range == 1 && getTarget().getLocation().isDiagonal(getOwner().getLocation())){
			return true; //Allow diagonal combat
		}
		
		if (getTarget().getLocation().near(getOwner().getLocation(), range) == false) {
			return false; //Couldn't reach the target yet. Continue the CombatFollow.
		}
		return true;
	}

	@Override
	protected void run() throws SuspendExecution {
		long warmup = Core.getServer().getTicks() - getOwner().getDamage().getLastAttack();
		
		while(++warmup < getAttack().getWarmupTicks()){
			wait(1);
			if(getTarget() == null){
				return;
			}
			
			//If we're still warming up, allow the mob to move closer if necessary
			if(inRange() == false){
				assert getOwner().getActions().after(this) instanceof CombatFollow : "AttackAction tried to yield to CombatFollow but instead tried to yield to " + getOwner().getActions().after(this);
				this.yield(); //Assumably, yield to CombatFollow
				wait(1);
				continue;
			}
			
			getOwner().face(getTarget());
		}
		
		//Now we have to wait until we're close enough!
		while(getTarget() != null && inRange() == false){
			assert getOwner().getActions().after(this) instanceof CombatFollow : "AttackAction tried to yield to CombatFollow but instead tried to yield to " + getOwner().getActions().after(this);
			this.yield(); //Assumably, yield to CombatFollow
			wait(1);
		}
		
		Mob target = getTarget();
		if(target != null && getAttack().run(target)){
			getOwner().getDamage().setLastAttack(Core.getServer().getTicks());
			target.getDamage().setLastAttacker(getOwner());
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
