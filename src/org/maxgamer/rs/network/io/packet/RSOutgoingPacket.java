package org.maxgamer.rs.network.io.packet;

import org.maxgamer.rs.io.ByteWriter;
import org.maxgamer.rs.network.io.stream.RSOutputStream;

/**
 * @author netherfoam
 */
public class RSOutgoingPacket extends RSOutputStream implements ByteWriter {
	/**
	 * Represents the size of packets which can be sent to the client from the
	 * server. size >= 0 represents a literal size. size == -1 represents the
	 * next byte in the stream is the size size == -2 represents the next two
	 * bytes in the stream (as a short) represent the size.
	 */
	private static byte[] PACKET_SIZES = new byte[] { 12, -1, 2, 5, 6, 6, -2, 8, 6, -1, -2, 28, 0, 1, 2, -2, -2, 8, 3, 2, 7, 3, 6, 0, 6, 0, 6, 6, -2, 5, -1, -2, 1, -2, 6, -1, 3, -2, 3, 17, -1, 4, 6, -1, 4, 0, 2, -1, 3, 2, 7, 3, 4, -1, 4, -1, 10, -2, -1, 3, 4, 4, -1, 7, 6, -1, 1, -1, 3, 8, -2, 6, 0, 4, 11, 3, -1, 2, 4, 0, -2, 4, 0, 20, 4, -1, 6, 7, -2, 0, 10, 10, 16, 6, -1, 10, 10, 2, -2, 0, 6, 6, -1, 7, -2, 6, 6, 6, 2, 11, -1, 1, 6, -2, 3, 8, 8, 0, 1, 12, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, };

	private int opcode;

	static {
		PACKET_SIZES[113] = -2; // TODO just replace the packet size in the
								// array
	}

	public RSOutgoingPacket(int opcode) {
		// If opcode >= 0 && packet size is defined then use packet size as
		// default size, else 16 default
		super(opcode >= 0 && PACKET_SIZES[opcode] >= 0 ? PACKET_SIZES[opcode] : 16);
		this.opcode = opcode;
	}

	public int getOpcode() {
		return this.opcode;
	}

	public int getLength() {
		return PACKET_SIZES[opcode];
	}

	/**
	 * Writes the opcode, length and payload to a byte array and returns that
	 * array.
	 * 
	 * @return the opcode, length and payload as a byte array ready for
	 *         transmission
	 */
	public byte[] toByteArray() {
		byte[] payload = this.getPayload();
		byte[] data = new byte[1 + 2 + payload.length];
		if (payload.length > 0xFFFF) {
			throw new IllegalStateException("Payload length must be <= 0xFFFF, due to protocol limitations. Given " + payload.length);
		}
		data[0] = (byte) this.opcode;
		data[1] = (byte) (payload.length >> 8);
		data[2] = (byte) (payload.length);
		for (int i = 0; i < payload.length; i++)
			data[i + 1 + 2] = payload[i];
		return data;
	}
}