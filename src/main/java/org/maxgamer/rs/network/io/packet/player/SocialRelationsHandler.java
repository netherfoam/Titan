package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.interfaces.impl.chat.StringInputInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.FriendsList;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class SocialRelationsHandler implements PacketProcessor<Player> {
	
	public static final int ADDING_FRIEND = 2;
	public static final int REMOVING_FRIEND = 77;
	public static final int ADDING_IGNORE = 74;
	public static final int REMOVING_IGNORE = 20;
	
	@Override
	public void process(Player player, final RSIncomingPacket in) throws Exception {
		
		//Get the interface for inputting a string that the player has open
		StringInputInterface interf = (StringInputInterface) player.getWindow().getInterface(StringInputInterface.CHILD_ID);
		
		final String msg = in.readPJStr1();
		if (interf == null) { //TODO: Check that the Friends or ignores tab is open. If not, cheat log it!
		
			// The player currently doesn't have an input interface open, but has the friends/ignores tab open.
			// This can be caused naturally by the client when a player right clicks on a friend/ignore's name
			// and selects 'Delete'.
			interf = new StringInputInterface(player) {
				@Override
				public void onInput(String value) {
					FriendsList f = getPlayer().getFriends();
					switch (in.getOpcode()) {
						case REMOVING_FRIEND:
							if (f.removeFriend(msg, false) == false) {
								getPlayer().getCheats().log(1, "Attempted to right click -> Delete friend which they didn't have");
							}
							break;
						case REMOVING_IGNORE:
							if (f.removeIgnore(msg, false) == false) {
								getPlayer().getCheats().log(1, "Attempted to right click -> Delete ignore which they didn't have");
							}
							break;
					}
				}
			};
		}
		
		interf.onInput(msg);
		player.getWindow().close(interf);
	}
}
