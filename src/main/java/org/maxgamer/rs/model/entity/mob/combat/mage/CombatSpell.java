package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.entity.mob.combat.Damage;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class CombatSpell extends TargetSpell {
    private int autocastId;
    private int maxHit;

    public CombatSpell(int level, int gfx, int anim, int castTime, int targetGfx, int targetAnim, int projectileId, int range, int maxHit, int autocastId, ItemStack... runes) {
        super(level, gfx, anim, castTime, targetGfx, targetAnim, projectileId, range, runes);
        if (maxHit <= 0) {
            throw new IllegalArgumentException("MaxHit for a combat spell must be > 0. If you don't want to deal damage, use a TargetSpell instead.");
        }

        this.autocastId = autocastId;
        this.maxHit = maxHit;
    }

    public int getMaxHit() {
        return maxHit;
    }

    public int getAutocastId() {
        return autocastId;
    }

    @Override
    public boolean prepare(Mob source, Mob target, AttackResult damages) {
        Damage d = MagicAttack.roll(source, target, maxHit);
        damages.add(d);
        return true;
    }

    @Override
    public void perform(final Mob source, final Mob target, final AttackResult damages) {
        //We don't need to deal damages, that's done by MagicAttack when it calls Super.perform()
    }
}