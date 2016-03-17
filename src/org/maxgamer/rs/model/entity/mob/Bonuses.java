package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.structure.dbmodel.Mapping;
import org.maxgamer.rs.structure.dbmodel.Transparent;

/**
 * @author netherfoam
 */
public class Bonuses extends Transparent{
	/** Improves chance of successful stab attacks */
	public static final int ATK_STAB = 0;
	/** Improves chance of successful slash attacks */
	public static final int ATK_SLASH = 1;
	/** Improves chance of successful crush attacks */
	public static final int ATK_CRUSH = 2;
	/** Improves chance of successful magic attacks */
	public static final int ATK_MAGIC = 3;
	/** Improves chance of successful range attacks */
	public static final int ATK_RANGE = 4;
	
	/** Improves chance of successful stab defends */
	public static final int DEF_STAB = 5;
	/** Improves chance of successful slash defends */
	public static final int DEF_SLASH = 6;
	/** Improves chance of successful crush defends */
	public static final int DEF_CRUSH = 7;
	/** Improves chance of successful magic defends */
	public static final int DEF_MAGIC = 8;
	/** Improves chance of successful range defends */
	public static final int DEF_RANGE = 9;
	/** Improves chance of successful summoning defends */
	public static final int DEF_SUMMON = 10;
	
	/** Improves damage for melee */
	public static final int POW_STRENGTH = 14;
	/** Improves damage for range */
	public static final int POW_RANGE = 12;
	/** Improves prayer drain rate */
	public static final int PRAYER = 13;
	/** Improves damage for magic */
	public static final int POW_MAGIC = 11;
	
	public static final int COUNT = 15;
	
	@Mapping
	private int atk_stab;
	@Mapping
	private int atk_slash;
	@Mapping
	private int atk_crush;
	@Mapping
	private int atk_magic;
	@Mapping
	private int atk_range;
	@Mapping
	private int def_stab;
	@Mapping
	private int def_slash;
	@Mapping
	private int def_crush;
	@Mapping
	private int def_magic;
	@Mapping
	private int def_range;
	@Mapping
	private int def_summon;
	@Mapping
	private int pow_magic;
	@Mapping
	private int pow_range;
	@Mapping
	private int pow_prayer;
	@Mapping
	private int pow_strength;
	
	public Bonuses(){
		super();
	}
	
	public int[] toArray(){
		return new int[]{
				atk_stab,
				atk_slash,
				atk_crush,
				atk_magic,
				atk_range,
				
				def_stab,
				def_slash,
				def_crush,
				def_magic,
				def_range,
				def_summon,
				
				pow_magic,
				pow_range,
				pow_prayer,
				pow_strength
		};
	}
	
	public int getBonus(int type){
		switch(type){
			case ATK_STAB:
				return atk_stab;
			case ATK_SLASH:
				return atk_slash;
			case ATK_CRUSH:
				return atk_crush;
			case ATK_MAGIC:
				return atk_magic;
			case ATK_RANGE:
				return atk_range;
			case DEF_STAB:
				return def_stab;
			case DEF_SLASH:
				return def_slash;
			case DEF_CRUSH:
				return def_crush;
			case DEF_MAGIC:
				return def_magic;
			case DEF_RANGE:
				return def_range;
			case DEF_SUMMON:
				return def_summon;
			case POW_MAGIC:
				return pow_magic;
			case PRAYER:
				return pow_prayer;
			case POW_STRENGTH:
				return pow_strength;
			case POW_RANGE:
				return pow_range;
		}
		throw new IllegalArgumentException("No such bonus type: " + type);
	}
	
	public int getAtkStab(){
		return atk_stab;
	}
	
	public int getAtkSlash(){
		return atk_slash;
	}
	
	public int getAtkCrush(){
		return atk_crush;
	}
	
	public int getAtkMagic(){
		return atk_magic;
	}
	
	public int getAtkRange(){
		return atk_range;
	}
	
	public int getDefStab(){
		return def_stab;
	}
	
	public int getDefSlash(){
		return def_slash;
	}
	
	public int getDefCrush(){
		return def_crush;
	}
	
	public int getDefMagic(){
		return def_magic;
	}
	
	public int getDefRange(){
		return def_range;
	}
	
	public int getDefSummon(){
		return def_summon;
	}
	
	public int getPowRange(){
		return pow_range;
	}
	
	public int getPowPrayer(){
		return pow_prayer;
	}
	
	public int getPowMagic(){
		return pow_magic;
	}
	
	public int getPowStrength(){
		return pow_strength;
	}
}