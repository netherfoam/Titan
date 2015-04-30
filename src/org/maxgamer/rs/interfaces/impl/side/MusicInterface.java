package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class MusicInterface extends SideInterface {
	public MusicInterface(Player p) {
		super(p, (short) 187, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 216 : 101));
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		
	}
}
