package org.maxgamer.rs.network.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.maxgamer.io.ByteReader;

/**
 * Basically a stream wrapper with some frequently used RS methods
 * @author netherfoam
 */
public class RSInputBuffer {
	/**
	 * The last marked index
	 */
	private int mark;
	
	/**
	 * The current position in the steam.
	 */
	private int index = 0;
	
	/**
	 * The data in the steam
	 */
	private byte[] payload;
	
	/**
	 * Constructs a new RSInputStream from the given data
	 * @param in the data source
	 * @param length the number of bytes to read
	 * @throws IOException if the stream is unreadable or not enough data is
	 *         available
	 */
	public RSInputBuffer(ByteReader in, int length) throws IOException {
		if (in.available() < length) {
			throw new IOException("requested " + length + ", but only " + in.available() + " remaining");
		}
		
		payload = new byte[length];
		
		for (int i = 0; i < length; i++) {
			int v = in.readByte();
			payload[i] = (byte) v;
		}
	}
	
	public RSInputBuffer(ByteBuffer in, int length) throws BufferUnderflowException {
		if (in.remaining() < length) {
			throw new BufferUnderflowException();
		}
		
		payload = new byte[length];
		in.get(payload);
	}
	
	public RSInputBuffer(RSInputBuffer src, int length) {
		if (src.available() < length) {
			throw new BufferUnderflowException();
		}
		
		payload = new byte[length];
		src.read(payload);
	}
	
	/**
	 * Reads a signed byte from the stream
	 * @return the next signed byte
	 */
	public byte readByte() {
		return payload[index++];
	}
	
	/**
	 * Reads two bytes, with the first byte being the highest value byte. This
	 * calls readByte() twice.
	 * @return the next short
	 */
	public short readShort() {
		return (short) (((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
	}
	
	/**
	 * Reads four bytes, with the first byte being the highest value byte and in
	 * descending order. This calls readShort() twice
	 * @return the next integer.
	 */
	public int readInt() {
		return (((readShort() & 0xFFFF) << 16) | (readShort() & 0xFFFF));
	}
	
	/**
	 * Reads eight bytes, with the first byte being the highest value byte and
	 * in descending order. This calls readInt() twice
	 * @return the next long
	 */
	public long readLong() {
		return ((long) ((readInt() & 0xFFFFFFFF)) | (long) ((readInt() << 32)));
	}
	
	/**
	 * Reads four bytes, and uses Float.intBitsToFloat() to convert it. This
	 * calls readInt().
	 * @return the next float
	 */
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}
	
	/**
	 * Reads eight bytes, and uses Double.longBitsToDouble() to convert it. This
	 * calls readLong().
	 * @return the next double
	 */
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}
	
	/**
	 * Returns the number of available bytes in the stream.
	 * @return the number of available bytes in the stream.
	 */
	public int available() {
		return payload.length - index;
	}
	
	/**
	 * Returns true if available() <= 0.
	 * @return true if there is no more data left
	 */
	public boolean isEmpty() {
		return index >= payload.length;
	}
	
	/**
	 * Reads data into the given array. This throws an
	 * {@link IndexOutOfBoundsException} if the parameters don't make sense.
	 * @param dest the destination array
	 * @param start the start index of the array
	 * @param end the end index of the array
	 * @return the number of bytes read
	 */
	public int read(byte[] dest, int start, int end) {
		int read = end - start;
		
		while (start < end) {
			dest[start++] = readByte();
		}
		
		return read;
	}
	
	/**
	 * Reads data through a call read(dest, 0, dest.length)
	 * @param dest the destination
	 * @return the number of bytes read
	 */
	public int read(byte[] dest) {
		return read(dest, 0, dest.length);
	}
	
	/**
	 * Marks this stream at the current index, so that reset() will put the
	 * index back to the position it was at at the time of calling mark()
	 */
	public void mark() {
		this.mark = index;
	}
	
	/**
	 * Resets the index to the last mark, or 0 if no mark was set.
	 */
	public void reset() {
		this.index = mark;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(payload.length * 3);
		for (byte b : payload) {
			sb.append(String.format("%X ", b));
		}
		return sb.toString();
	}
	
	/**
	 * A String that starts with a NUL character and ends with a NUL character,
	 * with each byte representing a character in between (Eg, characters are
	 * all 1 byte). This reads the NUL character and then invokes readPJStr1().
	 * @return the string read, possibly empty but not null
	 * @throws RuntimeException if the string doesn't start with a NUL character
	 */
	public String readPJStr2() {
		if (readByte() != 0) throw new RuntimeException("PJStr2 must start with a null character! It didn't.");
		return readPJStr1(); //Same format as PJStr1
	}
	
	/**
	 * A String that ends with a NUL character, with each byte representing a
	 * character in between (Eg, characters are all 1 byte)
	 * @return the string read, possibly empty but not null
	 */
	public String readPJStr1() {
		StringBuilder sb = new StringBuilder();
		byte b;
		while ((b = readByte()) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}
	
	/**
	 * Reads a type C byte.
	 *
	 * @return A type C byte.
	 */
	public byte readByteC() {
		return (byte) (-readByte());
	}
	
	/**
	 * reads a type S byte.
	 *
	 * @return A type S byte.
	 */
	public byte readByteS() {
		return (byte) (128 - readByte());
	}
	
	/**
	 * Reads a little-endian type A short.
	 *
	 * @return A little-endian type A short.
	 */
	public int readLEShortA() {
		int i = (readByte() - 128 & 0xFF) | ((readByte() & 0xFF) << 8);
		return i;
	}
	
	/**
	 * Reads a little-endian short.
	 *
	 * @return A little-endian short.
	 */
	public int readLEShort() {
		int i = (readByte() & 0xFF) | ((readByte() & 0xFF) << 8);
		return i;
	}
	
	/**
	 * reads a 3-byte integer.
	 *
	 * @return The 3-byte integer.
	 */
	public int readTriByte() {
		return ((readByte() << 16) & 0xFF) | ((readByte() << 8) & 0xFF) | (readByte() & 0xFF);
	}
	
	/**
	 * Reads a type A byte.
	 *
	 * @return A type A byte.
	 */
	public byte readByteA() {
		return (byte) (readByte() - 128);
	}
	
	/**
	 * Reads a type A short.
	 *
	 * @return A type A short.
	 */
	public int readShortA() {
		int i = ((readByte() & 0xFF) << 8) | (readByte() - 128 & 0xFF);
		return i;
	}
	
	/**
	 * reads a smart. One or two bytes. The first byte indicates if there is a
	 * second if the left most bit (0x80) is set. If so, then a second byte is
	 * read. The first byte is used as the highest value byte, and the second is
	 * the lower value. The first byte has the 0x80 flag removed, if given, and
	 * then returns (byte1 << 8) | (byte2)
	 *
	 * @return The smart.
	 */
	public int readSmart() {
		int peek = payload[index];
		if (peek < 128) {
			return (readByte() & 0xFF);
		}
		else {
			return (readShort() & 0xFFFF) - 32768;
		}
	}
	
	public int read() throws IOException {
		if (available() <= 0) throw new IOException("End of Stream");
		return readByte();
	}
	
	public InputStream getInputStream() {
		return new InputStream() {
			
			@Override
			public int read() throws IOException {
				return RSInputBuffer.this.read();
			}
			
			@Override
			public int available() {
				return RSInputBuffer.this.available();
			}
		};
	}
}