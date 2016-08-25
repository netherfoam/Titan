package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class InterfaceComponentOnInterfaceComponentHandler implements PacketProcessor<Player> {
	public static final int ON_USE = 3;
	
	@Override
	public void process(Player p, RSIncomingPacket in) throws Exception {
		int toInterface = in.readShort();
		int toButtonId = in.readShort();
		int fromSlot = in.readShort();
		int toItemId = in.readLEShort();
		int toSlot = in.readShort();
		int fromInterface = in.readShort();
		int fromButtonId = in.readShort();
		int fromItemId = in.readLEShortA();
		
		// Pane from = p.getPanes().getPane(fromInterface);
		// Pane to = p.getPanes().getPane(toInterface);
		
		Interface from = p.getWindow().getInterface(fromInterface);
		Interface to = p.getWindow().getInterface(toInterface);
		from.onUse(to, fromButtonId, fromItemId, fromSlot, toButtonId, toItemId, toSlot);
	}
}