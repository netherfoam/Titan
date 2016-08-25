package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.ScreenSettings;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class PaneSwitchHandler implements PacketProcessor<Player> {
	@Override
	public void process(Player c, RSIncomingPacket p) throws Exception {
		//Opcode 7, 5 bytes
		int mode = p.readByte();
		int width = p.readShort();
		int height = p.readShort();
		
		ScreenSettings s = c.getSession().getScreenSettings();
		s.setDisplayMode(mode);
		s.setWidth(width);
		s.setHeight(height);
	}
}