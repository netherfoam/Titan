package org.maxgamer.rs.model.entity.mob.combat;

import java.util.Arrays;

import org.maxgamer.rs.model.entity.mob.Bonus;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class AttackStyle {
	private int slot;
	private String name;
	private SkillType[] skills;
	private int bonusType;
	
	public AttackStyle(int slot, String name, int bonus, SkillType... skill) {
		if (slot != -1 && (slot < 1 || slot > 4)) {
			throw new IllegalArgumentException("Slot must be between 1 and 4 inclusive or -1, given " + slot);
		}
		
		if (name == null) {
			throw new IllegalArgumentException("Name may not be null");
		}
		
		if (bonus < 0) {
			throw new IllegalArgumentException("Bonus must be >= 0");
		}
		
		this.slot = slot;
		this.skills = skill;
		this.name = name;
		this.bonusType = bonus;
	}
	
	@Override
	public String toString() {
		return slot + " '" + name + "', Bonus " + bonusType + " " + Arrays.toString(skills);
	}
	
	public String getName() {
		return name;
	}
	
	public SkillType[] getSkills() {
		return skills;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public int getBonusType() {
		return bonusType;
	}
	
	public boolean isType(SkillType type) {
		for (SkillType skill : this.skills) {
			if (skill == type) return true;
		}
		return false;
	}
	
	/**
	 * Fetches the attack style for the given type of weapon and the given slot.
	 * @param type the weapon type, varying from 1 to 27.
	 * @param slot the slot to lookup, varying from 1 to 4
	 * @return the attack style or null if the requested slot is invalid
	 */
	public static AttackStyle getStyle(int type, int slot) {
		switch (type) {
			case 1:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Bash", Bonus.ATK_CRUSH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Pound", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Focus", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
				break;
			case 23:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Chop", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Hack", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Smash", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 4:
						return new AttackStyle(slot, "Slash", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 2:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Chop", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Hack", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Smash", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 3:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Bash", Bonus.ATK_CRUSH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Pound", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Block", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
				break;
			case 4:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Spike", Bonus.ATK_STAB, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Impale", Bonus.ATK_STAB, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Smash", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_STAB, SkillType.DEFENCE);
				}
				break;
			case 5:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Stab", Bonus.ATK_STAB, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Lunge", Bonus.ATK_STAB, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Slash", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_STAB, SkillType.DEFENCE);
				}
				break;
			case 6:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Chop", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Slash", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Lunge", Bonus.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 7:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Chop", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Slash", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Smash", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 8:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Pound", Bonus.ATK_CRUSH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Pummel", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Spike", Bonus.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
				break;
			case 9:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Chop", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Slash", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Lunge", Bonus.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 10:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Pound", Bonus.ATK_CRUSH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Pummel", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Block", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
				break;
			case 11:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Flick", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Lash", Bonus.ATK_SLASH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 3:
						return new AttackStyle(slot, "Deflect", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 12:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Pound", Bonus.ATK_CRUSH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Pummel", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Block", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
				break;
			case 13:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Accurate", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Rapid", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Long range", Bonus.ATK_RANGE, SkillType.RANGE);
				}
				break;
			case 14:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Lunge", Bonus.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 2:
						return new AttackStyle(slot, "Swipe", Bonus.ATK_SLASH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 3:
						return new AttackStyle(slot, "Pound", Bonus.ATK_CRUSH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_STAB, SkillType.DEFENCE);
				}
				break;
			case 15:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Jab", Bonus.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 2:
						return new AttackStyle(slot, "Swipe", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Fend", Bonus.ATK_STAB, SkillType.DEFENCE);
				}
				break;
			case 16:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Accurate", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Rapid", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Long range", Bonus.ATK_RANGE, SkillType.RANGE);
				}
				break;
			case 17:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Accurate", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Rapid", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Long range", Bonus.ATK_RANGE, SkillType.RANGE);
				}
				break;
			case 18:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Accurate", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Rapid", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Long range", Bonus.ATK_RANGE, SkillType.RANGE);
				}
				break;
			case 19:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Short fuse", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Medium fuse", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Long fuse", Bonus.ATK_RANGE, SkillType.RANGE);
				}
				break;
			case 20:
				//What the heck is this? Aim and fire option AND kick option?
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Aim and fire", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Kick", Bonus.ATK_CRUSH, SkillType.STRENGTH);
				}
				break;
			case 21:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Scorch", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 2:
						return new AttackStyle(slot, "Flare", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Blaze", Bonus.ATK_MAGIC, SkillType.MAGIC);
				}
				break;
			case 22:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Reap", Bonus.ATK_SLASH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Chop", Bonus.ATK_STAB, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Jab", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 4:
						return new AttackStyle(slot, "Block", Bonus.ATK_SLASH, SkillType.DEFENCE);
				}
				break;
			case 24:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Sling", Bonus.ATK_RANGE, SkillType.RANGE);
					case 2:
						return new AttackStyle(slot, "Chuck", Bonus.ATK_RANGE, SkillType.RANGE);
					case 3:
						return new AttackStyle(slot, "Lob", Bonus.ATK_RANGE, SkillType.RANGE);
				}
				break;
			case 25:
			case 26:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Jab", Bonus.ATK_STAB, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Swipe", Bonus.ATK_SLASH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Fend", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
				break;
			case 27:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Hack!", Bonus.ATK_SLASH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 2:
						return new AttackStyle(slot, "Gouge!", Bonus.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
					case 3:
						return new AttackStyle(slot, "Smash!", Bonus.ATK_CRUSH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
				}
				break;
			default:
				switch (slot) {
					case 1:
						return new AttackStyle(slot, "Punch", Bonus.ATK_CRUSH, SkillType.ATTACK);
					case 2:
						return new AttackStyle(slot, "Kick", Bonus.ATK_CRUSH, SkillType.STRENGTH);
					case 3:
						return new AttackStyle(slot, "Block", Bonus.ATK_CRUSH, SkillType.DEFENCE);
				}
		}
		if (slot != 1) return getStyle(type, 1); //Default to slot '1'.
		return null;
	}
}