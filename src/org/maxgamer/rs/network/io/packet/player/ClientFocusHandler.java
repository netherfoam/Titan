package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class ClientFocusHandler implements PacketProcessor<Player> {
	@Override
	public void process(Player c, RSIncomingPacket p) throws Exception {
		//Opcode 62, 1 byte
		//Client sends 1 if the game window became active,
		//0 if the screen was moved out of focus. (Not
		//necessarily minimized, though, just ALT+TAB)
		c.getSession().getScreenSettings().setWindowActive(p.readByte() != 0);
	}
}