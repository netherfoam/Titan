package org.maxgamer.rs.model.entity.mob.facing;

import java.lang.ref.WeakReference;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public class MobFacing extends Facing {
	private final WeakReference<Mob> target;
	
	public MobFacing(Mob target) {
		if (target == null) {
			throw new NullPointerException("Target may not be null");
		}
		this.target = new WeakReference<Mob>(target);
	}
	
	public Mob getTarget() {
		if (target == null) {
			return null;
		}
		
		return target.get(); //May still be null
	}
	
	@Override
	public Position getPosition() {
		Mob t = getTarget();
		if (t == null || t.isDestroyed()) return null;
		return t.getCenter();
	}
}