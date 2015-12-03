package org.maxgamer.rs.network.io.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.maxgamer.rs.structure.BitOutputStream;

/**
 * An output stream containing common methods for RS Streams. See also
 * {@link RSInputBuffer}
 * @author netherfoam
 */
public class RSOutputStream {
	/**
	 * The output stream delegate
	 */
	private ByteArrayOutputStream out;
	
	/**
	 * The bit output stream
	 */
	private BitOutputStream bitOut;
	
	/**
	 * Constructs a new OutputStream, with an internal size of 32
	 */
	public RSOutputStream() {
		this(32);
	}
	
	/**
	 * Constructs a new OutputStream. The size is not permanent and is only a
	 * recommendation, to prevent this stream's buffer from having to resize
	 * frequently you should provide a size which is accurate in most cases.
	 * @param size the internal start size of the stream
	 */
	public RSOutputStream(int size) {
		this.out = new ByteArrayOutputStream(size);
	}
	
	/**
	 * Writes a single byte
	 * @param b the byte
	 */
	public void writeByte(int b) {
		out.write(b);
	}
	
	/**
	 * The total number of bytes written so far
	 * @return The total number of bytes written so far
	 */
	public int length(){
		return out.size();
	}
	
	/**
	 * All data currently written to this stream as a byte array. The byte array
	 * will have the length equal to the number of bytes written.
	 * @return all data written to this stream
	 */
	public byte[] getPayload() {
		return out.toByteArray();
	}
	
	/**
	 * Writes the given array to this stream.
	 * @param data
	 */
	public void write(byte[] data) {
		try {
			out.write(data);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * WRites the given byte to this stream
	 * @param b
	 */
	public void write(int b) {
		writeByte((byte) b);
	}
	
	/**
	 * Writes the given short to this stream
	 * @param s
	 */
	public void writeShort(short s) {
		writeByte((byte) (s >> 8));
		writeByte((byte) s);
	}
	
	/**
	 * Writes the given short to this stream
	 * @param i
	 */
	public void writeShort(int i) {
		writeShort((short) i);
	}
	
	/**
	 * Writes the given int to this stream
	 * @param i
	 */
	public void writeInt(int i) {
		writeShort((short) (i >> 16));
		writeShort((short) i);
	}
	
	public void writeTriByte(int v) {
		writeByte(v >> 16);
		writeByte(v >> 8);
		writeByte(v >> 0);
	}
	
	/**
	 * Writes the given long to this stream
	 * @param l
	 */
	public void writeLong(long l) {
		writeInt((int) (l >> 16));
		writeInt((int) l);
	}
	
	/**
	 * Writes the given flaot to this stream
	 * @param f
	 */
	public void writeFloat(float f) {
		writeInt(Float.floatToIntBits(f));
	}
	
	/**
	 * Writes the given double to this stream
	 * @param d
	 */
	public void writeDouble(double d) {
		writeLong(Double.doubleToRawLongBits(d));
	}
	
	/**
	 * Returns the output stream delegate for this stream
	 * @return the output stream delegate
	 */
	public OutputStream getOutputStream() {
		return out;
	}
	
	/**
	 * Begins writing bits to this stream
	 */
	public void startBitAccess() {
		if (bitOut != null) {
			throw new IllegalStateException("Bit access already started!");
		}
		bitOut = new BitOutputStream(out);
	}
	
	/**
	 * Ends writing bits to this stream
	 */
	public void finishBitAccess() {
		if (bitOut == null) {
			throw new IllegalStateException("Bit access not started!");
		}
		bitOut.flush();
		bitOut = null;
	}
	
	/**
	 * Writes the given bits to this stream. You must call startBitAccess()
	 * before calling this method, and you should call finishBitAccess() when
	 * you are done writing bits.
	 * @param numBits the number of bits to write
	 * @param bits the bits to write
	 */
	public void writeBits(int numBits, int bits) {
		bitOut.write(numBits, bits);
	}
	
	/**
	 * Writes the given JagString. These strings have a single byte which
	 * represents each character and are terminated by a NUL (byte 0) value
	 * @param string the string to write
	 */
	public void writePJStr1(String string) {
		if (string.indexOf((char) 0) >= 0) {
			throw new IllegalArgumentException("The given string may not contain a NUL (byte 0) character");
		}
		write(string.getBytes());
		writeByte((byte) 0);
	}
	
	/**
	 * Writes the given JagString2. These strings have a single byte which
	 * represents each character and are terminated by a NUL (byte 0) value.
	 * They also must start with a NUL (byte 0) character which is discarded
	 * when read.
	 * @param string the string to write
	 */
	public void writePJStr2(String s) {
		if (s.indexOf((char) 0) >= 0) {
			throw new IllegalArgumentException("The given string may not contain a NUL (byte 0) character");
		}
		writeByte((byte) 0);
		writePJStr1(s);
	}
	
	public void writeShortA(int val) {
		writeByte((byte) (val >> 8));
		writeByte((byte) (val + 128));
	}
	
	public void writeLEShortA(int val) {
		writeByte((byte) (val + 128));
		writeByte((byte) (val >> 8));
	}
	
	public void writeLEShort(int val) {
		writeByte((byte) (val));
		writeByte((byte) (val >> 8));
	}
	
	public void writeLEInt(int val) {
		writeByte((byte) (val));
		writeByte((byte) (val >> 8));
		writeByte((byte) (val >> 16));
		writeByte((byte) (val >> 24));
	}
	
	public void writeByteA(int val) {
		writeByte((byte) (val + 128));
	}
	
	public void writeByteC(int val) {
		writeByte((byte) (-val));
	}
	
	public void writeByteS(int val) {
		writeByte((byte) (128 - val));
	}
	
	public void writeSmart(int val) {
		if (val >= 128) {
			writeShort((short) (val + 32768));
		}
		else {
			writeByte((byte) val);
		}
	}
	
	public void writeMediumInt(int i) {
		writeByte((byte) (i >> 16));
		writeByte((byte) (i >> 8));
		writeByte((byte) i);
	}
	
	public void writeLEMedium(int i) {
		writeByte((byte) i);
		writeByte((byte) (i >> 8));
		writeByte((byte) (i >> 16));
	}
	
	/**
	 * Say we have an integer where byte[0] represents the least significant
	 * byte. This method writes byte[1], byte[0], byte[3], byte[2]. Why? Just
	 * because Jagex are nuts.
	 * @param val the value to write.
	 */
	public void writeInt1(int val) {
		writeByte((byte) (val >> 8));
		writeByte((byte) val);
		writeByte((byte) (val >> 24));
		writeByte((byte) (val >> 16));
	}
	
	public void writeByte(byte b) {
		out.write(b);
	}
	
	public void write(byte[] data, int off, int length) {
		out.write(data, off, length);
	}
	
	public void writeInt2(int val) {
		writeByte((byte) (val >> 16));
		writeByte((byte) (val >> 24));
		writeByte((byte) val);
		writeByte((byte) (val >> 8));
	}
}