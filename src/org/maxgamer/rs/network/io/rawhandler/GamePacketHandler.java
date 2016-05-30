package org.maxgamer.rs.network.io.rawhandler;

import java.nio.BufferUnderflowException;

import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * @author netherfoam
 */
public class GamePacketHandler extends RawHandler {
	private Player p;
	
	public GamePacketHandler(Session s, Player p) {
		super(s);
		this.p = p;
	}
	
	@Override
	public void handle(RSByteBuffer b) {
		while (b.isEmpty() == false) {
			//Throws IOException if only a partial packet
			//has been received. This means the buffer is
			//reset until more data is available.
			RSIncomingPacket in;
			try {
				in = RSIncomingPacket.parse(b);
			}
			catch (Exception e) {
				throw new BufferUnderflowException();
			}
			
			PacketProcessor<Player> p = this.p.getProtocol().getPacketManager().getHandler(in.getOpcode());
			
			if (p == null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unhandled opcode: " + in.getOpcode() + ", Size: " + in.available());
				if (in.isEmpty() == false && in.available() <= 20) {
					sb.append(String.format(", Data: 0x%X", in.readByte()));
					while (in.isEmpty() == false) {
						sb.append(String.format(" %X", in.readByte()));
					}
				}
				
				Log.debug(sb.toString());
				
				return;
			}
			
			try {
				p.process(this.p, in);
			}
			catch (Exception e) {
				e.printStackTrace();
				Log.warning("Error handling opcode " + in.getOpcode());
			}
		}
	}
}