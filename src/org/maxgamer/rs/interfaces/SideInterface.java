package org.maxgamer.rs.interfaces;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class SideInterface extends Interface {
	public SideInterface(Player p, short childId, short childPos) {
		super(p, p.getWindow(), childId, childPos, true);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
}