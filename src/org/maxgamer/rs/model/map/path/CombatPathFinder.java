package org.maxgamer.rs.model.map.path;

import java.util.ArrayList;

import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public class CombatPathFinder implements PathFinder {
	@Override
	public Path findPath(Location start, Position min, Position max, int xSize, int ySize) {
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		
		//The closest point
		Position best = getClosest(start, min, max);
		
		int dx = best.x - start.x;
		int dy = best.y - start.y;
		
		int dxs = Calc.betweeni(-1, 1, dx);
		int dys = Calc.betweeni(-1, 1, dy);
		
		Location pos = start;
		boolean fail = false;
		System.out.println("Pathing...");
		while (dx != 0 || dy != 0) {
			Direction d = Directions.get(dxs, dys);
			if (d.conflict(pos) != 0) {
				System.out.println("Fail on dx " + dx + " dy " + dy);
				fail = true;
				break;
				//Failure
			}
			dirs.add(d);
			
			dx -= d.dx;
			dy -= d.dy;
			
			pos = pos.add(d.dx, d.dy);
			
			if (dx == 0) dxs = 0;
			if (dy == 0) dys = 0;
		}
		
		System.out.println("Dx " + dx + ", dy " + dy);
		
		if (fail == false) {
			//Dirs now leads us onto the enemy's tile.
			Direction d = dirs.remove(dirs.size() - 1); //Take the last direction.
			if (d instanceof ComplexDirection) {
				fail = true; //We may have failed
				//We want a simple direction.
				for (SimpleDirection dir : Directions.SIMPLE) {
					if (pos.add(dir.dx, dir.dy).equals(best) && dir.conflict(pos) == 0) {
						//Success
						dirs.add(dir);
						fail = false; //We did not fail
						break;
					}
				}
			}
		}
		
		Path path = new Path(dirs);
		if (fail) path.setFailed(fail);
		
		return path;
	}
	
	private Position getClosest(Position from, Position min, Position max) {
		int x, y;
		if (from.x < min.x) x = min.x;
		else if (from.x > max.x) x = max.x;
		else x = from.x; //Our X doesn't need changing
		
		if (from.y < min.y) y = min.y;
		else if (from.y > max.y) y = max.y;
		else y = from.y; //Our Y doesn't need changing
		
		return new Position(x, y);
	}
}