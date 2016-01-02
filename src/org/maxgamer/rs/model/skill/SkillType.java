package org.maxgamer.rs.model.skill;

/**
 * @author netherfoam
 */
public enum SkillType {
	ATTACK(0, 99, 0, "Attack"),
	DEFENCE(1, 99, 4, "Defence"),
	STRENGTH(2, 99, 1, "Strength"),
	CONSTITUTION(3, 99, 5, "Constitution"),
	RANGE(4, 99, 2, "Range"),
	PRAYER(5, 99, 6, "Prayer"),
	MAGIC(6, 99, 3, "Magic"),
	COOKING(7, 99, 15, "Cooking"),
	WOODCUTTING(8, 99, 17, "Woodcutting"),
	FLETCHING(9, 99, 18, "Fletching"),
	FISHING(10, 99, 14, "Fishing"),
	FIREMAKING(11, 99, 16, "Firemaking"),
	CRAFTING(12, 99, 10, "Crafting"),
	SMITHING(13, 99, 13, "Smithing"),
	MINING(14, 99, 12, "Mining"),
	HERBLORE(15, 99, 8, "Herblore"),
	THIEVING(16, 99, 9, "Thieving"),
	AGILITY(17, 99, 7, "Agility"),
	SLAYER(18, 99, 19, "Slayer"),
	FARMING(19, 99, 20, "Farming"),
	RUNECRAFTING(20, 99, 11, "Runecrafting"),
	HUNTER(21, 99, 22, "Hunter"),
	CONSTRUCTION(22, 99, 21, "Construction"),
	SUMMONING(23, 99, 23, "Summoning"),
	DUNGEONEERING(24, 120, 24, "Dungeoneering");
	
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
	
	public static SkillType forId(int id) {
		//Is this optimised by the compiler?
		return SkillType.values()[id];
	}
	
	public static SkillType forTargetId(int id) {
		for (SkillType type : values())
			if (type.getTargetId() == id) return type;
		//Is this optimised by the compiler?
		return null;
	}
	
	/** Network ID */
	private byte id;
	/** Max level */
	private int max;
	private final int targetId;
	/** Nice name of skill */
	private String name;
	
	private SkillType(int id, int max, int targetId, String name) {
		this.id = (byte) id;
		this.max = max;
		this.name = name;
		this.targetId = targetId;
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
	
	public int getTargetId() {
		return targetId;
	}
}