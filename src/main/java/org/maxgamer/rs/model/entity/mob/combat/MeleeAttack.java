package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.model.entity.mob.CombatStats;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobDamageRollEvent;
import org.maxgamer.rs.util.Erratic;

/**
 * @author netherfoam
 */
public class MeleeAttack extends Attack {
    public MeleeAttack(Mob attacker) {
        super(attacker, attacker.getCombatStats().getAttackAnimation(), -1);
    }

    public static Damage roll(Mob attacker, Mob target) {
        return roll(attacker, target, 1.0, 1.0);
    }

    public static Damage roll(Mob attacker, Mob target, double accuracyModifier, double maxHitModifier) {
        int atkType = attacker.getAttackStyle().getBonusType();

        CombatStats srcStats = attacker.getCombatStats();
        CombatStats vicStats = target.getCombatStats();

        double accuracy = Erratic.getGaussian(0.5, srcStats.getMeleeHitRating()) * accuracyModifier;
        double defence = Erratic.getGaussian(0.5, vicStats.getMeleeDefenceRating(atkType));
        int max = (int) (srcStats.getMeleePower() * maxHitModifier);

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

    @Override
    public boolean prepare(Mob target, AttackResult data) {
        MobDamageRollEvent event = new MobDamageRollEvent(this, attacker, target, 1.0, 1.0);
        event.call();
        Damage d = MeleeAttack.roll(attacker, target, event.getAccuracyModifier(), event.getMaxHitModifier());
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