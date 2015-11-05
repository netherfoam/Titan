package org.maxgamer.rs.interfaces.impl;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class PrayerOrbInterface extends Interface {
	public static final int CHILD_ID = 749;
	
	public PrayerOrbInterface(Player p) {
		super(p, p.getWindow(), (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 184 : 175), false);
		this.setChildId(CHILD_ID);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return false;
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (option) {
			case 0:
				player.getPrayer().switchQuickPrayers();
				break;
			case 1:
				player.getPrayer().setQuickPrayerEditing(player.getPrayer().isSelectingQuickPrayers() ? false : true);
				break;
		}
	}
}
