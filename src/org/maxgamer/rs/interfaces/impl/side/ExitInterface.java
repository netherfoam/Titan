package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.events.mob.persona.player.PlayerLogOutEvent;
import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class ExitInterface extends SideInterface {
	public static final int BUTTON_LOBBY = 5;
	public static final int BUTTON_LOGIN = 10;
	
	public ExitInterface(Player p) {
		//220 or 105 
		super(p, (short) 182, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 220 : 105));
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		PlayerLogOutEvent e;
		switch (buttonId) {
			case BUTTON_LOBBY:
				e = new PlayerLogOutEvent(getPlayer());
				e.call();
				if (e.isCancelled()) {
					return;
				}
				
				player.getProtocol().logout(true);
				player.destroy();
				break;
			case BUTTON_LOGIN:
				e = new PlayerLogOutEvent(getPlayer());
				e.call();
				if (e.isCancelled()) {
					return;
				}
				
				player.getProtocol().logout(false);
				player.destroy();
				break;
		}
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
}