package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.interfaces.Window;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.persona.player.WindowClickEvent;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class InterfaceHandler implements PacketProcessor<Player> {
	private static final int[] OPCODES = { 6, 13, 0, 15, 46, 67, 82, 39, 73, 58 };
	
	@Override
	public void process(Player p, RSIncomingPacket in) throws Exception {
		int childId = in.readShort();
		int buttonId = in.readShort();
		int slot = in.readLEShortA();
		int itemId = in.readShort();
		
		Window interf = p.getPanes().get(childId);
		
		int option = -1;
		for (int i = 0; i < OPCODES.length; i++) {
			if (in.getOpcode() == OPCODES[i]) {
				option = i;
			}
		}
		
		if (interf == null) {
			//TODO: Raise this severity when most interfaces are completed.
			p.getCheats().log(2, "Player attempted to use an interface they don't appear to have open. Option: " + option + ", ChildId: " + childId + ", buttonId: " + buttonId + ", slot: " + slot + ", itemId: " + itemId);
			return;
		}
		
		if (interf.isOpen() == false) {
			p.getCheats().log(20, "Player attempted to use an interface which is not currently visible (Force-open?). Option: " + option + ", ChildId: " + childId + ", buttonId: " + buttonId + ", slot: " + slot + ", itemId: " + itemId);
			return;
		}
		
		Log.debug("Player clicked button. Opcode: " + in.getOpcode() + "(Option " + option + ")" + ", ChildId: " + childId + ", buttonId: " + buttonId + ", slot: " + slot + ", itemId: " + itemId);
		
		WindowClickEvent e = new WindowClickEvent(p, interf, in.getOpcode(), buttonId, slot, itemId);
		e.call();
		if (e.isCancelled()) {
			return;
		}
		
		interf.onClick(option, buttonId, slot, itemId);
	}
}