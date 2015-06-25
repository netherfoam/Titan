package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.path.PathFinder;

/**
 * A class to handle one mob following another.
 * @author netherfoam
 */
public class FriendFollow extends Follow {
	/**
	 * The preferred follow distance, 1 to max.
	 */
	private int prefDistance = 1;
	private int breakDistance = 10;
	
	/**
	 * Constructs a new Follow object.
	 * @param owner the mob who is following another
	 * @throws NullPointerException if the owner is null
	 */
	public FriendFollow(Mob owner, Mob target, int prefDistance, int breakDistance, PathFinder pather) {
		super(owner, target, pather);
		if (prefDistance > breakDistance) throw new IllegalArgumentException("Preferred distance must be <= breakDistance");
		if (prefDistance <= 0 || breakDistance <= 0) throw new IllegalArgumentException("PrefDistance and BreakDistance must be > 0");
		if (pather == null) throw new NullPointerException("Pather may not be null");
		
		this.prefDistance = prefDistance;
		this.breakDistance = breakDistance;
	}
	
	/**
	 * The preferred number of tiles between this mob and the target. Once this
	 * follow reaches this many times remaining, it will begin yielding to other
	 * Actions, though it will not stop. If the target moves out of this
	 * distance, they will be followed again until this distance is acceptable.
	 * @return the preferred distance between us and the target.
	 */
	public int getPreferredDistance() {
		return prefDistance;
	}

	@Override
	public int getBreakDistance() {
		return this.breakDistance;
	}

	@Override
	public boolean isSatisfied() {
		if(getOwner().getLocation().withinRange(getTarget().getLocation(), prefDistance) == false){
			return false;
		}
		return true;
	}

	@Override
	public void onWait() {
		//Nothing: Don't yield.
	}
}