package org.maxgamer.rs.model.skill;

/**
 * @author netherfoam
 */
public enum SkillType {
	ATTACK(0, 99, "Attack"), DEFENCE(1, 99, "Defence"), STRENGTH(2, 99, "Strength"), CONSTITUTION(3, 99, "Constitution"), RANGE(4, 99, "Range"), PRAYER(5, 99, "Prayer"), MAGIC(6, 99, "Magic"), COOKING(7, 99, "Cooking"), WOODCUTTING(8, 99, "Woodcutting"), FLETCHING(9, 99, "Fletching"), FISHING(10, 99, "Fishing"), FIREMAKING(11, 99, "Firemaking"), CRAFTING(
			12, 99, "Crafting"), SMITHING(13, 99, "Smithing"), MINING(14, 99, "Mining"), HERBLORE(15, 99, "Herblore"), THIEVING(16, 99, "Thieving"), AGILITY(17, 99, "Agility"), SLAYER(18, 99, "Slayer"), FARMING(19, 99, "Farming"), RUNECRAFTING(20, 99, "Runecrafting"), HUNTER(21, 99, "Hunter"), CONSTRUCTION(22, 99, "Construction"), SUMMONING(23, 99,
			"Summoning"), DUNGEONEERING(24, 120, "Dungeoneering");
	
	/**
	 * Maximum experience a skill can have. Any experience after this point is
	 * ignored.
	 */
	public static final double MAX_EXP = 200000000;
	
	private static final int[] EXPERIENCE_REQUIRED = new int[1000];
	static {
		double points = 0;
		for (int lvl = 1; lvl < EXPERIENCE_REQUIRED.length; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			EXPERIENCE_REQUIRED[lvl] = (int) points / 4;
		}
	}
	
	public static SkillType forId(int id){
		//Is this optimised by the compiler?
		return SkillType.values()[id];
	}
	
	/** Network ID */
	private byte id;
	/** Max level */
	private int max;
	/** Nice name of skill */
	private String name;
	
	private SkillType(int id, int max, String name) {
		this.id = (byte) id;
		this.max = max;
		this.name = name;
	}
	
	/**
	 * The network ID of the skill
	 * @return The network ID of the skill
	 */
	public byte getId() {
		return id;
	}
	
	/**
	 * The max level for the skill
	 * @return The max level for the skill
	 */
	public int getMax() {
		return max;
	}
	
	/**
	 * The nicely formatted name of the skill
	 * @return The nicely formatted name of the skill
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The level for the given amount of experience for this skill. This never
	 * exceeds getMax().
	 * @param exp the amount of experience
	 * @return the level for this skill at the given exp.
	 */
	public int getLevel(double exp) {
		int max = getMax();
		for (int i = 1; i < max; i++) {
			if (EXPERIENCE_REQUIRED[i] > exp) {
				return i;
			}
		}
		
		return max;
	}
	
	/**
	 * Fetches the exp that is required for the given level
	 * @param level the level to reach
	 * @return the exp required.
	 * @throws IllegalArgumentException if the given level is not between 1-120
	 *         inclusive.
	 */
	public static double getExpRequired(int level) {
		if (level <= 0) {
			throw new IllegalArgumentException("Skill levels must be > 0 and <= 120");
		}
		return EXPERIENCE_REQUIRED[level - 1];
	}
}