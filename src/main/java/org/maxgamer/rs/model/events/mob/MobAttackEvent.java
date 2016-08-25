package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.Attack;
import org.maxgamer.rs.model.entity.mob.combat.Damage;

/**
 * @author netherfoam
 */
public class MobAttackEvent extends MobEvent implements Cancellable {
	private Damage damage;
	private Attack attack;
	private Mob target;
	private boolean cancel;
	
	public MobAttackEvent(Mob attacker, Attack attack, Damage damage, Mob target) {
		super(attacker);
		this.damage = damage;
		this.target = target;
		this.attack = attack;
	}
	
	public Mob getTarget() {
		return target;
	}
	
	public Damage getDamage() {
		return damage;
	}
	
	public Mob getAttacker() {
		return getMob();
	}
	
	public Attack getAttack() {
		return attack;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
	
	public void setCancelled(boolean c) {
		this.cancel = c;
	}
	
	public void setDamage(Damage damage){
		this.damage = damage;
	}
}