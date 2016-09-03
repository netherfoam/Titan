package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class BuffSpell extends TargetSpell {
    private SkillType skill;
    private double multiplier;

    /**
     * @param level
     * @param gfx
     * @param anim
     * @param castTime
     * @param targetGfx
     * @param targetAnim
     * @param projectileId
     * @param range
     * @param multiplier   the multiplier, eg 0.95 will reduce the skill by 5% and
     *                     1.15 will increase it by 15%
     * @param skill        the skill to modify
     * @param runes
     */
    public BuffSpell(int level, int gfx, int anim, int castTime, int targetGfx, int targetAnim, int projectileId, int range, double multiplier, SkillType skill, ItemStack... runes) {
        super(level, gfx, anim, castTime, targetGfx, targetAnim, projectileId, range, runes);
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier is a modifier, thus " + multiplier + " is invalid. Values should be >= 0");
        }

        if (skill == null) {
            throw new NullPointerException("SkillType may not be null");
        }

        this.multiplier = multiplier;
        this.skill = skill;
    }

    @Override
    public boolean prepare(Mob source, Mob target, AttackResult damages) {
        return true;
    }

    @Override
    public void perform(Mob source, Mob target, AttackResult damages) {
        target.getSkills().buff(skill, multiplier);
    }
}