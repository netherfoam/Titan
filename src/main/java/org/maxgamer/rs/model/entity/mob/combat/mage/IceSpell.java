package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.entity.mob.combat.Damage;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class IceSpell extends AncientCombatSpell {
    private int duration;

    public IceSpell(int level, int gfx, int anim, int castTime, int targetGfx, int targetAnim, int projectileId, int range, int maxHit, int autocastId, boolean multi, int duration, ItemStack... runes) {
        super(level, gfx, anim, castTime, targetGfx, targetAnim, projectileId, range, maxHit, autocastId, multi, runes);
        this.duration = duration;
    }

    @Override
    public void perform(Mob src, Mob target, AttackResult damages) {
        super.perform(src, target, damages);

        for (Damage d : damages) {
            d.getTarget().root(duration, 10, false);
        }
    }
}