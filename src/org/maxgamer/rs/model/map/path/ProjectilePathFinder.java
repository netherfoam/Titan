package org.maxgamer.rs.model.map.path;

import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public class ProjectilePathFinder implements PathFinder {
	@Override
	public Path findPath(Location start, Position min, Position max, int xSize, int ySize) {
		if (min.equals(max) == false) {
			throw new IllegalArgumentException("Projectiles must go to one tile");
		}
		if (xSize != 1 || ySize != 1) {
			throw new IllegalArgumentException("XSize and YSize for projectile must be 1");
		}
		
		//We do not actually add waypoints to the path, as they do not follow the strict tile
		//paths, which a Path object conforms to.
		Path path = new Path();
		
		if (start.x == min.x && start.y == min.y) {
			//Shooting from a tile to itself.
			return path;
		}
		
		//The number of tiles the projectile is moving along the X and Y axis
		double dx = min.x - start.x;
		double dy = min.y - start.y;
		
		//The directions that this projectile is travelling. Could be just one (EG directly north)
		//Or a combination of two (Eg north and north_east if the projectile is between north and
		//north_east)
		Direction[] dirs;
		
		double absdx = Math.abs(dx);
		double absdy = Math.abs(dy);
		
		double rx;
		double ry;
		
		if (absdx > absdy) {
			rx = dx / absdx;
			ry = dy / absdx;
		}
		else {
			rx = dx / absdy;
			ry = dy / absdy;
		}
		
		//Given the above code, 
		//If rx is not an integer, then ry is.
		//If ry is not an integer, then rx is.
		//Both may be an integer.
		//Both may not be a decimal at the same time.
		if (rx != (int) rx) {
			dirs = new Direction[2];
			dirs[0] = Directions.get((int) Calc.between(-1, 1, rx), (int) ry);
			dirs[1] = Directions.get(0, (int) ry);
		}
		else if (ry != (int) ry) {
			//If rx == (int) rx, this can never occur.
			dirs = new Direction[2];
			dirs[0] = Directions.get((int) rx, (int) Calc.between(-1, 1, ry));
			dirs[1] = Directions.get((int) rx, 0);
		}
		else {
			//both are valid integers, we only have one cardinal direction.
			dirs = new Direction[1];
			dirs[0] = Directions.get((int) rx, (int) ry);
		}
		
		if (absdy >= absdx) {
			double tx = start.x;
			int ty = start.y;
			
			while (ty != min.y) {
				Location l = new Location(start.getMap(), (int) tx, ty, start.z);
				for (Direction d : dirs) {
					if (d.canShoot(l) == false) {
						path.setFailed(true);
						return path;
					}
				}
				
				//For each step:
				//Increment ty by 1
				//Increment tx by dx / dy
				tx += dx / absdy;
				ty += (int) Calc.between(-1, 1, dy);
			}
		}
		else {
			int tx = start.x;
			double ty = start.y;
			
			while (tx != min.x) {
				Location l = new Location(start.getMap(), tx, (int) ty, start.z);
				for (Direction d : dirs) {
					if (d.canShoot(l) == false) {
						path.setFailed(true);
						return path;
					}
				}
				
				//For each step:
				//Increment ty by dy / dx
				//Increment tx by 1
				ty += dy / absdx;
				tx += (int) Calc.between(-1, 1, dx);
			}
		}
		//The path will always be empty.
		return path;
	}
}