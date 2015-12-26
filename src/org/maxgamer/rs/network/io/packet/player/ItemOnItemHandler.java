package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

public class ItemOnItemHandler implements PacketProcessor<Player> {

	public static final int OPCODE = 3;

	@SuppressWarnings("unused")
	@Override
	public void process(Player c, RSIncomingPacket in) throws Exception {
		int firstInterface = in.readShort();
		int unknownShort = in.readShort();
		int usingSlot = in.readShort();
		int useWithItem = in.readLEShort();
		int useWithSlot = in.readShort();
		int secondInterface = in.readShort();
		int componentId = in.readShort();
		int usingItem = in.readLEShortA();

		c.use(ItemStack.create(usingItem), ItemStack.create(useWithItem));
	}

}
