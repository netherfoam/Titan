package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class SettingsInterface extends SideInterface {
	
	public SettingsInterface(Player p) {
		super(p, (short) 261, (short) 99);
		player.getProtocol().sendConfig(170, player.getConfig().getInt("mouse.buttons", 0));//Mouse buttons
		player.getProtocol().sendConfig(427, player.getConfig().getBoolean("accept.aid", false) ? 0 : 1);//Accept aid
		player.getProtocol().sendConfig(171, player.getConfig().getBoolean("chat.effects", true) ? 0 : 1);//Chat effects
		
		if (!player.getConfig().getBoolean("privatechat.split", true)) {//Sending this here, because otherwise it won't load before the player has click chatsettings.
			player.getProtocol().sendConfig(287, player.getConfig().getInt("privatechat.colour", 1));//Private chat colour
		}
		
		//TODO: The value for 1438 has to be the combination of all 1438 values. See also: ChatSettings.java onClick() and EmotesInterface.java (Constructor)
		//It cannot be sent like this, as the value is simply overridden when we send the next value. We need to combine ALL of the values that modify config 1438,
		//and THEN send that combination - eg. player.getProtocol().sendConfig(1438, combination); 
		//Where combination = (3612 << (filter ? 4 : 3)) | (clanChatColor) | (taskMasterEmote??)
		player.getProtocol().sendConfig(1438, player.getConfig().getInt("clanchat.colour", 0));//Clan chat colour
		player.getProtocol().sendConfig(1438, (player.getConfig().getBoolean("profanity.filter", false) == false ? (3612 << 3) : (3612 << 4)));//Profanity filter
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 3://Profanity Filter TODO: fix since somehow it'll still be default on login. 
				if (player.getConfig().getBoolean("profanity.filter", false) == true) player.getConfig().set("profanity.filter", false);
				else player.getConfig().set("profanity.filter", true);
				player.getProtocol().sendConfig(1438, player.getConfig().getBoolean("profanity.filter") == false ? 3612 << 3 : 3612 << 4);
				break;
			case 4://Chat effects
				if (player.getConfig().getBoolean("chat.effects", true) == true) player.getConfig().set("chat.effects", false);
				else player.getConfig().set("chat.effects", true);
				player.getProtocol().sendConfig(171, player.getConfig().getBoolean("chat.effects") ? 0 : 1);
				break;
			case 5://Chat settings
				player.getWindow().open(new ChatSettings(player));
				break;
			case 6://Mouse button
				if (player.getConfig().getInt("mouse.buttons", 0) == 0) player.getConfig().set("mouse.buttons", 1);
				else player.getConfig().set("mouse.buttons", 0);
				player.getProtocol().sendConfig(170, player.getConfig().getInt("mouse.buttons", 0));
				break;
			case 7://Accept aid ON/OFF
				if (!player.getConfig().getBoolean("accept.aid", false)) player.getConfig().set("accept.aid", true);
				else player.getConfig().set("accept.aid", false);
				player.getProtocol().sendConfig(427, player.getConfig().getBoolean("accept.aid") ? 0 : 1);
				break;
			case 8://House Building Options 
				player.getWindow().open(new HouseOptionsInterface(player));
				break;
			case 14://Graphics interface
				player.getWindow().open(new GraphicsInterface(player));
				break;
			case 16://Sound interface
				player.getWindow().open(new SoundInterface(player));
				break;
		}
		
	}
	
}
