package org.maxgamer.rs.model.map.area;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.areagrid.MBRUtil;

/**
 * @author netherfoam
 */
public class Area implements MBR {
	private Location min;
	private Location max;
	private HashSet<AreaFlag> flags;

	public Area(Location min, Location max) {
		if (min.getMap() != max.getMap())
			throw new IllegalArgumentException("An Area must range across only one map.");
		this.flags = new HashSet<AreaFlag>(0);
		int minX, minY, minZ;
		int maxX, maxY, maxZ;

		if (min.x <= max.x) {
			minX = min.x;
			maxX = max.x;
		} else {
			minX = max.x;
			maxX = min.x;
		}

		if (min.y <= max.y) {
			minY = min.y;
			maxY = max.y;
		} else {
			minY = max.y;
			maxY = min.y;
		}

		if (min.z <= max.z) {
			minZ = min.z;
			maxZ = max.z;
		} else {
			minZ = max.z;
			maxZ = min.z;
		}

		this.min = new Location(min.getMap(), minX, minY, minZ);
		this.max = new Location(min.getMap(), maxX, maxY, maxZ);
	}

	public boolean hasFlag(AreaFlag f) {
		return flags.contains(f);
	}

	public void addFlag(AreaFlag f) {
		this.flags.add(f);
	}

	public void removeFlag(AreaFlag f) {
		this.flags.remove(f);
	}

	/**
	 * Returns an unmodifiable set of flags which are attached to this area.
	 * 
	 * @return the flags.
	 */
	public Set<AreaFlag> getFlags() {
		return Collections.unmodifiableSet(this.flags);
	}

	@Override
	public int getMin(int axis) {
		return min.getMin(axis);
	}

	@Override
	public int getDimension(int axis) {
		return max.getMin(axis) - min.getMin(axis);
	}

	@Override
	public int getDimensions() {
		return min.getDimensions(); // three
	}

	public boolean contains(MBR m) {
		return MBRUtil.isOverlap(this, m);
	}

	public boolean contains(Position p) {
		int x1 = Math.min(min.x, max.x);
		int x2 = Math.max(min.x, max.x);
		int y1 = Math.min(min.y, max.y);
		int y2 = Math.max(min.y, max.y);
		return p.x >= x1 && p.y >= y1 && p.x <= x2 && p.y <= y2;
	}

	public WorldMap getMap() {
		return min.getMap();
	}
}