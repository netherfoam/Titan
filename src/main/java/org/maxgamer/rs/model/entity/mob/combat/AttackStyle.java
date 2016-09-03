package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.model.entity.mob.Bonuses;
import org.maxgamer.rs.model.skill.SkillType;

import java.util.Arrays;

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

    /**
     * Fetches the attack style for the given type of weapon and the given slot.
     *
     * @param type the weapon type, varying from 1 to 27.
     * @param slot the slot to lookup, varying from 1 to 4
     * @return the attack style or null if the requested slot is invalid
     */
    public static AttackStyle getStyle(int type, int slot) {
        switch (type) {
            case 1:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Bash", Bonuses.ATK_CRUSH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Pound", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Focus", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
                break;
            case 23:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Chop", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Hack", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Smash", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 4:
                        return new AttackStyle(slot, "Slash", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 2:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Chop", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Hack", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Smash", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 3:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Bash", Bonuses.ATK_CRUSH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Pound", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
                break;
            case 4:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Spike", Bonuses.ATK_STAB, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Impale", Bonuses.ATK_STAB, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Smash", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_STAB, SkillType.DEFENCE);
                }
                break;
            case 5:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Stab", Bonuses.ATK_STAB, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Lunge", Bonuses.ATK_STAB, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Slash", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_STAB, SkillType.DEFENCE);
                }
                break;
            case 6:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Chop", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Slash", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Lunge", Bonuses.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 7:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Chop", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Slash", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Smash", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 8:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Pound", Bonuses.ATK_CRUSH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Pummel", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Spike", Bonuses.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
                break;
            case 9:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Chop", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Slash", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Lunge", Bonuses.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 10:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Pound", Bonuses.ATK_CRUSH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Pummel", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
                break;
            case 11:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Flick", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Lash", Bonuses.ATK_SLASH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 3:
                        return new AttackStyle(slot, "Deflect", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 12:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Pound", Bonuses.ATK_CRUSH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Pummel", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
                break;
            case 13:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Accurate", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Rapid", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Long range", Bonuses.ATK_RANGE, SkillType.RANGE);
                }
                break;
            case 14:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Lunge", Bonuses.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 2:
                        return new AttackStyle(slot, "Swipe", Bonuses.ATK_SLASH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 3:
                        return new AttackStyle(slot, "Pound", Bonuses.ATK_CRUSH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_STAB, SkillType.DEFENCE);
                }
                break;
            case 15:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Jab", Bonuses.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 2:
                        return new AttackStyle(slot, "Swipe", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Fend", Bonuses.ATK_STAB, SkillType.DEFENCE);
                }
                break;
            case 16:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Accurate", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Rapid", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Long range", Bonuses.ATK_RANGE, SkillType.RANGE);
                }
                break;
            case 17:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Accurate", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Rapid", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Long range", Bonuses.ATK_RANGE, SkillType.RANGE);
                }
                break;
            case 18:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Accurate", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Rapid", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Long range", Bonuses.ATK_RANGE, SkillType.RANGE);
                }
                break;
            case 19:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Short fuse", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Medium fuse", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Long fuse", Bonuses.ATK_RANGE, SkillType.RANGE);
                }
                break;
            case 20:
                //What the heck is this? Aim and fire option AND kick option?
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Aim and fire", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Kick", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                }
                break;
            case 21:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Scorch", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 2:
                        return new AttackStyle(slot, "Flare", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Blaze", Bonuses.ATK_MAGIC, SkillType.MAGIC);
                }
                break;
            case 22:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Reap", Bonuses.ATK_SLASH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Chop", Bonuses.ATK_STAB, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Jab", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 4:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_SLASH, SkillType.DEFENCE);
                }
                break;
            case 24:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Sling", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 2:
                        return new AttackStyle(slot, "Chuck", Bonuses.ATK_RANGE, SkillType.RANGE);
                    case 3:
                        return new AttackStyle(slot, "Lob", Bonuses.ATK_RANGE, SkillType.RANGE);
                }
                break;
            case 25:
            case 26:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Jab", Bonuses.ATK_STAB, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Swipe", Bonuses.ATK_SLASH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Fend", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
                break;
            case 27:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Hack!", Bonuses.ATK_SLASH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 2:
                        return new AttackStyle(slot, "Gouge!", Bonuses.ATK_STAB, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                    case 3:
                        return new AttackStyle(slot, "Smash!", Bonuses.ATK_CRUSH, SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE);
                }
                break;
            default:
                switch (slot) {
                    case 1:
                        return new AttackStyle(slot, "Punch", Bonuses.ATK_CRUSH, SkillType.ATTACK);
                    case 2:
                        return new AttackStyle(slot, "Kick", Bonuses.ATK_CRUSH, SkillType.STRENGTH);
                    case 3:
                        return new AttackStyle(slot, "Block", Bonuses.ATK_CRUSH, SkillType.DEFENCE);
                }
        }
        if (slot != 1) return getStyle(type, 1); //Default to slot '1'.
        return null;
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
}