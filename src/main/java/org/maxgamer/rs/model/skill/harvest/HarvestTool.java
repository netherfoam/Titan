package org.maxgamer.rs.model.skill.harvest;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class HarvestTool {
    private ItemStack item;
    private SkillType skill;
    private int level;
    private Animation animation;
    private double efficiency;

    public HarvestTool(ItemStack item, SkillType skill, int level, int animation, double efficiency) {
        this.item = item;

        if (efficiency <= 0) {
            throw new IllegalArgumentException("HarvestTool efficiency must be > 0");
        }

        if (skill == null && level > 0) {
            throw new IllegalArgumentException("May not have a HarvestTool with no SkillType and a level requirement > 0");
        }

        if (skill != null && level <= 0) {
            throw new IllegalArgumentException("May not have a HarvestTool with a SkillType and level requirement <= 0");
        }

        this.skill = skill;
        this.level = level;
        if (animation >= 0) this.animation = new Animation(animation);
        this.efficiency = efficiency;
    }

    public HarvestTool(ItemStack item, int animation, double efficiency) {
        this(item, null, 0, animation, 1);
    }

    public HarvestTool(SkillType skill, int level, int animation) {
        this(null, skill, level, animation, 1);
    }

    public boolean has(Persona p) {
        if (p == null) throw new NullPointerException();

        if (item != null)
            if (!p.getInventory().contains(item) && !p.getEquipment().contains(item)) return false;

        if (skill != null)
            if (p.getSkills().getLevel(skill, true) < level) return false;
        return true;
    }

    public boolean hasRequirements(Persona p) {
        return has(p) && p.getSkills().getLevel(skill, true) >= level;
    }

    /**
     * The efficiency of this tool. Consider 1.0 to be the base efficiency, and
     * increasing this to 2.0 would halve the time it takes to harvest the
     * object. Halving it to 0.5 would double the time taken to harvest. Value
     * is > 0
     *
     * @return the efficiency of this tool
     */
    public double getEfficiency() {
        return efficiency;
    }

    /**
     * The animation to use when using this tool, or null
     *
     * @return The animation to use when using this tool
     */
    public Animation getAnimation() {
        return animation;
    }

    public int getLevel() {
        return level;
    }

    public ItemStack getItem() {
        return item;
    }
}