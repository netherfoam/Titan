package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class MusicRequestHandler implements PacketProcessor<Player> {
	@Override
	public void process(Player c, RSIncomingPacket p) throws Exception {
		// Opcode 42, 4 bytes
		// songId = p.readInt();
		System.out.println(p.readInt());
	}
}