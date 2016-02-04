package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.CombatStats;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.Attack;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.entity.mob.combat.Damage;
import org.maxgamer.rs.model.entity.mob.combat.DamageType;
import org.maxgamer.rs.model.entity.mob.combat.Projectile;

/**
 * @author netherfoam
 */
public class MagicAttack extends Attack {
	
	public static Damage roll(Mob attacker, Mob target, int max) {
		CombatStats srcStats = attacker.getCombatStats();
		CombatStats vicStats = target.getCombatStats();
		
		double accuracy = Erratic.getGaussian(0.5, srcStats.getMagicHitRating());
		double defence = Erratic.getGaussian(0.5, vicStats.getMagicDefenceRating());
		
		if (accuracy > defence) {
			int hit = (int) Erratic.getGaussian(accuracy / (accuracy + defence), max);
			Damage d = new Damage(hit, DamageType.MAGE, target);
			if (hit * 20 > max * 19) {
				//top 5% of hits are 'max' for us
				d.setMax(true);
			}
			return d;
		}
		
		return new Damage(0, DamageType.MISS, target);
	}
	
	private TargetSpell spell;
	
	public MagicAttack(Mob attacker, TargetSpell spell) {
		super(attacker, spell.getAnimation(), spell.getGraphics());
		this.spell = spell;
	}
	
	@Override
	public boolean prepare(Mob target, AttackResult damage) {
		if (this.spell.hasRequirements(attacker) == false) {
			attacker.sendMessage("You don't have enough runes to cast that spell.");
			return false;
		}
		
		return this.spell.prepare(attacker, target, damage);
	}
	
	@Override
	public void perform(final Mob target, final AttackResult data) {
		if (this.spell.getProjectileId() >= 0) {
			Projectile p = Projectile.create(this.spell.getProjectileId(), attacker.getLocation(), target);
			p.launch();
		}
		
		new Tickable() {
			@Override
			public void tick() {
				for (Mob t : data.getTargets()) {
					spell.displayHit(t);
				}
				MagicAttack.super.perform(target, data);
				spell.perform(attacker, target, data);
				
				//If the spell is not a damage one, setLastAttacker will never be called otherwise.
				target.getDamage().setLastAttacker(attacker);
			}
		}.queue(1);
	}
	
	@Override
	public boolean takeConsumables() {
		return this.spell.takeConsumables(attacker);
	}
	
	@Override
	public int getMaxDistance() {
		return spell.getMaxDistance();
	}
	
	@Override
	public int getWarmupTicks() {
		return spell.getCastTime();
	}
}