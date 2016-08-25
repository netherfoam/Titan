package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class LocaleHandler implements PacketProcessor<Player> {
	@Override
	public void process(Player c, RSIncomingPacket p) throws Exception {
		//Opcode 59, 1 short
		//This is only sent when a player enters the world, and the
		//packet is stored in the same class as a bunch of Locale messages
		//in the client source.  Assumably, it is something to do with
		//locale. In my client, the short is always 0x00 0x1A
	}
}