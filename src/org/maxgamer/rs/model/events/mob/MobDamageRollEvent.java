package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.Attack;

public class MobDamageRollEvent extends MobEvent {

	private final Attack attack;
	private final Mob target;
	private double accuracyModifier;
	private double maxHitModifier;

	public MobDamageRollEvent(Attack attack, Mob attacker, Mob target, double accuracyModifier, double maxHitModifier) {
		super(attacker);
		this.attack = attack;
		this.target = target;
		this.accuracyModifier = accuracyModifier;
		this.maxHitModifier = maxHitModifier;
	}

	public Mob getTarget() {
		return target;
	}

	public double getAccuracyModifier() {
		return accuracyModifier;
	}

	public void addAccuracyModifier(double modifier) {
		this.accuracyModifier += modifier;
	}

	public Attack getAttack() {
		return attack;
	}

	public double getMaxHitModifier() {
		return maxHitModifier;
	}

	public void addMaxHitModifer(double maxHitModifier) {
		this.maxHitModifier += maxHitModifier;
	}

}
