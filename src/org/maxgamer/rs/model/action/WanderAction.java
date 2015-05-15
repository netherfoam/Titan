package org.maxgamer.rs.model.action;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;
import org.maxgamer.rs.structure.timings.StopWatch;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * Represents when a mob wanders to a random nearby location. This does not
 * force the mob to walk. This action will never make the mob leave the radius.
 * Once a walk has been performed, this action generates a new path of the same
 * parameters in the original radius. This task is infinite and does not end,
 * but is cancellable. It will insert a walk task before this task repeatedly.
 * @author netherfoam
 */
public class WanderAction extends Action {
	private Location center;
	private int radius;
	private int minWait;
	private int maxWait;
	
	public WanderAction(Mob mob, Location center, int radius, int minWait, int maxWait) {
		super(mob);
		if (radius <= 0) {
			throw new IllegalArgumentException("A wander radius of " + radius + " is invalid. It must be > 0");
		}
		
		this.center = center;
		this.radius = radius;
		this.minWait = minWait;
		this.maxWait = maxWait;
	}
	
	public WanderAction(Mob mob, Location center, int radius) {
		this(mob, center, radius, 10, 30);
	}
	
	/**
	 * Finds an acceptable path from the mob's current location to a random
	 * location within the radius. This path is guaranteed not to leave the
	 * radius.
	 * @return the path not null, possibly empty under extreme circumstances
	 */
	private Path doPath() {
		//Move them to a random nearby tile within a 13x13 area with them at the center. Make sure it is reachable.
		int dx = Erratic.nextInt(-radius, radius);
		int dy = Erratic.nextInt(-radius, radius);
		PathFinder finder = new AStar(radius);
		Path p = finder.findPath(getOwner().getLocation(), center.add(dx, dy), center.add(dx, dy), getOwner().getSizeX(), getOwner().getSizeY());
		
		dx = 0;
		dy = 0;
		
		//Now ensure our path doesn't walk outside the zone. If it does, trim off
		//any parts that would cause us to walk out of the zone.
		for (int i = 0; i < p.size(); i++) {
			Direction d = p.get(i);
			dx += d.dx;
			dy += d.dy;
			
			if (dx >= radius || dx <= -radius || dy >= radius || dy <= -radius) {
				//So our path takes us out of our required zone.
				while (p.size() >= i) {
					//So delete any parts (including the last direction we checked)
					//of the path outside of the radius.
					p.removeLast();
				}
			}
		}
		
		return p;
	}
	
	@Override
	protected void run() throws SuspendExecution {
		//Notes:
		// - If this method is called, then we have no walk in the actions queue.
		// - Walking does not call yield(), it finishes though.
		/*
		if (pause > 0) {
			//We're waiting before wandering again
			pause--;
			yield();
			return false;
		}
		
		StopWatch timer = Core.getTimings().start("npc-wander-pathing");
		WalkAction walk = new WalkAction(getOwner(), doPath());
		getOwner().getActions().insertBefore(this, walk);
		timer.stop();
		
		//Time we wait *after* the walk we just queued.
		pause = Erratic.nextInt(minWait, maxWait);
		
		return false;*/
		int pause = Erratic.nextInt(minWait, maxWait);
		while(true){
			if(pause > 0) {
				yield();
				pause--;
			}
			else{
				StopWatch timer = Core.getTimings().start("npc-wander-pathing");
				WalkAction walk = new WalkAction(getOwner(), doPath());
				getOwner().getActions().insertBefore(this, walk);
				timer.stop();
			}
			wait(1);
		}
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
	
}