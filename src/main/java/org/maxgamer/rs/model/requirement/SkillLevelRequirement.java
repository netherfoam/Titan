package org.maxgamer.rs.model.requirement;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.skill.SkillType;

public class SkillLevelRequirement implements Requirement<Mob> {

	private final SkillType skill;
	private final int levelRequirement;

	public SkillLevelRequirement(SkillType skill, int levelRequirement) {
		this.skill = skill;
		this.levelRequirement = levelRequirement;
	}

	@Override
	public boolean passes(Mob mob) {
		return mob.getSkills().getLevel(skill, true) >= levelRequirement;
	}
	
	@Override
	public int hashCode() {
		return skill.getId() * Math.max(0, levelRequirement);
	}

}
