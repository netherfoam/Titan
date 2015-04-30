package org.maxgamer.rs.interfaces.impl.dialogue;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class ChatInterface extends Interface {
	public static final short CHILD_ID = (short) 752;
	
	public ChatInterface(Player p) {
		super(p, p.getWindow(), CHILD_ID, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 192 : 69), true);
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
		
	}
}