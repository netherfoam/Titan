package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class WorldMapHandler implements PacketProcessor<Player> {
	public static final int OPCODE = 49;
	
	@Override
	public void process(Player c, RSIncomingPacket in) throws Exception {
		int val = in.readInt();
		
		Log.debug("Val: " + String.format("0x%X", val));
		int x = val & 0x3FFF;
		int y = (val >> 14) & 0x3FFF;
		int z = val >> 28; //Well it's definitely not Z
		Log.debug("X: " + x + ", Y: " + y + ", Z: " + z);
		//Log.debug("Z: " + (val >> 28) + ", X: " + ((val >> 14) & 0x3FFF) + ", Y: " + (val & 0x3FFF));
	}
	
}