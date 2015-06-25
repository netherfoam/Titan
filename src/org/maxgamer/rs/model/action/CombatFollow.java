package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;
import org.maxgamer.rs.model.map.path.ProjectilePathFinder;

/**
 * A class to handle one mob following another.
 * @author netherfoam
 */
public class CombatFollow extends Follow {
	private int prefDistance;
	/**
	 * Constructs a new Follow object.
	 * @param owner the mob who is following another
	 * @throws NullPointerException if the owner is null
	 */
	public CombatFollow(Mob owner, Mob target, int prefDistance, int breakDistance, PathFinder pather) {
		super(owner, target, pather);
		if (prefDistance > breakDistance) throw new IllegalArgumentException("Preferred distance must be <= breakDistance");
		if (prefDistance <= 0 || breakDistance <= 0) throw new IllegalArgumentException("PrefDistance and BreakDistance must be > 0");
		if (pather == null) throw new NullPointerException("Pather may not be null");
		this.prefDistance = prefDistance;
	}

	@Override
	public int getBreakDistance() {
		return 10;
	}

	@Override
	public boolean isSatisfied() {
		if(getOwner().getLocation().withinRange(getTarget().getLocation(), prefDistance) == false){
			return false;
		}
		
		Mob t = getTarget();
		Mob m = getOwner();
		PathFinder finder = new ProjectilePathFinder();
		Path path = finder.findPath(m.getLocation(), t.getLocation(), t.getLocation(), m.getSizeX(), m.getSizeY());
		
		if(path.hasFailed()){
			return false;
		}
		return true;
	}

	@Override
	public void onWait() {
		yield();
	}
}