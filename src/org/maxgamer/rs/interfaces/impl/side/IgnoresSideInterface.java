package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.interfaces.impl.dialogue.StringInputInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class IgnoresSideInterface extends SideInterface {
	public static final int BUTTON_ADD = 9;
	public static final int BUTTON_DELETE = 10;
	public static final int INTERFACE_ID = 551;
	
	public IgnoresSideInterface(Player p) {
		//220 or 105 
		super(p, (short) INTERFACE_ID, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 212 : 97));
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case BUTTON_ADD:
				getPlayer().getWindow().open(new StringInputInterface(getPlayer()) {
					
					@Override
					public void onInput(String ignore) {
						if (getPlayer().getFriends().isIgnore(ignore)) {
							getPlayer().getCheats().log(1, "Player attempted to ignore " + ignore + " but they already are. The client should not allow this unless modified.");
							return;
						}
						
						if (getPlayer().getFriends().addIgnore(ignore) == false) {
							getPlayer().sendMessage("Ignore list is full!");
							return;
						}
					}
				});
				break;
			case BUTTON_DELETE:
				getPlayer().getWindow().open(new StringInputInterface(getPlayer()) {
					
					@Override
					public void onInput(String ignore) {
						if (getPlayer().getFriends().removeIgnore(ignore, false) == false) {
							getPlayer().sendMessage(ignore + " is not being ignored.");
						}
					}
				});
				break;
		}
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
}