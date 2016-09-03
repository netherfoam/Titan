package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Graphics;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public abstract class TargetSpell extends Spell {
    private int projectileId;
    private int range;
    private Graphics targetGfx;
    private Animation targetAnim;

    public TargetSpell(int level, int gfx, int anim, int castTime, int targetGfx, int targetAnim, int projectileId, int range, ItemStack... runes) {
        super(level, gfx, anim, castTime, runes);

        if (range < 1) {
            throw new IllegalArgumentException("Range of TargetSpell must be >= 1");
        }

        if (targetGfx >= 0) this.targetGfx = new Graphics(targetGfx);
        if (targetAnim >= 0) this.targetAnim = new Animation(targetAnim);
        this.projectileId = projectileId;
        this.range = range;
    }

    public int getProjectileId() {
        return projectileId;
    }

    public int getMaxDistance() {
        return range;
    }

    public Graphics getTargetGraphics() {
        return targetGfx;
    }

    public Animation getTargetAnimation() {
        return targetAnim;
    }

    /**
     * Animates the given mob and makes them perform the graphics of this Spell,
     * as if they were casting it.
     *
     * @param mob the mob who is to enact casting the spell
     */
    public void displayHit(Mob mob) {
        if (targetGfx != null) mob.getUpdateMask().setGraphics(targetGfx);
        if (targetAnim != null) mob.getUpdateMask().setAnimation(targetAnim, 25);
    }

    public abstract boolean prepare(Mob source, Mob target, AttackResult damages);

    public abstract void perform(Mob source, Mob target, AttackResult damages);
}