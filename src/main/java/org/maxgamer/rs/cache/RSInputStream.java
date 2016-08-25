package org.maxgamer.rs.cache;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RSInputStream extends DataInputStream {
	public RSInputStream(InputStream in) {
		super(in);
	}
	
	public String readPJStr1() throws IOException {
		StringBuilder sb = new StringBuilder();
		byte b;
		while ((b = readByte()) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}
	
	public int readShortA() throws IOException {
		return (((readByte() & 0xff) << 8) + (readByte() - 128 & 0xff));
	}
	
	public int readShortLEA() throws IOException {
		byte bl = readByte();
		byte bh = readByte();
		return ((bl - 128 & 0xff)) + ((bh & 0xff) << 8);
	}
	
	public byte readSByteA() throws IOException {
		return (byte) (read() - 128);
	}
	
	public byte readSByteC() throws IOException {
		return (byte) (-read());
	}
	
	public byte readSByteS() throws IOException {
		return (byte) (128 - read());
	}
	
	public int readByteA() throws IOException {
		return (readUnsignedByte() - 128 & 0xff);
	}
	
	public int readByteC() throws IOException {
		return -(readUnsignedByte() & 0xff);
	}
	
	public int readByteS() throws IOException {
		return (128 - readUnsignedByte() & 0xff);
	}
	
	public int read24BitInt() throws IOException {
		return ((read() & 0xff) << 16) + ((read() & 0xff) << 8) + (read() & 0xff);
	}
	
	public int readSShort() throws IOException {
		int i_54_ = readShort();
		if (i_54_ > 32767) i_54_ -= 65536;
		return i_54_;
	}
	
	public int readUnsignedSmart() throws IOException {
		int i = readUnsignedByte();
		if (i < 128) {
			return i;
		}
		else {
			//i &= ~SMART_LARGE_FLAG; //Strip the 0x80 bit
			i -= 128;
			return (i << 8) | (readUnsignedByte());
		}
	}
	
	public int readSmart() throws IOException {
		int i = readUnsignedByte();
		//if((i & SMART_LARGE_FLAG) != 0){
		if (i < 128) {
			return i;
		}
		else {
			//i &= ~SMART_LARGE_FLAG; //Strip the 0x80 bit
			i -= 128;
			return (i << 8) | (readUnsignedByte());
		}
	}
	
	public boolean isEmpty() throws IOException {
		return super.available() <= 0;
	}
}