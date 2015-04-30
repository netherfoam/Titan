package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.map.path.Path;

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
	public boolean run() {
		MovementUpdate m = getOwner().getUpdateMask().getMovement();
		if (m.hasTeleported()) {
			//Can't move while teleporting
			return false;
		}
		
		if (m.hasChanged()) {
			//TODO: This is still occasionally triggered, 27/01/2015, Netherfoam
			return false;
			//throw new IllegalStateException("Movement update mask has already changed dir " + m.getDirection() + ", tele " + m.hasTeleported() + ", ActionQueue: " + getOwner().getActions().toString());
		}
		if (getOwner().move(this.path)) {
			//We've reached our destination. This may make the server seem more responsive
			//by yielding to the next action, though the player will always appear to interact
			//before they get there. For now, we do not yield here.
			return true;
		}
		else {
			return false;
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