package org.maxgamer.rs.model.entity.mob.facing;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public abstract class Facing {
	public static Facing face(Mob m) {
		if (m == null) return null;
		return new MobFacing(m);
	}
	
	public static Facing face(GameObject g) {
		if (g == null) return null;
		return face(g.getCenter());
	}
	
	public static Facing face(Position p) {
		if (p == null) return null;
		return new PositionFacing(p);
	}
	
	public abstract Position getPosition();
}