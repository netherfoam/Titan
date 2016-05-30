package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.impl.chat.GESearchInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class GrandExchangeHandler implements PacketProcessor<Player> {
	public static final int OPCODE = 19;
	
	@Override
	public void process(Player p, RSIncomingPacket in) throws Exception {
		int id = in.readShort() & 0xFFFF;
		Interface iface = p.getWindow().getInterface(GESearchInterface.INTERFACE_ID);
		if (iface instanceof GESearchInterface) {
			GESearchInterface search = (GESearchInterface) iface;
			try {
				ItemStack item = ItemStack.create(id);
				search.onSelect(item);
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}