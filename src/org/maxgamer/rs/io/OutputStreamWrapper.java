package org.maxgamer.rs.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class OutputStreamWrapper extends OutputStream{
	/** The internal output stream */
	private OutputStream o;
	/** True for little endian, false for big endian */
	private boolean littleEndian;
	private int written = 0;
	
	public OutputStreamWrapper(OutputStream out) {
		this.o = out;
	}
	/**
	 * Returns true if the stream is writing as little endian.
	 * Little Endian is when bytes are written with the most significant
	 * byte at the highest memory address (Eg, last). For example, 0x6A 3D
	 * will be written to memory in the order 3D, 6A
	 * @return true if the stream is writing in little-endian mode.
	 */
	public synchronized boolean isLittleEndian(){
		return littleEndian;
	}
	/**
	 * Returns true if the stream is writing as big endian.
	 * Big Endian is when bytes are written with the most significant
	 * byte at the lowest memory address (Eg, the start). For example, 0x88 2E
	 * will be written to memory in the order 88, 2E.
	 * @return true if the stream is writing in big-endian mode.
	 */
	public synchronized boolean isBigEndian(){
		return !littleEndian;
	}
	
	public void write(boolean... bools) throws IOException{
		for(int i = 0; i < bools.length; i += 8){
			byte value = 0;
			for(int j = 0; j < 8 && (j + i < bools.length); j++){
				if(bools[j+i]) value = (byte) ((value << 1) | 0x1);
			}
			this.writeByte(value);
		}
	}
	
	/**
	 * Writes the given data, starting at start (inclusive), ending at end (exclusive).
	 * @param data The data to write
	 * @param start The start index of the data (Inclusive)
	 * @param end The end index of the data (Exclusive)
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void write(byte[] data, int start, int end) throws IOException{
		for(int i = start; i < end; i++){
			this.write(data[i]);
		}
	}
	
	/**
	 * Writes the given data to this stream.
	 * @param data The data to write.
	 * @throws IOException if the stream is closed.
	 */
	public synchronized void write(byte[] data) throws IOException{
		this.write(data, 0, data.length);
	}
	
	/**
	 * Sets whether this stream is writing in Little Endian or Big Endian mode.
	 * Use true to read as Little, use false to read as Big Endian.
	 * @param little true for Little Endian, False for Big Endian.
	 */
	public synchronized void setLittleEndian(boolean little){
		this.littleEndian = little;
	}
	/**
	 * Writes the given byte to this output stream.
	 * @param b The byte to write
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeByte(int b) throws IOException{
		this.write(b);
	}
	/**
	 * Writes the given short to this output stream. (2 bytes)
	 * @param s The short to write.
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeShort(int s) throws IOException{
		if(littleEndian){
			this.writeByte((byte) s);
			this.writeByte((byte) (s >> 8));
		}
		else{
			this.writeByte((byte) (s >> 8));
			this.writeByte((byte) s);
		}
	}
	/**
	 * Writes the given int to this output stream. (4 bytes)
	 * @param i The int to write.
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeInt(int i) throws IOException{
		if(littleEndian){
			this.writeShort((short) i);
			this.writeShort((short) (i >> 16));
		}
		else{
			this.writeShort((short) (i >> 16));
			this.writeShort((short) i);
		}
	}
	/**
	 * Writes the given long to this output stream. (8 bytes)
	 * @param l The long to write.
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeLong(long l) throws IOException{
		if(littleEndian){
			this.writeInt((int) l);
			this.writeInt((int) (l >> 32));
		}
		else{
			this.writeInt((int) (l >> 32));
			this.writeInt((int) l);
		}
	}
	/**
	 * Writes the given char to this output stream. The value
	 * is cast to a byte and written, so only ASCII values 0-255
	 * are valid.
	 * @param c The character to write.
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeChar(char c) throws IOException{
		this.writeByte((byte) c);
	}
	/**
	 * Writes the given float to this output stream. (4 bytes).
	 * The value is converted to an integer equivilant, which is
	 * then written.
	 * @param f The float to write.
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeFloat(float f) throws IOException{
		this.writeInt(Float.floatToRawIntBits(f));
	}
	/**
	 * Writes the given double to this output stream. (8 bytes).
	 * The value is converted to a long equivilant, which is then
	 * written.
	 * @param d The double to write
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void writeDouble(double d) throws IOException{
		this.writeLong(Double.doubleToRawLongBits(d));
	}
	/**
	 * Writes the given String to the output stream, terminated
	 * by a NULL character at the end.
	 * @param s The String to write
	 * @throws IOException If the stream is closed.
	 */
	public synchronized void write(String s) throws IOException{
		byte[] data;
		try {
			data = s.getBytes(InputStreamWrapper.CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			data = new byte[0];
		}
		for(byte b : data){
			this.write(b);
		}
		this.write(0);
	}
	
	/**
	 * Closes this OutputStream. If an OutputStream was used in the constructor,
	 * and said OutputStream has already been closed, this method does not need
	 * to be called. Otherwise, you should do so to avoid resource leaks.
	 */
	@Override
	public void close(){
		try {
			o.close();
		} catch (IOException e) {}
	}

	/**
	 * Writes the given byte to this OutputStream.
	 * @param b The byte to write. This is cast to a byte.
	 * @throws IOException If the stream is closed.
	 */
	@Override
	public synchronized void write(int b) throws IOException {
		o.write(b);
		written++;
	}
	
	public int getWrittenBytes(){
		return written;
	}
}