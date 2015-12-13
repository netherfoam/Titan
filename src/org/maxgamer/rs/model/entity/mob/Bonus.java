package org.maxgamer.rs.model.entity.mob;

/**
 * @author netherfoam
 */
public class Bonus {
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
	
	private Bonus() {
		//Private constructor
	}
}