package org.maxgamer.rs.model.interfaces.impl;

import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class RunInterface extends Interface {
	
	public RunInterface(Player p) {
		super(p, p.getWindow(), (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 185 : 176), true);
		setChildId(750);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		if (buttonId == 1) {
			if (option == 0) { //Toggle
				getPlayer().setRunning(!getPlayer().isRunning());
			}
			else if (option == 1) { //Rest
				//TODO
			}
		}
	}
}