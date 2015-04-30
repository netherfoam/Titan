package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class ChatSettings extends SideInterface {
	
	public ChatSettings(Player p) {
		super(p, (short) (982), (short) 99);
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		if (buttonId == 5) {//Close
			getPlayer().getWindow().close(this);
			getPlayer().getWindow().open(new SettingsInterface(player));
		}
		else if (buttonId == 37) {//Split private chat 
			if (player.getConfig().getBoolean("privatechat.split", true) == true) {
				player.getConfig().set("privatechat.split", false);
				player.getConfig().set("privatechat.colour", 1);
				player.getProtocol().sendConfig(287, player.getConfig().getInt("privatechat.colour", 1));
			}
			else {
				player.getConfig().set("privatechat.split", true);
				player.getConfig().set("privatechat.colour", player.getConfig().getInt("privatechat.colour", 1));
			}
		}
		else if (buttonId >= 13 && buttonId <= 32) {//Clan chat colour
			int colour = buttonId - 13;
			player.getConfig().set("clanchat.colour", colour);
			player.getProtocol().sendConfig(1438, colour);
		}
		else if (buttonId >= 45 && buttonId <= 62) {//Private chat colour
			int colour = buttonId - 44;
			player.getConfig().set("privatechat.split", false);
			player.getConfig().set("privatechat.colour", colour);
			player.getProtocol().sendConfig(287, colour);
		}
	}
}