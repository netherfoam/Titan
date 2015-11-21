package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class PlayerOptionsHandler implements PacketProcessor<Player> {
	public static final int FIRST_OPTION = 70;
	public static final int SECOND_OPTION = 80;
	public static final int THIRD_OPTION = 27;
	public static final int FOURTH_OPTION = 47;
	public static final int FIFTH_OPTION = 64;
	public static final int SIXTH_OPTION = 8;
	public static final int SEVENTH_OPTION = 68;
	public static final int EIGHTH_OPTION = 53;
	
	@Override
	public void process(Player player, RSIncomingPacket packet) throws Exception {
		Persona target = null;
		int option = -1;
		int index;
		switch (packet.getOpcode()) {
			case FIRST_OPTION:
				index = packet.readLEShort() - 1;
				packet.readByteS(); //Unknown.
				option = 1;
				break;
			case SECOND_OPTION:
				index = packet.readLEShortA() - 1;
				packet.readByte(); //Unknown. is value + 128.
				option = 2;
				break;
			case THIRD_OPTION:
				index = packet.readShort() - 1;
				packet.readByte(); //Unknown.
				option = 3;
				break;
			case FOURTH_OPTION:
				packet.readByteS(); //Unknown.
				index = packet.readShort() - 1;
				option = 4;
				break;
			case FIFTH_OPTION:
				index = packet.readLEShortA() - 1;
				packet.readByte(); //Unknown. is value + 128.
				option = 5;
				break;
			case SIXTH_OPTION:
				packet.readByteC(); //Unknown. Some kind of boolean.
				index = packet.readShort() - 1;
				option = 6;
				break;
			case SEVENTH_OPTION:
				packet.readByteS(); //Unknown.
				index = packet.readShort() - 1;
				option = 7;
				break;
			case EIGHTH_OPTION:
				index = packet.readShortA() - 1;
				packet.readByteC(); //Unknown. Some kind of boolean.
				option = 8;
				break;
			default:
				return;
		}
		target = Core.getServer().getPersonas().get(index);
		if (target == null) {
			player.getCheats().log(5, "Player attempted to interact with a NULL player");
		}
		
		if (player.getProtocol().isVisible(target) == false) {
			player.getCheats().log(30, "Player attempted to interact with a Persona that wasn't on screen. ID: " + target.getName());
			return;
		}
		
		player.use(target, player.getPersonaOptions().get(option));
	}
}