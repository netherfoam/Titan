package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.CombatStats;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class MeleeAttack extends Attack {
	public static Damage rollMeleeDamage(Mob attacker, Mob target, int atkType) {
		CombatStats srcStats = attacker.getCombatStats();
		CombatStats vicStats = target.getCombatStats();
		
		double accuracy = Erratic.getGaussian(0.5, srcStats.getMeleeHitRating());
		double defence = Erratic.getGaussian(0.5, vicStats.getMeleeDefenceRating(atkType));
		int max = srcStats.getMeleePower();
		
		if (accuracy > defence) {
			int hit = (int) Math.min(target.getHealth(), Erratic.getGaussian(accuracy / (accuracy + defence), max));
			Damage d = new Damage(hit, DamageType.MELEE, target);
			if (hit >= Math.floor(max - (max * 0.05))) {
				// top 5% of hits are 'max' for us
				d.setMax(true);
			}
			return d;
		}
		return new Damage(0, DamageType.MISS, target);
	}
	
	public MeleeAttack(Mob attacker) {
		super(attacker, attacker.getCombatStats().getAttackAnimation(), -1);
	}
	
	@Override
	public boolean prepare(Mob target, AttackResult data) {
		Damage d = MeleeAttack.rollMeleeDamage(attacker, target, getStyle().getBonusType());
		data.add(d);
		
		return true;
	}
	
	@Override
	public boolean takeConsumables() {
		return true;
	}
	
	@Override
	public int getMaxDistance() {
		return 1;
	}
	
	@Override
	public int getWarmupTicks() {
		return 4; // TODO: This varies from weapon to weapon
	}
}