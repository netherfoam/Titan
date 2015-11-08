package org.maxgamer.rs.interfaces;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class PrimaryInterface extends Interface {
	public PrimaryInterface(Player p) {
		//548/746 is the id of the main frame.
		//18/9 is the id of the position that a primary interface takes up.
		//super(p, true, new short[]{548, 746}, new int[]{18, 9}, childId);
		super(p, p.getWindow(), (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 18 : 9), true);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return false;
	}
}