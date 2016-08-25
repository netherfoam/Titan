package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.PacketProcessor;

/**
 * @author netherfoam
 */
public class ItemMoveHandler implements PacketProcessor<Player> {
	public static final int PACKET_ID = 10;
	
	@Override
	public void process(Player p, RSIncomingPacket in) throws Exception {
		//TODO: This is still very incomplete
		//TODO: Handle this in the interface
		int fromInterfaceId = in.readShort();
		@SuppressWarnings("unused")
		int fromWindowId = in.readShort();
		
		int toItemId = in.readShortA();
		int fromItemId = in.readShort();
		
		//TODO: May have to be swapped around a bit
		int toWindowId = in.readLEShort();
		int toInterfaceId = in.readLEShort();
		
		int tabId = toWindowId & 0xFF;
		int fromSlot = in.readLEShortA();
		int toSlot = in.readLEShort();
		
		Interface from = p.getWindow().getInterface(fromInterfaceId);
		if (from == null) {
			p.getCheats().log(2, "Player attempted to drag an item from interface " + fromInterfaceId + " to interface " + toInterfaceId + ", but the from interface isn't open to the player.");
			return;
		}
		
		Interface to = p.getWindow().getInterface(toInterfaceId);
		if (to == null) {
			p.getCheats().log(2, "Player attempted to drag an item from interface " + fromInterfaceId + " to interface " + toInterfaceId + ", but the to interface isn't open to the player.");
			return;
		}
		
		to.onDrag(from, fromItemId, toItemId, tabId, fromSlot, toSlot);
	}
	
}