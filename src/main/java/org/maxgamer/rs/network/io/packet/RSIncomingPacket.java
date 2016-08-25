package org.maxgamer.rs.network.io.packet;

import java.io.IOException;

import org.maxgamer.rs.util.io.ByteReader;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;
import org.maxgamer.rs.network.io.stream.RSInputBuffer;
import org.maxgamer.rs.network.protocol.ProtocolException;

/**
 * Represents a packet received from the client
 * @author netherfoam
 */
public class RSIncomingPacket extends RSInputBuffer implements ByteReader {
	/**
	 * Size of packets which the server can receive from the client. Any values
	 * >= 0 are literal (in bytes). Values which are -1 begin with a byte
	 * describing the length of the remainder of the packet. Values of -3 are
	 * possibly unknown.
	 */
	private static final byte[] PACKET_SIZES = new byte[] { 8, -1, -1, 16,
			6,
			2, //0-5
			8, 6, 3, -1, 16, 15, 0, 8, 11, 8, -1, -1, 3, 2, -1, -1, 7, 2, -1, 7, -1, 3, 3, 6, 4, 3, 0, 3, 4, 5, -1, -1, 7, 8, 4, -1, 4, 7, 3, 15, 8, 3, 2, 4, 18, -1, 1, 3, 7, 7, 4, -1, 8, 2, 7, -1, 1, -1, 3, 2, -1, 8, 3, 2, 3, 7, 3, 8, -1, 0, 7, -1, 11, -1, 3, 7, 8, 12, 4, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
			-3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3,
			-3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3 };
	
	public static RSIncomingPacket parse(RSByteBuffer b) throws IOException {
		try {
			int opcode = b.readByte() & 0xFF;
			
			int length = PACKET_SIZES[opcode];
			if (length < 0) {
				switch (length) {
					case -1:
						length = b.readByte() & 0xFF;
						break;
					case -2:
						length = ((b.readByte() & 0xFF) << 8) | (b.readByte() & 0xFF);
						break;
					default:
						throw new ProtocolException("Invalid incoming opcode: " + opcode);
				}
			}
			
			RSIncomingPacket p = new RSIncomingPacket(opcode, b, length);
			return p;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	//Value 0-255
	private int opcode;
	
	private RSIncomingPacket(int opcode, RSByteBuffer b, int length) throws IOException {
		super(b.getBuffer(), length);
		this.opcode = opcode;
	}
	
	public int getOpcode() {
		return opcode;
	}
}