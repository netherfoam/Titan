package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class MouseMoveHandler implements PacketProcessor<Player> {
	/**
	 * The mouse move opcode
	 */
	public static final int OPCODE = 17;
	
	@Override
	public void process(Player c, RSIncomingPacket p) throws Exception {
		//Varying size packet. Triggered when the user moves the mouse at intervals or when they stop moving it.
	}
}