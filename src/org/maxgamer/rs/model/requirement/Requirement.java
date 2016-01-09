package org.maxgamer.rs.model.requirement;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author Albert Beaupre
 */
public interface Requirement {
	
	/**
	 * Returns true if the specified {@code mob} passes this {@code Requirement}
	 * . If the specified {@code mob} does not pass this {@code Requirement},
	 * return false.
	 * 
	 * @param mob the mob to check for passing
	 * @return true if the mob passes; return false otherwise
	 */
	boolean passes(Mob mob);
	
}
