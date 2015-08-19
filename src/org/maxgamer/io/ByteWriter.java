package org.maxgamer.io;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteWriter{
	public void writeByte(byte b) throws IOException;
	public void writeShort(short s) throws IOException;
	public void writeInt(int i) throws IOException;
	public void writeLong(long l) throws IOException;
	public void writeFloat(float f) throws IOException;
	public void writeDouble(double d) throws IOException;
	public void write(byte[] src, int start, int end) throws IOException;
	public void write(byte[] src) throws IOException;
	public OutputStream getOutputStream();
}