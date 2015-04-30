package org.maxgamer.rs.model.map;

import java.util.HashSet;

import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.structure.areagrid.Cube;
import org.maxgamer.structure.areagrid.MBR;
import org.maxgamer.structure.configs.ConfigSection;

/**
 * Represents a location in game.
 * @author netherfoam
 */
public class Location extends Position implements MBR {
	public static Location deserialize(ConfigSection s, WorldMap map, Location fallback) {
		if (s == null) {
			return fallback;
		}
		
		int x = s.getInt("x", -1);
		int y = s.getInt("y", -1);
		int z = s.getInt("z", 0);
		
		if (x == -1 || y == -1) {
			return fallback;
		}
		return new Location(map, x, y, z);
	}
	
	/** The map that this position is based on */
	private WorldMap map;
	
	/** The height plane for this location. */
	public final int z;
	
	/**
	 * Constructs a new location with the given parameters
	 * @param map the map
	 * @param x the x coordinate (West[0] to East[MAX])
	 * @param y the y coordinate (South[0] to North[MAX])
	 * @param z the z or height coordinate
	 * @throws IllegalArgumentException if the z value is < 0 or > 3.
	 */
	public Location(WorldMap map, int x, int y, int z) {
		super(x, y);
		this.map = map;
		this.z = z;
		
		if (z < 0 || z > 3) {
			throw new IllegalArgumentException("Heights must be in the range of 0-3 inclusive. Given Z: " + z);
		}
	}
	
	/**
	 * The flags for this location. This will throw a
	 * {@link NullPointerException} if the map is null. The flags are a bitset,
	 * see Chunk.FLAG_* for values. These are flags like indoors and bridge
	 * flags. If the area at this location is not loaded, this will return 0
	 * @return the flags for this location, 0 represents no special flags.
	 */
	public int getFlags() {
		return map.getFlags(this.x, this.y, this.z);
	}
	
	/**
	 * The clip for this location. This will throw {@link NullPointerException}
	 * if the map is null. The clip is a 32 bit int mask. See {@link ClipMasks}
	 * for a list of clip masks by name. If this area of the map is not loaded,
	 * this will return -1 (Equivilant to all flags)
	 * @return the clip for this location.
	 */
	public int getClip() {
		return map.getClip(this.x, this.y, this.z);
	}
	
	/**
	 * The map that this location is on
	 * @return the map
	 */
	public WorldMap getMap() {
		return map;
	}
	
	@Override
	public int getMin(int axis) {
		switch (axis) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
		}
		throw new IllegalArgumentException("Invalid axis requested. Given " + axis);
	}
	
	@Override
	public int getDimension(int axis) {
		return 1;
	}
	
	@Override
	public int getDimensions() {
		return 3;
	}
	
	/**
	 * Fetches all entities of a given type in a square radius of this location.
	 * @param clazz the type to search for, eg use Player.class to get players.
	 * @param radius the radius, this is square and not circular.
	 * @return the entities nearby not null.
	 */
	public <T extends MBR> HashSet<T> getNearby(Class<T> clazz, int radius) {
		return getNearby(clazz, radius, false);
	}
	
	/**
	 * Fetches all entities of a given type in a square radius of this location.
	 * @param clazz the type to search for, eg use Player.class to get players.
	 * @param radius the radius, this is square and not circular.
	 * @return the entities nearby not null.
	 */
	public <T extends MBR> HashSet<T> getNearby(Class<T> clazz, int radius, boolean allHeights) {
		MBR q = new Cube(new int[] { this.x - radius, this.y - radius, this.z }, new int[] { radius * 2 + 1, radius * 2 + 1, allHeights ? 3 : 0 });
		
		HashSet<T> entities = getMap().getEntities(q, radius * radius + 4, clazz);
		return entities;
	}
	
	/**
	 * Fetches all entities of a given type in a square radius of this location.
	 * @param clazz the type to search for, eg use Player.class to get players.
	 * @param radX the radius for the x coordinate
	 * @param radY the radius for the y coordinate
	 * @return the entities nearby not null.
	 */
	public <T extends MBR> HashSet<T> getNearby(Class<T> clazz, int radX, int radY) {
		MBR q = new Cube(new int[] { this.x - radX, this.y - radY, this.z }, new int[] { radX * 2, radY * 2, 0 });
		HashSet<T> entities = getMap().getEntities(q, radX * radY + 4, clazz);
		return entities;
	}
	
	/**
	 * Constructs a new location by adding the given values to this existing
	 * location's coordinates. This does not modify this location
	 * @param x the coordinate
	 * @param y the coordinate
	 * @return the new location
	 */
	public Location add(int x, int y) {
		return add(x, y, 0);
	}
	
	/**
	 * Constructs a new location by adding the given values to this existing
	 * location's coordinates. This does not modify this location
	 * @param x the coordinate
	 * @param y the coordinate
	 * @return the new location
	 */
	public Location add(int x, int y, int z) {
		return new Location(getMap(), this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Returns the chunk coordinate of this location by shifting its x
	 * coordinate right
	 * @return the chunk coordinate
	 */
	public int getChunkX() {
		return x >> WorldMap.CHUNK_BITS;
	}
	
	/**
	 * Returns the chunk coordinate of this location by shifting its y
	 * coordinate right
	 * @return the chunk coordinate
	 */
	public int getChunkY() {
		return y >> WorldMap.CHUNK_BITS;
	}
	
	/**
	 * Returns the chunk coordinate of this location by shifting its chunkX
	 * right
	 * @return the region coordinate
	 */
	public int getRegionX() {
		return getChunkX() >> 3;
	}
	
	/**
	 * Returns the chunk coordinate of this location by shifting its chunkY
	 * right
	 * @return the region coordinate
	 */
	public int getRegionY() {
		return getChunkY() >> 3;
	}
	
	/**
	 * Returns the Direction required to move to the given tile from this tile.
	 * This method only works for tiles which are next to (diagonal or
	 * cardinally) this Location.
	 * @param to the target position
	 * @return the Direction, not null
	 * @throws IllegalArgumentException if the two locations are not next to
	 *         each other.
	 */
	public Direction getDirection(Position to) {
		int dx = to.x - this.x;
		int dy = to.y - this.y;
		Direction d = Directions.get(dx, dy); //Throws an IllegalArgumentException if positions aren't next to each other
		return d;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")@" + map.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o.getClass() != this.getClass()) return false;
		Location p = (Location) o;
		if (p.x != this.x) return false;
		if (p.y != this.y) return false;
		if (p.z != this.z) return false;
		if (p.map != this.map) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		//If we had negative values for y, then
		//this may cause lots of hash collisions.
		//But, as a rule of thumb, we don't.
		return ((z << 30) | (x << 15) | (y)) + map.hashCode();
	}
}