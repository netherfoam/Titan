package org.maxgamer.rs.util;

import java.nio.ByteBuffer;

/**
 * @author 'Mystic Flow
 */
public class BufferUtils {
	
	public static void writeRS2String(ByteBuffer buffer, String string) {
		buffer.put(string.getBytes());
		buffer.put((byte) 0);
	}
	
	public static String readRS2String(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while (buffer.remaining() > 0 && (b = buffer.get()) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}
	
	public static int readSmart(ByteBuffer buf) {
		int peek = buf.get(buf.position()) & 0xFF;
		if (peek < 128) {
			return buf.get();
		}
		else {
			return (buf.getShort() & 0xFFFF) - 32768;
		}
	}
	
	public static int getTriByte(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) | ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
	}
	
	public static int readExtendedSmart(ByteBuffer buffer) {
		int total = 0;
		int smart = readSmart(buffer);
		while (smart == 0x7FFF) {
			smart = readSmart(buffer);
			total += 0x7FFF;
		}
		total += smart;
		return total;
	}
	
	public static String toString(ByteBuffer buffer){
		StringBuilder builder = new StringBuilder(buffer.remaining());
		for(int i = buffer.remaining(); i > 0; i--){
			builder.append((char) buffer.get());
		}
		return builder.toString();
	}
}
