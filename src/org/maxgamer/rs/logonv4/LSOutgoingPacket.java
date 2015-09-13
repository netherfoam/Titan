package org.maxgamer.rs.logonv4;

import org.maxgamer.rs.io.ByteWriter;
import org.maxgamer.rs.network.io.stream.RSOutputStream;

/**
 * @author netherfoam
 */
public class LSOutgoingPacket extends RSOutputStream implements ByteWriter {
	private int opcode;
	
	public LSOutgoingPacket(int opcode) {
		super(32); //Default 32 size, varies
		
		this.opcode = opcode;
	}
	
	public int getOpcode() {
		return this.opcode;
	}
	
	/**
	 * Writes the opcode, length and payload to a byte array and returns that
	 * array.
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