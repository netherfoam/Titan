package org.maxgamer.rs.model.action;

import java.lang.ref.WeakReference;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;

/**
 * A class to handle one mob following another.
 * @author netherfoam
 */
public abstract class Follow extends Action {
	/**
	 * The mob who is being folloewd
	 */
	private WeakReference<Mob> target = new WeakReference<>(null);
	
	/**
	 * The maximum follow distance, 1 to n
	 */
	private int breakDistance = 10;
	
	/**
	 * Constructs a new Follow object.
	 * @param owner the mob who is following another
	 * @throws NullPointerException if the owner is null
	 */
	public Follow(Mob owner, Mob target, int breakDistance) {
		super(owner);
		if (owner == null) throw new NullPointerException("Follow owner may not be null");
		if (target == null) throw new NullPointerException("Target may not be null");
		
		this.target = new WeakReference<Mob>(target);
		this.breakDistance = breakDistance;
	}
	
	/**
	 * The mob which is being followed, may be null
	 * @return The mob which is being followed, may be null
	 */
	public Mob getTarget() {
		return target.get();
	}
	
	/**
	 * The maximum distance, greater than 0.
	 * @return The maximum distance, greater than 0.
	 */
	public int getBreakDistance() {
		return breakDistance;
	}
	
	/**
	 * Returns true if this follow is currently valid.
	 * @return true if this follow is valid, false otherwise.
	 */
	public boolean isFollowing() {
		if (getTarget() == null || getTarget().isDestroyed()) return false;
		if (getOwner().isDestroyed()) return false;
		
		Location d = getTarget().getLocation();
		
		if (d.withinRange(getOwner().getLocation(), breakDistance) == false) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected abstract boolean run();
	
	@Override
	protected void onCancel() {
		//No longer stalking them.
		//getOwner().getUpdateMask().getFacing().setTarget(null);
		getOwner().setFacing(null);
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}