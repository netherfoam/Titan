package org.maxgamer.rs.model.map.path;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.model.map.ClipMasks;

/**
 * @author netherfoam
 */
public class Directions {
	//Old, work mostly. But these do not check the blocked or wall flags.
	/*
	 * public static final SimpleDirection NORTH = new SimpleDirection(0, 1,
	 * 0x48240000); public static final SimpleDirection SOUTH = new
	 * SimpleDirection(0, -1, 0x40a40000); public static final SimpleDirection
	 * EAST = new SimpleDirection(1, 0, 0x60240000); public static final
	 * SimpleDirection WEST = new SimpleDirection(-1, 0, 0x42240000); public
	 * static final ComplexDirection NORTH_EAST = new ComplexDirection(NORTH,
	 * EAST, 0x78240000); public static final ComplexDirection NORTH_WEST = new
	 * ComplexDirection(NORTH, WEST, 0x4e240000); public static final
	 * ComplexDirection SOUTH_EAST = new ComplexDirection(SOUTH, EAST,
	 * 0x60e40000); //This mask was found in dementhium. public static final
	 * ComplexDirection SOUTH_WEST = new ComplexDirection(SOUTH, WEST,
	 * 0x43a40000);
	 */
	
	public static final SimpleDirection NORTH = new SimpleDirection(0, 1, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_SOUTH | ClipMasks.WALL_SOUTH, ClipMasks.BLOCKED_NORTH | ClipMasks.WALL_NORTH);
	public static final SimpleDirection SOUTH = new SimpleDirection(0, -1, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_NORTH | ClipMasks.WALL_NORTH, ClipMasks.BLOCKED_SOUTH | ClipMasks.WALL_SOUTH);
	
	public static final SimpleDirection EAST = new SimpleDirection(1, 0, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_WEST | ClipMasks.WALL_WEST, ClipMasks.BLOCKED_EAST | ClipMasks.WALL_EAST);
	public static final SimpleDirection WEST = new SimpleDirection(-1, 0, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_EAST | ClipMasks.WALL_EAST, ClipMasks.BLOCKED_WEST | ClipMasks.WALL_WEST);
	
	public static final ComplexDirection NORTH_EAST = new ComplexDirection(NORTH, EAST, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_SOUTH_WEST | ClipMasks.WALL_SOUTH_WEST, ClipMasks.BLOCKED_NORTH_EAST | ClipMasks.WALL_NORTH_EAST);
	public static final ComplexDirection NORTH_WEST = new ComplexDirection(NORTH, WEST, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_SOUTH_EAST | ClipMasks.WALL_SOUTH_EAST, ClipMasks.BLOCKED_NORTH_WEST | ClipMasks.WALL_NORTH_WEST);
	
	public static final ComplexDirection SOUTH_EAST = new ComplexDirection(SOUTH, EAST, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_NORTH_WEST | ClipMasks.WALL_NORTH_WEST, ClipMasks.BLOCKED_SOUTH_EAST | ClipMasks.WALL_SOUTH_EAST);
	public static final ComplexDirection SOUTH_WEST = new ComplexDirection(SOUTH, WEST, ClipMasks.BLOCKED_TILE | ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_NORTH_EAST | ClipMasks.WALL_NORTH_EAST, ClipMasks.BLOCKED_SOUTH_WEST | ClipMasks.WALL_SOUTH_WEST);
	
	public static final SimpleDirection[] SIMPLE = new SimpleDirection[] { NORTH, SOUTH, EAST, WEST };
	public static final ComplexDirection[] COMPLEX = new ComplexDirection[] { NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST };
	public static final Direction[] ALL = new Direction[] { NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST };
	
	private static HashMap<String, Direction> names = new HashMap<String, Direction>();
	
	static {
		for (Field f : Directions.class.getFields()) {
			try {
				if (Direction.class.isInstance(f.get(null))) {
					names.put(f.getName(), (Direction) f.get(null));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the direction next to the given one
	 * @param d the direction
	 * @param antiClockwise true if we're going anticlockwise (Eg NORTH returns
	 *        NORTH_WEST, SOUTH_EAST returns EAST), false if we go clockwise
	 * @return the direction next to the given one, not null.
	 */
	public static Direction next(Direction d, boolean antiClockwise) {
		for (int i = 0; i < ALL.length; i++) {
			if (ALL[i] == d) {
				if (antiClockwise) {
					if (i == 0) return ALL[ALL.length - 1];
					return ALL[i - 1];
				}
				else {
					if (i == ALL.length - 1) return ALL[0];
					return ALL[i + 1];
				}
			}
		}
		throw new IllegalArgumentException("Bad direction given, given " + d);
	}
	
	/**
	 * Fetches the string name of the given direction.
	 * @param d the direction
	 * @return the name, upper case with underscores.
	 */
	public static String getName(Direction d) {
		for (Entry<String, Direction> e : names.entrySet()) {
			if (e.getValue() == d) return e.getKey();
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Finds the opposite direction of the given direction. Eg, rotate 180
	 * degrees
	 * @param d the direction
	 * @return the opposite direction
	 */
	public static Direction opposite(Direction d) {
		for (int i = 0; i < ALL.length; i++) {
			if (ALL[i] == d) {
				int j = i + 4; //Opposite
				return ALL[j % ALL.length];
			}
		}
		throw new IllegalArgumentException(d + " is not a valid direction.");
	}
	
	/**
	 * Rotates the given direction by the given amount
	 * @param d the direction to rotate
	 * @param degrees the degrees to rotate it, this must be a multiple of 45.
	 *        Rotating 360 does nothing.
	 * @return the rotated direction (EG, rotate NORTH 45 degrees equals
	 *         NORTH_EAST)
	 */
	public static Direction rotate(Direction d, int degrees) {
		int mod = degrees % ALL.length;
		if (mod != 0) throw new IllegalArgumentException("Directions must be rotated by a multiple of " + (360 / ALL.length) + " degrees");
		for (int i = 0; i < ALL.length; i++) {
			if (ALL[i] == d) {
				return ALL[(i + mod) % ALL.length];
			}
		}
		throw new IllegalArgumentException(d + " is not a valid direction.");
	}
	
	public static Direction forName(String s) {
		s = s.toUpperCase();
		return names.get(s);
	}
	
	/**
	 * Finds a direction that will have the given directional effects when
	 * applied.
	 * @param dx the north movement intended (Negative becomes south)
	 * @param dy the east movement intended (Negative becomes west)
	 * @return the direction
	 * @throws IllegalArgumentException if the given values are both 0 or have
	 *         an absolute value greater than 1.
	 */
	public static Direction get(int dx, int dy) {
		if (dx != 0 && dy != 0) {
			//It's complex
			for (ComplexDirection d : COMPLEX) {
				if (d.dx == dx && d.dy == dy) {
					return d;
				}
			}
		}
		else {
			//It's simple. We know that only one of the numbers is != 0.
			//That means, we only need to compare one number with the other.
			if (dx != 0) {
				//We only need to compare DX
				for (SimpleDirection d : SIMPLE) {
					if (d.dx == dx) {
						return d;
					}
				}
			}
			else {
				//We only need to compare DY
				for (SimpleDirection d : SIMPLE) {
					if (d.dy == dy) {
						return d;
					}
				}
			}
		}
		throw new IllegalArgumentException("Failed to find a location for: dx: " + dx + ", dy: " + dy);
	}
}