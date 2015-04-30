package org.maxgamer.rs.model.action;

import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;

/**
 * A class to handle one mob following another.
 * @author netherfoam
 */
public class FriendFollow extends Follow {
	private Location lastPos;
	private Location nextPos;
	
	/**
	 * The preferred follow distance, 1 to max.
	 */
	private int prefDistance = 1;
	private PathFinder pathFinder;
	private WalkAction walk;
	private Path path;
	
	/**
	 * Constructs a new Follow object.
	 * @param owner the mob who is following another
	 * @throws NullPointerException if the owner is null
	 */
	public FriendFollow(Mob owner, Mob target, int prefDistance, int breakDistance, PathFinder pather) {
		super(owner, target, breakDistance);
		if (prefDistance > breakDistance) throw new IllegalArgumentException("Preferred distance must be <= breakDistance");
		if (prefDistance <= 0 || breakDistance <= 0) throw new IllegalArgumentException("PrefDistance and BreakDistance must be > 0");
		if (pather == null) throw new NullPointerException("Pather may not be null");
		
		this.prefDistance = prefDistance;
		this.pathFinder = pather;
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
	protected boolean run() {
		if (isFollowing() == false) {
			//getOwner().getUpdateMask().getFacing().setTarget(null);
			getOwner().setFacing(null);
			return true; //We're done following
		}
		
		if (nextPos == null) {
			//We haven't started this following yet
			//getOwner().getUpdateMask().getFacing().setTarget(getTarget());
			getOwner().setFacing(Facing.face(getTarget()));
		}
		
		if (getOwner().getLocation().equals(getTarget().getLocation())) {
			//Whoops, we're ontop of our target!
			Direction[] dirs = Directions.ALL;
			
			int r = Erratic.nextInt(0, dirs.length - 1);
			
			//Finds a random walk direction to move in.
			for (int i = r; i < dirs.length; i++) {
				Direction d = dirs[i];
				if (d.conflict(getOwner().getLocation()) == 0) {
					path = new Path();
					path.addFirst(d);
					walk = new WalkAction(mob, path);
					walk.run();
					return false; //Not done yet.
				}
			}
			//Finds a random walk direction to run in.
			for (int i = 0; i < r; i++) {
				Direction d = dirs[i];
				if (d.conflict(getOwner().getLocation()) == 0) {
					path = new Path();
					path.addFirst(d);
					walk = new WalkAction(mob, path);
					walk.run();
					return false; //Not done yet.
				}
			}
		}
		
		if (prefDistance * prefDistance < getOwner().getLocation().distanceSq(getTarget().getLocation())) {
			//We are further than the desired distance from the target! We should attempt to move closer.
			if (lastPos == null || lastPos.equals(getTarget().getLocation()) == false) {
				//Our previous destination is now invalid.
				lastPos = nextPos;
				
				if (lastPos == null) { //So nextPos is null, this is our first move.
					lastPos = getTarget().getLocation();
				}
				
				path = pathFinder.findPath(getOwner().getLocation(), lastPos, lastPos, getOwner().getDimension(0), getOwner().getDimension(1));
				if (path.hasFailed() == false && path.isEmpty() == false) {
					path.removeLast();
				}
				walk = new WalkAction(mob, path);
			}
			
			//Call returns true if we've reached the destination.
			if (walk.run() && path.hasFailed() == false) {
				yield();
			}
			
			nextPos = getTarget().getLocation();
			return false;
		}
		else {
			//TODO: This is a bit peculiar. We really need to check that we can still reach the
			//target, and for that, the path can't be null.
			if (path == null || path.hasFailed() == false) {
				yield();
			}
		}
		
		return false;
	}
}