package org.maxgamer.rs.model.interfaces;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class SideInterface extends Interface {
	public SideInterface(Player p, short childPos) {
		super(p, p.getWindow(), childPos, true);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
}