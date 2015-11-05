package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class DialogueHandler implements PacketProcessor<Player> {
	public static final int OPCODE = 4;
	
	@Override
	public void process(Player p, RSIncomingPacket in) throws Exception {
		in.readShort(); //Appears to be -129? Possibly some boolean?
		int buttonId = in.readLEShort();
		int interfaceId = in.readLEShort();
		
		Interface iface = p.getWindow().getInterface(interfaceId);
		if (iface == null || iface.isVisible() == false) {
			p.getCheats().log(5, "Player attempted to interact with dialogue interface " + interfaceId + " but interface not found.");
			return;
		}
		
		Log.debug("Player clicked button. Opcode: " + in.getOpcode() + "(Option " + 0 + ")" + ", ChildId: " + interfaceId + ", buttonId: " + buttonId + ", slot: " + -1 + ", itemId: " + -1);
		Log.debug("Interface: " + iface);
		iface.onClick(0, buttonId, -1, -1);
	}
}