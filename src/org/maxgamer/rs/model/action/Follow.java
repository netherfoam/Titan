package org.maxgamer.rs.model.action;

import java.lang.ref.WeakReference;

import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * A class to handle one mob following another.
 * @author netherfoam
 */
public abstract class Follow extends Action {
	/**
	 * The mob who is being folloewd
	 */
	private WeakReference<Mob> target = new WeakReference<>(null);
	private PathFinder pathFinder = new AStar(8);
	
	/**
	 * Constructs a new Follow object.
	 * @param owner the mob who is following another
	 * @throws NullPointerException if the owner is null
	 */
	public Follow(Mob owner, Mob target, PathFinder finder) {
		super(owner);
		if (owner == null) throw new NullPointerException("Follow owner may not be null");
		if (target == null) throw new NullPointerException("Target may not be null");
		
		this.target = new WeakReference<Mob>(target);
		this.pathFinder = finder;
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
	public abstract int getBreakDistance();
	
	/**
	 * Called each tick that the follow is active. If the current location is
	 * desirable, then the follow will not continue to move until it is not. This
	 * method is used to check if a current location is desired, eg. It returns
	 * true if the owner is shooting at a target, and there is a line of sight
	 * path between the owner and the target.
	 * 
	 * @return true if the owner should move if possible, false if the owner shouldn't
	 * 		   move for this tick.
	 */
	public abstract boolean isSatisfied();
	
	/**
	 * Called when the follow is still active, but there is no work
	 * to be done, eg. the target has been reached.
	 */
	public abstract void onWait();
	
	/**
	 * Returns true if this follow is currently valid.
	 * @return true if this follow is valid, false otherwise.
	 */
	public boolean isFollowing() {
		if (getTarget() == null || getTarget().isDestroyed()) return false;
		if (getOwner().isDestroyed()) return false;
		
		Location d = getTarget().getLocation();
		
		if (d.withinRange(getOwner().getLocation(), getBreakDistance()) == false) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected final void run() throws SuspendExecution{
		Mob mob = getOwner();
		MovementUpdate m = mob.getUpdateMask().getMovement();
		Path path = null;
		
		getOwner().setFacing(Facing.face(getTarget()));
		
		while(isFollowing()){
			if(isSatisfied() == false){
				//The mob wants to get closer to their target!
				
				if (getOwner().getLocation().equals(getTarget().getLocation())) {
					//Whoops, we're ontop of our target!
					Direction[] dirs = Directions.ALL;
					int r = Erratic.nextInt(0, dirs.length - 1);
					
					//Finds a random walk direction to move in.
					for (int i = 0; i < dirs.length; i++) {
						Direction d = dirs[(i + r) % dirs.length];
						if (d.conflict(getOwner().getLocation()) == 0) {
							path = new Path();
							path.addFirst(d);
						}
					}
					
					//Stuck on spot but still retry
					wait(1);
					continue;
				}
				else{
					if(path == null || path.isEmpty()){
						//Plan a new path to the target
						Location dest = getTarget().getLocation();
						
						path = pathFinder.findPath(mob.getLocation(), dest, dest, mob.getSizeX(), mob.getSizeY());
						if (path.hasFailed() == false && path.isEmpty() == false) {
							path.removeLast();
						}
					}
				}
				
				if (path == null || path.isEmpty() || m.hasTeleported() || m.hasChanged() || getOwner().isRooted()) {
					//Mob can't or won't move
					this.onWait();
					wait(1);
					continue;
				}
			
				getOwner().move(path);
			}
			else{
				this.onWait();
			}
			
			wait(1);
		}
		getOwner().setFacing(null);
	}
	
	@Override
	protected void onCancel() {
		//No longer stalking them.
		getOwner().setFacing(null);
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}