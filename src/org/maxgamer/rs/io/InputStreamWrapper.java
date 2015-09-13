package org.maxgamer.rs.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class InputStreamWrapper extends InputStream{
	/** The character set used for transmitting messages. Should have 1;1 with byte[] to String. */
	public static final String CHARSET = "ISO-8859-1";
	
	private InputStream i;
	/** The number of bytes read */
	private int read = 0;
	/** After this number of milliseconds has passed with no data, we should stop blocking and return */
	private long timeout = 0;
	/** True for little endian, false for big endian */
	private boolean littleEndian;
	/**
	 * Creates a new CachedInputStream with the given buffer.
	 * The buffer array is not copied.
	 * @param buf The buffer
	 */
	public InputStreamWrapper(InputStream in) {
		this.i = in;
	}
	/**
	 * Creates a new CachedInputStream using a ByteArrayInputStream with
	 * the given byte[] as a constructor arg. for this wrappers input stream.
	 * @param data The data array to read from.
	 */
	public InputStreamWrapper(byte[] data){
		this(new ByteArrayInputStream(data));
	}
	
	public InputStreamWrapper toSubStream(int size) throws IOException{
		byte[] data = new byte[size];
		this.read(data);
		return new InputStreamWrapper(data);
	}
	
	/**
	 * Returns the number of bytes which have been successfully read from
	 * this stream.
	 * @return The number of successful bytes read.
	 */
	public int getReadBytes(){
		return read;
	}

	public boolean[] readBits(int num_bools) throws IOException{
		if(num_bools < 0) throw new IllegalArgumentException("Number of bits to read must be >= 0");
		int num_bytes = (num_bools + 7) / 8; //Round up to nearest 8.
		
		boolean[] values = new boolean[num_bools];
		byte[] data = new byte[num_bytes];
		
		this.read(data, 0, data.length);
		
		for(int i = 0; i < data.length; i++){
			byte value = data[i];
			
			for(int j = 0; j < 8 && (i * 8 + j) < num_bools; j++){
				values[i * 8 + j] = (value & 0x1) == 1 ? true : false;
				value >>= 1;
			}
		}
		return values;
	}
	
	/**
	 * Returns true if the stream is reading as little endian.
	 * Little Endian is when bytes are written with the most significant
	 * byte at the highest memory address (Eg, last). For example, 0x6A 3D
	 * will be written to memory in the order 3D, 6A
	 * @return true if the stream is reading in little-endian mode.
	 */
	public boolean isLittleEndian(){
		return littleEndian;
	}
	/**
	 * Returns true if the stream is reading as big endian.
	 * Big Endian is when bytes are written with the most significant
	 * byte at the lowest memory address (Eg, the start). For example, 0x88 2E
	 * will be written to memory in the order 88, 2E.
	 * @return true if the stream is reading in big-endian mode.
	 */
	public boolean isBigEndian(){
		return !littleEndian;
	}
	/**
	 * Sets whether this stream is reading in Little Endian or Big Endian mode.
	 * Use true to read as Little, use false to read as Big Endian.
	 * @param little true for Little Endian, False for Big Endian.
	 */
	public synchronized void setLittleEndian(boolean little){
		this.littleEndian = little;
	}
	
	/**
	 * Returns the number of milliseconds this wrapper will block, at most, before
	 * giving up and returning -1 when reading a byte (or, if implemented, throwing
	 * an exception).
	 * @return The maximum wait time between reading bytes.
	 */
	public long getTimeout(){
		return timeout;
	}
	/**
	 * Sets the number of milliseconds this wrapper will block, at most, before
	 * giving up and returning -1 when reading a byte (or, if implemented, throwing
	 * an exception).
	 * @param time The maximum wait time between reading bytes.
	 */
	public void setTimeout(long time){
		this.timeout = time;
	}
	
	/**
	 * Reads a string from the stream, which is null terminated.
	 * @return The read string. Guaranteed not to be null, but may be empty.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized String readString() throws IOException{
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		int b;
		while((b = this.read()) > 0){
			data.write((byte) b);
		}
		try {
			return new String(data.toByteArray(), CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Reads a single byte from the input stream.
	 * Simply calls read() and casts it to a byte.
	 * @return The byte.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized byte readByte() throws IOException{
		return (byte) this.read();
	}
	
	/**
	 * Reads a given number of bytes from the input stream.
	 * Creates a new byte[] of n length, and then calls
	 * InputStreamWrapper.readBytes(array, 0, n). This is not
	 * affected by Little or Big Endian modes.
	 * @param n The number of bytes to read. Not guaranteed that
	 * all of the bytes are valid unless available() >= n.
	 * @return The byte array
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized byte[] readBytes(int n) throws IOException{
		byte[] data = new byte[n];
		this.readBytes(data, 0, n);
		return data;
	}
	
	/**
	 * Reads data from the input stream into the given array, starting at
	 * start (inclusive) and ending at end (exclusive), or, when the stream
	 * runs out of data (getTimeout() applies here). This is not affected
	 * by Little or Big Endian modes.
	 * @param data The data array to write to
	 * @param start The first position to write to
	 * @param end The last position to write to. If the stream finishes, no data will be written.
	 * @return The number of bytes read. It is not guaranteed that return = (end - start).
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized int readBytes(byte[] data, int start, int end) throws IOException{
		if(start > end) throw new IllegalArgumentException("Start (" + start + ") must be <= End (" + end + ")");
		
		int n = this.getReadBytes();
		int next = this.read();
		for(int pos = start; pos < end && next >= 0; pos++){
			data[pos] = (byte) next;
			this.read();
		}
		
		//The number of bytes read.
		return this.getReadBytes() - n;
	}
	
	/**
	 * Reads an unsigned byte from the input stream.
	 * @return The byte value, from range 0-255 inclusive.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized short readUnsignedByte() throws IOException{
		return (short) (readByte() & 0xFF);
	}
	/**
	 * Reads a long from the input stream (8 bytes).
	 * @return The long.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized long readLong() throws IOException{
		if(this.littleEndian){
			return ((long)((readInt() & 0xFFFFFFFF)) | (long)((readInt() << 32)));
		}
		else{
			return (readInt() << 32) | (readInt() & 0xFFFFFFFF);
		}
	}
	/**
	 * Reads an int from the input stream (4 bytes).
	 * @return The int.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized int readInt() throws IOException{
		if(this.littleEndian){
			return ((readShort() & 0xFFFF) | (readShort() << 16));
		}
		else{
			return (readShort() << 16) | (readShort() & 0xFFFF);
		}
	}
	/**
	 * Reads an unsigned int from the input stream (4 bytes).
	 * @return The int, as a long.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized long readUnsignedInt() throws IOException{
		return ((long)readInt()) & 0xFFFFFFFFL;
	}
	/**
	 * Reads a short from the input stream (2 bytes).
	 * @return The short.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized short readShort() throws IOException{
		if(this.littleEndian){
			return (short) ((readByte() & 0xFF) | (readByte() << 8));
		}
		else{
			return (short) ((readByte() << 8) | (readByte() & 0xFF));
		}
	}
	
	/**
	 * Reads an unsigned short from the input stream (2 bytes).
	 * @return The short, as an int.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized int readUnsignedShort() throws IOException{
		return readShort() & 0xFFFF;
	}
	/**
	 * Reads a single ASCII character from the stream (No special characters)
	 * @return The character that was read. Will be between range 0-255.
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized char readChar() throws IOException{
		return (char) readByte();
	}
	/**
	 * Reads a double by reading a long, and converting it to a double.
	 * @return The value
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized double readDouble() throws IOException{
		return Double.longBitsToDouble(this.readLong());
	}
	/**
	 * Reads a float by reading an int, and converting it to a float.
	 * @return The value
	 * @throws IOException If the stream was closed during reading.
	 */
	public synchronized float readFloat() throws IOException{
		return Float.intBitsToFloat(this.readInt());
	}
	
	/**
	 * Closes the internal stream. Call this when finished with the resource.
	 * If you use a stream in the constructor for this object, and have closed that stream
	 * elsewhere, it is not necessary to call this method.
	 */
	@Override
	public void close(){
		try {
			i.close();
		}
		catch (IOException e) {}
	}

	/**
	 * Reads a single byte of data from the stream. It will wait for at most
	 * InputStreamWrapper.getTimeout() milliseconds before returning -1.
	 * When -1 is returned, it means the stream is open but no data is available.
	 * @return The data read, range 0-255, or -1 if no data is available.
	 * @throws IOException If the stream was closed during reading.
	 */
	@Override
	public synchronized int read() throws IOException{
		long start = System.currentTimeMillis();
		
		int n;
		while((n = i.read()) < 0 && start + this.timeout > System.currentTimeMillis()){
			try {
				Thread.sleep(0, 500000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(n >= 0){
			read++;
		}
		return n;
	}
	
	/**
	 * Returns the number of bytes available in this stream.
	 * @return The number of bytes available in this stream.
	 */
	@Override
	public synchronized int available() throws IOException{
		return i.available();
	}
	
	/**
	 * Skips the number of bytes specified.
	 * @param n The number of bytes to skip.
	 */
	@Override
	public synchronized long skip(long n) throws IOException{
		return i.skip(n);
	}
}