package org.maxgamer.rs.model.skill;

import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;

/**
 * Represents a player's skill.
 *
 * @author netherfoam
 */
public class Skill implements YMLSerializable {
    private SkillType type;
    private int level;
    private double exp;
    private double modifier;

    private double targetExp = -1;
    private int targetLevel = -1;

    private boolean levelChanged;

    /**
     * Represents a player's skill
     *
     * @param type the skill type
     * @param exp  the skill's experience
     */
    public Skill(SkillType type, double exp) {
        this.type = type;
        this.modifier = 0;
        this.exp = exp;
        this.level = type.getLevel(exp);
    }

    /**
     * The level of this skill
     *
     * @return The level of this skill
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level for this skill, by retrieving the minimum experience
     * requirement for the level and setting the level.
     *
     * @param level the new level to set.
     */
    public void setLevel(int level) {
        setExp(SkillType.getExpRequired(level));
    }

    public double getExp() {
        return exp;
    }

    /**
     * Sets the experience of this skill.
     *
     * @param exp Sets the experience of this skill.
     */
    public void setExp(double exp) {
        if (exp > SkillType.MAX_EXP) {
            exp = SkillType.MAX_EXP;
        } else if (exp < 0) {
            exp = 0;
        }

        this.exp = exp;
        this.level = type.getLevel(exp);
    }

    public void addExp(double d) {
        this.setExp(this.exp + d);
    }

    public double getModifier() {
        return this.modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    public SkillType getType() {
        return this.type;
    }

    @Override
    public ConfigSection serialize() {
        MutableConfig map = new MutableConfig();

        map.set("modifier", getModifier());
        map.set("exp", getExp());
        map.set("target_exp", getTargetExp());
        map.set("target_lvl", getTargetLevel());

        return map;
    }

    @Override
    public void deserialize(ConfigSection map) {
        this.exp = map.getDouble("exp", 0);
        this.modifier = map.getInt("modifier", 0);
        this.level = type.getLevel(exp);
        this.targetExp = map.getDouble("target_exp");
        this.targetLevel = map.getInt("target_lvl");
    }

    public double getTargetExp() {
        return targetExp;
    }

    public void setTargetExp(double targetExp) {
        this.targetExp = targetExp;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(int targetLevel) {
        this.targetLevel = targetLevel;
    }

    public boolean isTargeting() {
        return targetExp > 0 || targetLevel > 0;
    }

    public boolean hasLevelChanged() {
        return levelChanged;
    }

    public void setLevelChanged(boolean levelChanged) {
        this.levelChanged = levelChanged;
    }

}