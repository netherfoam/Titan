package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class UnknownHandler implements PacketProcessor<Player> {
	@Override
	public void process(Player c, RSIncomingPacket p) throws Exception {
		//Opcode 23, 1 short. Appears to be 0x00 00 all the time.
	}
}