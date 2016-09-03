package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class MobLevelupEvent extends MobEvent {
    private SkillType skill;
    private int oldLevel;

    public MobLevelupEvent(Mob mob, int oldLevel, SkillType skill) {
        super(mob);
        this.skill = skill;
        this.oldLevel = oldLevel;
    }

    public SkillType getSkill() {
        return skill;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return getMob().getSkills().getLevel(skill);
    }
}