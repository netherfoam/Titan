package org.maxgamer.rs.model.entity.mob.facing;

import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public class PositionFacing extends Facing {
	/**
	 * The current position this mob is facing. If facePosChanged is false, this
	 * should be ignored. If it is true, then an update must be sent to the
	 * players.
	 */
	private Position target;
	
	public PositionFacing(Position p) {
		if (p == null) {
			throw new NullPointerException("Position may not be null");
		}
		this.target = p;
	}
	
	/**
	 * The position this mob will be set to face after the next update. It will
	 * be null if the mob is CURRENTLY facing a position with no update set.
	 * @return The position this mob will be set to face after the next update
	 */
	public Position getTarget() {
		return target;
	}
	
	@Override
	public Position getPosition() {
		return target;
	}
}