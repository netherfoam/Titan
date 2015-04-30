package org.maxgamer.rs.model.skill;

import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.structure.configs.ConfigSection;

/**
 * @author netherfoam
 */
public class SkillSet implements YMLSerializable {
	/**
	 * The owner of this skillset
	 */
	private Mob owner;
	
	/**
	 * The map of skill type to skill. Each SkillType is an identifier (Eg
	 * woodcutting or smithing) and the Skill is an instance of Skill, which
	 * holds the current exp and modifier.
	 */
	protected final HashMap<SkillType, Skill> skills = new HashMap<SkillType, Skill>();
	
	/**
	 * Constructs a new SkillSet which only has attack, strength, defence, magic
	 * and range levels for the given NPC. They default to 0.
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
	 * @param m the owner
	 */
	protected SkillSet(Mob m) {
		this.owner = m;
	}
	
	/**
	 * Sets the exp for the given skill to the given amount. This updates the
	 * owner's combat level
	 * @param t the skill to set
	 * @param exp the exp to set
	 * @throws NullPointerException if skilltype is null or if exp is null
	 */
	public void setExp(SkillType t, double exp) {
		if (t == null) throw new NullPointerException();
		if (exp < 0) throw new IllegalArgumentException("Exp must be >= 0");
		
		if (skills.containsKey(t) == false) {
			skills.put(t, new Skill(t, 0));
		}
		
		skills.get(t).setExp(exp);
		
		if (t == SkillType.ATTACK || t == SkillType.STRENGTH || t == SkillType.DEFENCE || t == SkillType.CONSTITUTION || t == SkillType.RANGE || t == SkillType.MAGIC || t == SkillType.PRAYER) {
			int combat = 3;
			combat = (int) ((getLevel(SkillType.DEFENCE) + getLevel(SkillType.CONSTITUTION) + Math.floor(getLevel(SkillType.PRAYER) / 2)) * 0.25) + 1;
			double melee = (getLevel(SkillType.ATTACK) + getLevel(SkillType.STRENGTH)) * 0.325;
			double range = Math.floor(getLevel(SkillType.RANGE) * 1.5) * 0.325;
			double magic = Math.floor(getLevel(SkillType.MAGIC) * 1.5) * 0.325;
			if (melee >= range && melee >= magic) {
				combat += melee;
			}
			else if (range >= melee && range >= magic) {
				combat += range;
			}
			else if (magic >= melee && magic >= range) {
				combat += magic;
			}
			combat += getLevel(SkillType.SUMMONING) / 8;
			
			owner.getModel().setCombatLevel(combat);
		}
		
		if (owner instanceof Client) {
			Client c = (Client) owner;
			RSOutgoingPacket out = new RSOutgoingPacket(8);
			out.writeInt1((int) exp);
			out.writeByteS(t.getId());
			out.writeByteS((byte) getLevel(t) + (int) getModifier(t));
			c.write(out);
		}
	}
	
	public double getExp(SkillType t) {
		if (skills.containsKey(t) == false) {
			return 0;
		}
		
		return skills.get(t).getExp();
	}
	
	public void addExp(SkillType t, double exp) {
		if (t == null) throw new NullPointerException();
		
		if (skills.containsKey(t) == false) {
			skills.put(t, new Skill(t, 0));
		}
		setExp(t, skills.get(t).getExp() + exp);
	}
	
	public void setLevel(SkillType t, int level) {
		if (t == null) throw new NullPointerException();
		if (level < 0) throw new IllegalArgumentException("Level must be > 0");
		
		if (skills.containsKey(t) == false) {
			skills.put(t, new Skill(t, 0));
		}
		
		skills.get(t).setLevel(level);
		
		if (owner instanceof Client) {
			Client c = (Client) owner;
			RSOutgoingPacket out = new RSOutgoingPacket(8);
			out.writeInt1((int) getExp(t));
			out.writeByteS(t.getId());
			out.writeByteS((byte) getLevel(t) + (int) getModifier(t));
			c.write(out);
		}
	}
	
	public int getLevel(SkillType t) {
		if (t == null) throw new NullPointerException();
		if (skills.containsKey(t) == false) {
			return 0;
		}
		
		return skills.get(t).getLevel();
	}
	
	/**
	 * Returns the current level for the given skill. If modifier is true, then
	 * this will add whatever modifier is present. Else, it will simply return
	 * getLevel(t)
	 * @param t the skill type
	 * @param modifier true if you want to include modifiers, false otherwise
	 * @return the level, plus modifiers if requested
	 */
	public int getLevel(SkillType t, boolean modifier) {
		if (skills.containsKey(t) == false) {
			return 0;
		}
		
		if (modifier) {
			return getLevel(t) + (int) getModifier(t);
		}
		else {
			return getLevel(t);
		}
	}
	
	public void setModifier(SkillType t, double modifier) {
		if (skills.containsKey(t) == false) {
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
	}
	
	public double getModifier(SkillType t) {
		if (skills.containsKey(t) == false) {
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
	 * @param t The skill to modify/buff
	 * @param multiplier the multiplier, where 1 = normal, 1.1 = 10% boost, 0.9
	 *        = 10% decrease.
	 */
	public void buff(SkillType t, double multiplier) {
		if (t == null) {
			throw new NullPointerException("SkillType may not be null");
		}
		
		if (multiplier < 0) {
			throw new IllegalArgumentException("Multiplier must be >= 0");
		}
		
		if (skills.containsKey(t) == false) {
			skills.put(t, new Skill(t, 0));
		}
		
		double curMod = getModifier(t);
		double realLev = getLevel(t, false);
		
		//The number of levels the multiplier would add/remove
		double delta = (multiplier - 1) * realLev;
		
		if (delta > 0) {
			//curMod += delta;
			//curMod = Math.min(curMod + delta, delta);
			if (curMod + delta > delta) {
				curMod = Math.max(curMod, delta);
			}
			else {
				curMod = curMod + delta;
			}
		}
		else if (delta < 0) {
			if (curMod + delta < delta) {
				curMod = delta;
			}
			else {
				curMod = Math.min(curMod, delta);
			}
		}
		
		setModifier(t, curMod);
	}
	
	@Override
	public ConfigSection serialize() {
		ConfigSection map = new ConfigSection();
		for (Entry<SkillType, Skill> e : this.skills.entrySet()) {
			map.set(e.getKey().toString(), e.getValue().serialize());
		}
		
		return map;
	}
	
	@Override
	public void deserialize(ConfigSection map) {
		for (SkillType t : SkillType.values()) {
			Skill s = new Skill(t, 0);
			ConfigSection data = map.getSection(t.toString(), null);
			if (data != null) {
				s.deserialize(data);
			}
			else {
				//Constitution defaults to lvl 10 instead of 1
				if (t == SkillType.CONSTITUTION) {
					s.setLevel(10);
				}
				else {
					s.setLevel(1);
				}
			}
			
			setExp(t, s.getExp());
			setModifier(t, s.getModifier());
		}
		
	}
}