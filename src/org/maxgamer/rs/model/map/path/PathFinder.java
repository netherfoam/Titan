package org.maxgamer.rs.model.map.path;

import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public interface PathFinder {
	/**
	 * Finds a path from the given starting location, to a position that is
	 * contained within the bounds of min and max, given that the size of the
	 * entity moving is xSize and ySize in coordinates.
	 * @param start The starting location
	 * @param min The lower bound of the target location
	 * @param max The upper bound of the target location
	 * @param xSize The entity's size on the x axis
	 * @param ySize The entity's size on the y axis
	 * @return The path, potentially failed.
	 */
	public Path findPath(Location start, Position min, Position max, int xSize, int ySize);
	
}