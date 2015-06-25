package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.map.path.Path;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class WalkAction extends Action {
	private Path path;
	
	public WalkAction(Mob mob, Path path) {
		super(mob);
		this.path = path;
	}
	
	/**
	 * Walks the next phase of the path. This should be called once per tick for
	 * smooth walking.
	 * @return true if the path was completed (or cancelled by event), false if
	 *         path is incomplete.
	 */
	@Override
	public void run() throws SuspendExecution {
		MovementUpdate m = getOwner().getUpdateMask().getMovement();
		
		boolean done = false;
		while(!done){
			if (m.hasTeleported()) {
				//Can't move while teleporting
				wait(1);
				continue;
			}
			
			if (m.hasChanged()) {
				//TODO: This is still occasionally triggered, 27/01/2015, Netherfoam
				//throw new IllegalStateException("Movement update mask has already changed dir " + m.getDirection() + ", tele " + m.hasTeleported() + ", ActionQueue: " + getOwner().getActions().toString());
				wait(1);
				continue;
			}
			
			if (getOwner().isRooted()) {
				//Rooted mobs may not move.
				wait(1);
				continue;
			}
			
			done = getOwner().move(this.path);
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