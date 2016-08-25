package org.maxgamer.rs.model.entity.mob;

/**
 * @author netherfoam
 */
public class Factions {
	public static final Faction PLAYER = new Faction("Player");
	public static final Faction WILDERNESS = new Faction("Wilderness");
	public static final Faction NONE = new Faction("None");
	
	static {
		WILDERNESS.setEnemy(WILDERNESS);
	}
}