package org.maxgamer.rs.network.io.packet;

import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.network.Client;

/**
 * @author netherfoam
 */
public class PacketManager<T extends Client> {
	@SuppressWarnings("unchecked")
	private PacketProcessor<T>[] processors = new PacketProcessor[256]; //There are only 256 opcodes, this is hard-capped by sizeof(byte).
	
	public void setHandler(int opcode, PacketProcessor<T> handler) {
		processors[opcode] = handler;
	}
	
	public PacketProcessor<T> getHandler(int opcode) {
		return processors[opcode];
	}
	
	public boolean handle(T c, RSIncomingPacket p) {
		int opcode = p.getOpcode();
		PacketProcessor<T> handler = getHandler(opcode);
		if (handler == null) return false;
		
		try {
			handler.process(c, p);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.warning("Packet handler failed to handle packet correctly.");
		}
		
		return true; //We tried to handle it
	}
}