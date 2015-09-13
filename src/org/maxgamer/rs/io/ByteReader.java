package org.maxgamer.rs.io;

import java.io.IOException;
import java.io.InputStream;

public interface ByteReader{
	public byte readByte() throws IOException;
	public short readShort() throws IOException;
	public int readInt() throws IOException;
	public long readLong() throws IOException;
	public float readFloat() throws IOException;
	public double readDouble() throws IOException;
	public int available() throws IOException;
	public boolean isEmpty();
	public int read(byte[] dest, int start, int end) throws IOException;
	public int read(byte[] dest) throws IOException;
	public InputStream getInputStream();
	public void mark();
	public void reset();
}