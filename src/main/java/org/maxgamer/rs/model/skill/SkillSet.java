package org.maxgamer.rs.model.skill;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.MobLevelupEvent;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;
import org.maxgamer.rs.util.Calc;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author netherfoam
 */
public class SkillSet implements YMLSerializable {
    /**
     * The map of skill type to skill. Each SkillType is an identifier (Eg
     * woodcutting or smithing) and the Skill is an instance of Skill, which
     * holds the current exp and modifier.
     */
    protected final HashMap<SkillType, Skill> skills = new HashMap<>();
    /**
     * The owner of this skillset
     */
    private Mob owner;
    /**
     * Tick task levels stats towards their normal value over time. One
     * invocation per 30 seconds, or 50 ticks.
     */
    private Tickable normalizer;

    private boolean initializing = false;

    /**
     * Constructs a new SkillSet which only has attack, strength, defence, magic
     * and range levels for the given NPC. They default to 0.
     *
     * @param n the NPC
     */
    public SkillSet(NPC n) {
        this.owner = n;
        skills.put(SkillType.ATTACK, new Skill(SkillType.ATTACK, 0));
        skills.put(SkillType.STRENGTH, new Skill(SkillType.STRENGTH, 0));
        skills.put(SkillType.DEFENCE, new Skill(SkillType.DEFENCE, 0));

        skills.put(SkillType.MAGIC, new Skill(SkillType.MAGIC, 0));
        skills.put(SkillType.RANGE, new Skill(SkillType.RANGE, 0));
    }

    /**
     * Constructs a new SkillSet with 0 for all levels.
     */
    public SkillSet(Persona p) {
        this.owner = p;
        for (SkillType t : SkillType.values()) {
            skills.put(t, new Skill(t, 0));
        }
    }

    /**
     * Constructs a new SkillSet with no skills added.
     *
     * @param m the owner
     */
    protected SkillSet(Mob m) {
        this.owner = m;
    }

    /**
     * Sets the exp for the given skill to the given amount. This updates the
     * owner's combat level
     *
     * @param t   the skill to set
     * @param exp the exp to set
     * @throws NullPointerException if skilltype is null or if exp is null
     */
    public void setExp(SkillType t, double exp) {
        if (t == null) throw new NullPointerException();
        if (exp < 0) throw new IllegalArgumentException("Exp must be >= 0");

        if (!skills.containsKey(t)) {
            skills.put(t, new Skill(t, 0));
        }

        Skill skill = skills.get(t);
        int previousLevel = skill.getLevel();
        skill.setExp(exp);

        // If the owner is a player, then we must send them the update packet.
        if (owner instanceof Client) {
            Client c = (Client) owner;
            RSOutgoingPacket out = new RSOutgoingPacket(8);
            out.writeInt1((int) exp);
            out.writeByteS(t.getId());
            out.writeByteS((byte) getLevel(t) + (int) getModifier(t));
            c.write(out);
        }

        if (skill.getLevel() != previousLevel) {

            // We may need to recalculate our combat level based on the new skill
            if (t == SkillType.ATTACK || t == SkillType.STRENGTH || t == SkillType.DEFENCE || t == SkillType.CONSTITUTION || t == SkillType.RANGE || t == SkillType.MAGIC || t == SkillType.PRAYER || t == SkillType.SUMMONING) {
                int combat = 3;
                combat = (int) ((getLevel(SkillType.DEFENCE) + getLevel(SkillType.CONSTITUTION) + Math.floor(getLevel(SkillType.PRAYER) / 2)) * 0.25) + 1;
                double melee = (getLevel(SkillType.ATTACK) + getLevel(SkillType.STRENGTH)) * 0.325;
                double range = Math.floor(getLevel(SkillType.RANGE) * 1.5) * 0.325;
                double magic = Math.floor(getLevel(SkillType.MAGIC) * 1.5) * 0.325;
                if (melee >= range && melee >= magic) {
                    combat += melee;
                } else if (range >= melee && range >= magic) {
                    combat += range;
                } else if (magic >= melee && magic >= range) {
                    combat += magic;
                }
                combat += getLevel(SkillType.SUMMONING) / 8;

                owner.getModel().setCombatLevel(combat);
            }

            if (!initializing) {
                MobLevelupEvent e = new MobLevelupEvent(owner, previousLevel, t);
                e.call();
            }
        }
    }

    /**
     * Returns the amount of exp that the mob has in the given skill.
     *
     * @param t the skill level
     * @return the amount of exp in the level
     */
    public double getExp(SkillType t) {
        if (!skills.containsKey(t)) {
            return 0;
        }

        return skills.get(t).getExp();
    }

    /**
     * Adds the given amount of experience to the given skill type
     *
     * @param t   the {@link SkillType}
     * @param exp the amount of experience to add
     */
    public void addExp(SkillType t, double exp) {
        if (t == null) throw new NullPointerException();

        if (!skills.containsKey(t)) {
            skills.put(t, new Skill(t, 0));
        }
        double old = skills.get(t).getExp();
        setExp(t, old + exp);
    }

    /**
     * Sets the owning mobs experience for the given skill type to the minimum
     * amount that is required to reach the desired level
     *
     * @param t     the {@link SkillType} to change the level of
     * @param level the level for the new skill, must be >= 0.
     * @throws NullPointerException     if the {@link SkillType} is null
     * @throws IllegalArgumentException if the level is < 0
     */
    public void setLevel(SkillType t, int level) {
        if (t == null) throw new NullPointerException("SkillType may not be null");
        if (level < 0) throw new IllegalArgumentException("Level must be >= 0");

        setExp(t, SkillType.getExpRequired(level));
    }

    /**
     * Returns the level of the given skill type
     *
     * @param t the {@link SkillType} to get the level of
     * @return the level of the skill, this is always positive.
     */
    public int getLevel(SkillType t) {
        if (t == null) throw new NullPointerException("SkillType may not be null");
        if (!skills.containsKey(t)) {
            return 0;
        }

        return skills.get(t).getLevel();
    }

    /**
     * Returns the current level for the given skill. If modifier is true, then
     * this will add whatever modifier is present. Else, it will simply return
     * getCraftLevel(t)
     *
     * @param t        the skill type
     * @param modifier true if you want to include modifiers, false otherwise
     * @return the level, plus modifiers if requested
     */
    public int getLevel(SkillType t, boolean modifier) {
        if (!skills.containsKey(t)) {
            return 0;
        }

        if (modifier) {
            return getLevel(t) + (int) getModifier(t);
        } else {
            return getLevel(t);
        }
    }

    /**
     * Sets the modifier for the given skill type. If this would reduce the
     * owners temporary level below 0, this reduces the modifier in such a way
     * that the new temporary level will become at minimum, 0.
     *
     * @param t        the skill to set
     * @param modifier the modifier to set
     * @throws NullPointerException if the skill is null
     */
    public void setModifier(SkillType t, double modifier) {
        if (t == null) throw new NullPointerException("SkillType may not be null");
        if (getLevel(t, false) + modifier < 0) modifier = -getLevel(t, false);

        if (!skills.containsKey(t)) {
            skills.put(t, new Skill(t, 0));
        }
        skills.get(t).setModifier(modifier);

        if (owner instanceof Client) {
            Client c = (Client) owner;
            RSOutgoingPacket out = new RSOutgoingPacket(8);
            out.writeInt1((int) getExp(t));
            out.writeByteS(t.getId());
            out.writeByteS((byte) getLevel(t) + (int) getModifier(t));
            c.write(out);
        }
        if (modifier != 0) {
            this.normalize();
        }
    }

    /**
     * Returns the modifier for the given {@link SkillType}
     *
     * @param t the {@link SkillType} to get the modifier of
     * @return the level of the skill type
     * @throws NullPointerException if the given skill type is null
     */
    public double getModifier(SkillType t) {
        if (t == null) throw new NullPointerException("SkillType may not be null");
        if (!skills.containsKey(t)) {
            return 0;
        }
        return skills.get(t).getModifier();
    }

    /**
     * Buffs the given skill with the given multiplier. The result of this call
     * does not simply imply that the given skill's modifier will be set to the
     * current level * multiplier. This method will set the given skill's
     * modifier, by adding (multiplier - 1) * real_level to the modifier. This
     * also guarantees that the modifier adjustment will not vary above or below
     * the given multiplier's bounds. For example, this emulates drinking an
     * attack potion, or having Confuse cast on them. If this were done in that
     * order, the new modifier would be positive, not negative, despite Confuse
     * having been cast most recently.
     *
     * @param t          The skill to modify/buff
     * @param multiplier the multiplier, where 1 = normal, 1.1 = 10% boost, 0.9
     *                   = 10% decrease.
     * @return the number of levels gained (may be negative for loss)
     */
    public int buff(SkillType t, double multiplier) {
        if (t == null) {
            throw new NullPointerException("SkillType may not be null");
        }

        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }

        if (!skills.containsKey(t)) {
            skills.put(t, new Skill(t, 0));
        }

        double curMod = getModifier(t);
        double realLev = getLevel(t, false);

        // The number of levels the multiplier would add/remove
        double delta = (multiplier - 1) * realLev;

        if (delta > 0) {
            if (curMod + delta > delta) {
                curMod = Math.max(curMod, delta);
            } else {
                curMod = curMod + delta;
            }
        } else if (delta < 0) {
            if (curMod + delta < delta) {
                curMod = delta;
            } else {
                curMod = Math.min(curMod, delta);
            }
        }

        setModifier(t, curMod);
        return (int) delta;
    }

    /**
     * Restores the given {@link SkillType} by the given amount. This will not
     * grant a boost to the skill.
     *
     * @param type   the skill type to boost
     * @param levels the number of levels to restore
     * @throws IllegalArgumentException if the given levels to restore is < 0
     */
    public void restore(SkillType type, double levels) {
        if (type == null) throw new NullPointerException("SkillType may not be null");
        if (levels < 0) throw new IllegalArgumentException("Levels to restore must be >= 0");

        double modifier = getModifier(type);
        levels = Math.min(0, levels + modifier);
        this.setModifier(type, levels);
    }

    /**
     * Serializes this SkillSet to a ConfigSection
     *
     * @return the serialized version of this {@link ConfigSection}
     */
    @Override
    public ConfigSection serialize() {
        MutableConfig map = new MutableConfig();
        for (Entry<SkillType, Skill> e : this.skills.entrySet()) {
            map.set(e.getKey().toString(), e.getValue().serialize());
        }

        return map;
    }

    /**
     * Deserializes this {@link Skill} from the given {@link ConfigSection}
     *
     * @param map the {@link ConfigSection} for the skill
     */
    @Override
    public void deserialize(ConfigSection map) {
        initializing = true;
        try {
            for (SkillType t : SkillType.values()) {
                Skill s = new Skill(t, 0);
                ConfigSection data = map.getSection(t.toString(), null);
                if (data != null) {
                    s.deserialize(data);
                } else {
                    // Constitution defaults to lvl 10 instead of 1
                    if (t == SkillType.CONSTITUTION) {
                        s.setLevel(10);
                    } else {
                        s.setLevel(1);
                    }
                }

                // Assigns the type to the level and sends the update clients
                setExp(t, s.getExp());
                setModifier(t, s.getModifier());
            }
        } finally {
            initializing = false;
        }
        this.refreshTargetValues();
    }

    private void normalize() {
        if (this.normalizer == null || !this.normalizer.isQueued()) {
            this.normalizer = new Tickable() {
                @Override
                public void tick() {
                    for (Skill s : skills.values()) {
                        if (s.getModifier() == 0) continue;
                        if (s.getType() == SkillType.PRAYER) continue;
                        double mod = s.getModifier();

                        if (s.getModifier() > 0) {
                            setModifier(s.getType(), Calc.maxd(mod - 1, 0));
                        } else {
                            setModifier(s.getType(), Calc.mind(mod + 1, 0));
                        }
                    }
                    if (!isNormalized()) {
                        this.queue(50);
                    } else {
                        normalizer = null; // We are done, indicate so
                    }
                }
            };
            this.normalizer.queue(50);
        }
    }

    private boolean isNormalized() {
        for (Skill s : this.skills.values()) {
            if (s.getType() == SkillType.PRAYER) continue;
            if (s.getModifier() != 0) {
                return false;
            }
        }
        return true;
    }

    public void targetSkillLevel(SkillType type, int level) {
        Skill skill = skills.get(type);
        if (skill.getLevel() >= level || level > 120) {
            owner.sendMessage("You cannot set that level target.");
            return;
        }
        skill.setTargetLevel(level);
        skill.setTargetExp(-1);
        refreshTargetValues();
    }

    public void targetSkillExp(SkillType type, double experience) {
        Skill skill = skills.get(type);
        if (skill.getExp() >= experience || skill.getExp() > 200000000) {
            owner.sendMessage("You cannot set that experience target.");
            return;
        }
        skill.setTargetLevel(-1);
        skill.setTargetExp(experience);
        refreshTargetValues();
    }

    public void removeTarget(SkillType type) {
        Skill skill = skills.get(type);
        if (skill.isTargeting()) {
            owner.sendMessage("You clear the skill target.");
        } else {
            owner.sendMessage("There isn't any target to clear.");
        }
        skill.setTargetLevel(-1);
        skill.setTargetExp(-1);
        refreshTargetValues();
    }

    private void refreshTargetValues() {
        if (this.owner instanceof Player) {
            Player p = (Player) this.owner;
            p.getProtocol().sendConfig(1966, getTargetConfigValue(false));
            p.getProtocol().sendConfig(1968, getTargetConfigValue(true));
            for (int i = 0; i < skills.size(); i++) {
                Skill skill = skills.get(SkillType.forTargetId(i));
                if (!skill.isTargeting()) {
                    p.getProtocol().sendConfig(1969 + skill.getType().getTargetId(), 0);
                } else {
                    if (skill.getTargetExp() > 0) {
                        p.getProtocol().sendConfig(1969 + skill.getType().getTargetId(), (int) skill.getTargetExp());
                    } else {
                        p.getProtocol().sendConfig(1969 + skill.getType().getTargetId(), skill.getTargetLevel());
                    }
                }
            }
        }
    }

    private int getTargetConfigValue(boolean experienceTarget) {
        int value = 0;
        for (int index = 1; index < skills.size() + 1; index++) {
            Skill skill = skills.get(SkillType.forTargetId(index - 1));
            if (skill.isTargeting()) {
                if (experienceTarget && skill.getTargetLevel() < 0) continue;
                value += 1 << index;
            }
        }
        return value;
    }

    public int getTotal() {
        int total = 0;
        for (Skill s : this.skills.values()) {
            total += s.getLevel();
        }
        return total;
    }
}