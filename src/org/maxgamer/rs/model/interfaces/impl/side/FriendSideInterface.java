package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.model.interfaces.impl.chat.StringInputInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class FriendSideInterface extends SideInterface {
	public static final int BUTTON_ADD = 12;
	public static final int BUTTON_DELETE = 13;
	public static final int INTERFACE_ID = 550;
	
	public FriendSideInterface(Player p) {
		//220 or 105 
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 211 : 96));
		setChildId(INTERFACE_ID);
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case BUTTON_ADD:
				getPlayer().getWindow().open(new StringInputInterface(getPlayer()) {
					
					@Override
					public void onInput(String friend) {
						if (getPlayer().getFriends().isFriend(friend)) {
							getPlayer().getCheats().log(1, "Player attempted to add friend " + friend + " but they already are. The client should not allow this unless modified.");
							return;
						}
						getPlayer().getFriends().addFriend(friend);
					}
				});
				break;
			case BUTTON_DELETE:
				getPlayer().getWindow().open(new StringInputInterface(getPlayer()) {
					
					@Override
					public void onInput(String friend) {
						if (getPlayer().getFriends().removeFriend(friend, false) == false) {
							getPlayer().sendMessage(friend + " is not your friend.");
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