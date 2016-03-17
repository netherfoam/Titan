package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.skill.SkillType;
import org.nfunk.jep.JEP;

/**
 * @author netherfoam
 */
public abstract class CombatStats {
	/**
	 * The mob who owns these stats
	 */
	private Mob owner;
	
	/**
	 * Constructs a new CombatStats object. This does not modify the mob.
	 * @param owner the owner of the stats
	 * @throws NullPointerException if the owner is null
	 */
	public CombatStats(Mob owner) {
		if (owner == null) throw new NullPointerException("CombatStats owner mob may not be null");
		this.owner = owner;
	}
	
	/**
	 * Returns the owner of these combat stats, not null.
	 * @return the owner of these combat stats
	 */
	public Mob getOwner() {
		return owner;
	}
	
	public abstract int getAttackAnimation();
	
	public abstract int getDefenceAnimation();
	
	public abstract int getDeathAnimation();
	
	private JEP getJep(String key, SkillType type) {
		//TODO: Optimise this by caching the JEP object per-skill?
		JEP parser = new JEP();
		parser.setImplicitMul(false);
		if (owner instanceof Persona) {
			Persona p = (Persona) owner;
			//Add all prayer multipliers
			parser.addVariable("prayer_multiplier", p.getPrayer().getMultiplier(type));
			for (SkillType skill : SkillType.values()) {
				parser.addVariable("prayer_multiplier_" + skill.getName().toLowerCase(), p.getPrayer().getMultiplier(skill));
			}
		}
		else {
			//Set all prayer multipliers to 1.0
			parser.addVariable("prayer_multiplier", 1.0);
			for (SkillType skill : SkillType.values()) {
				parser.addVariable("prayer_multiplier_" + skill.getName().toLowerCase(), 1.0);
			}
		}
		//The main skill level is just skill_level
		parser.addVariable("skill_level", getOwner().getSkills().getLevel(type, true));
		
		//Any specific skill levels are skill_level_name_of_skill eg skill_level_magic
		for (SkillType skill : SkillType.values()) {
			parser.addVariable("skill_level_" + skill.getName().toLowerCase(), getOwner().getSkills().getLevel(type, true));
		}
		parser.addVariable("equip_bonus", 0); //Default to 0
		parser.addVariable("effective_str", 0); //Default to 0
		
		//TOOD: Optimise this by caching the formula
		String formula = Core.getWorldConfig().getString(key, null);
		if (formula == null) {
			Log.debug("No combat formula found for " + key);
			formula = "skill_level * prayer_multiplier + equip_bonus";
		}
		
		parser.parseExpression(formula);
		return parser;
	}
	
	/**
	 * The higher, the more likely this mob is to land a blow.
	 * @return the hit rating
	 */
	public int getMeleeHitRating() {
		JEP parser = getJep("formula.melee.hit", SkillType.ATTACK);
		
		AttackStyle style = getOwner().getAttackStyle();
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(style.getBonusType()));
		
		return (int) parser.getValue();
	}
	
	/**
	 * The higher, the more likely this mob is to land a spell.
	 * @return the hit rating
	 */
	public int getMagicHitRating() {
		JEP parser = getJep("formula.magic.hit", SkillType.MAGIC);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(Bonuses.ATK_MAGIC));
		
		return (int) parser.getValue();
	}
	
	/**
	 * The higher, the more likely this mob is to land a projectile.
	 * @return the hit rating
	 */
	public int getRangeHitRating() {
		JEP parser = getJep("formula.range.hit", SkillType.ATTACK);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(Bonuses.ATK_RANGE));
		
		return (int) parser.getValue();
	}
	
	/**
	 * The mobs max hit with melee
	 * @return the max hit with melee
	 */
	public int getMeleePower() {
		JEP parser = getJep("formula.melee.power", SkillType.STRENGTH);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(Bonuses.POW_STRENGTH));
		
		int strengthLevel = getOwner().getSkills().getLevel(SkillType.STRENGTH, true);
		double prayerModifier = owner instanceof Persona ? ((Persona) owner).getPrayer().getMultiplier(SkillType.STRENGTH) : 1D;

		parser.addVariable("effective_str", (strengthLevel * prayerModifier) + 8.0);
		return (int) parser.getValue();
	}
	
	/**
	 * The mobs max hit with range
	 * @return the max hit with range
	 */
	public int getRangePower() {
		JEP parser = getJep("formula.range.power", SkillType.RANGE);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(Bonuses.POW_RANGE));
		return (int) parser.getValue();
	}
	
	/**
	 * The higher, the more likely this mob is to avoid a blow
	 * @return the defence rating
	 */
	public int getMeleeDefenceRating(int fromType) {
		if (fromType < 0 || fromType > 2) throw new IllegalArgumentException("Bad fromType given for defensive calculation, given " + fromType);
		JEP parser = getJep("formula.melee.defence", SkillType.DEFENCE);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(fromType + Bonuses.DEF_STAB));
		
		return (int) parser.getValue();
	}
	
	/**
	 * The higher, the more likely this mob is to avoid a spell
	 * @return the defence rating
	 */
	public int getMagicDefenceRating() {
		JEP parser = getJep("formula.magic.defence", SkillType.MAGIC);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(Bonuses.DEF_MAGIC));
		
		return (int) parser.getValue();
	}
	
	/**
	 * The higher, the more likely this mob is to avoid a projectile
	 * @return the defence rating
	 */
	public int getRangeDefenceRating() {
		JEP parser = getJep("formula.range.defence", SkillType.RANGE);
		parser.addVariable("equip_bonus", getOwner().getEquipment().getBonus(Bonuses.DEF_RANGE));
		
		return (int) parser.getValue();
	}
}