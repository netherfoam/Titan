package org.maxgamer.rs.model.entity.mob.persona.player;

import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.structure.areagrid.Cube;

/**
 * Represents the view distance for a player. This is how many tiles the
 * player's client loads and will be able to see. In the last chunk (8x8 area)
 * at the border of the map, the player begins glitching and instead of walking,
 * they warp around.
 * @author netherfoam
 */
public enum ViewDistance {
	/** Small, 104 tiles, or 6 chunks to the left and right of the player. */
	SMALL(104, 0),
	/** Medium, 120 tiles, or 7 chunks to the left and right of the player. */
	MEDIUM(120, 1),
	/** Large, 136 tiles, or 8 chunks to the left and right of the player. */
	LARGE(136, 2),
	/** Huge, 168 tiles, or 10 chunks to the left and right of the player. */
	HUGE(168, 3);
	
	private int tiles;
	private byte id;
	
	private ViewDistance(int tiles, int id) {
		this.tiles = tiles;
		this.id = (byte) id;
	}
	
	/**
	 * The number of tiles this requires to be loaded. Eg, 104, 120, 136 or 168.
	 * @return
	 */
	public int getTileSize() {
		return tiles;
	}
	
	/**
	 * The radius (in chunks!) that this view distance requires. This does not
	 * include the chunk from which the view is started. Eg, A player's chunk
	 * will not be counted, but the one adjacent to it will be number 1. The
	 * mathematical formula for this is ((tiles - 8) / 2) >>
	 * WorldMap.CHUNK_BITS;
	 * @return The radius in chunks of this view distance.
	 */
	public int getChunkRadius() {
		return ((tiles - 8) / 2) >> WorldMap.CHUNK_BITS;
	}
	
	/**
	 * Network ID for this view distance. The player needs to be sent this
	 * number to know which distance to load.
	 * @return the network ID.
	 */
	public byte getId() {
		return id;
	}
	
	/**
	 * An MBR which overlaps with all of the area around the given location that
	 * is visible on the world map. For example, if you wish to query all the
	 * entities a player can see. The argument, center, must be the last
	 * location that the player was sent a map from. Eg, they could login, walk
	 * 25 squares, and the correct center argument should still be their initial
	 * login location.
	 * @param center The center location. Not necessarily the player's location.
	 * @return The area that overlaps with all visible chunks, not null.
	 */
	public Cube getArea(Location center) {
		int chunks = getChunkRadius();
		WorldMap m = center.getMap();
		
		int minX = Calc.betweeni(0, m.getSizeX() - 1, (center.x & ~0x7) - (chunks * 8));
		int minY = Calc.betweeni(0, m.getSizeY() - 1, (center.y & ~0x7) - (chunks * 8));
		
		int maxX = Calc.betweeni(0, m.getSizeX() - 1, (center.x & ~0x7) + (chunks * 8) + 7);
		int maxY = Calc.betweeni(0, m.getSizeY() - 1, (center.y & ~0x7) + (chunks * 8) + 7);
		
		Cube cube = new Cube(new int[] { minX, minY, 0 }, new int[] { maxX - minX, maxY - minY, 3 }); //TODO: Should this really be 4? not 3?
		return cube;
	}
}