package org.maxgamer.rs.structure;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * A Session held by a ServerHost class. This is notified through a process()
 * method when a read is ready to be done, and may be used to perform async
 * writes on data.
 * @author netherfoam
 */
public abstract class ServerSession {
	public static final int BUFFER_MIN_SIZE = 4096;
	/**
	 * The channel we're reading/writing data to.
	 */
	private SocketChannel channel;
	
	private boolean closing = false;
	
	/**
	 * The selection key, this is used to edit the operations (READ/WRITE) we
	 * wish to listen for on the channel
	 */
	private SelectionKey key;
	
	/**
	 * A LinkedList of ByteBuffers we have received from the channel. These are
	 * of a default size and should fill up appropriately, guaranteeing that
	 * none but the last element are not full. All operations on this should be
	 * synchronized on this.
	 */
	private ByteBuffer read;
	
	/**
	 * A LinkedList of ByteBuffers we wish to write to the channel, where the
	 * first element is the next to be written and the last is the last buffer
	 * to write. New write requests are queued at the end of the list. All
	 * operations on this should be synchronized on this.
	 */
	private LinkedList<ByteBuffer> write;
	
	private int up = 0;
	private int down = 0;
	private long lastReset = 0;
	
	public long getLastBandwidthReset() {
		return lastReset;
	}
	
	public int getUpload() {
		return up;
	}
	
	public int getDownload() {
		return down;
	}
	
	public void resetBandwidth() {
		up = 0;
		down = 0;
		lastReset = System.currentTimeMillis();
	}
	
	/**
	 * Constructs a new ServerSession for the given channel and selection key
	 * @param channel the channel to write to, part of the key.
	 * @param key the key used with the selector.
	 */
	public ServerSession(SocketChannel channel, SelectionKey key) {
		this.channel = channel;
		this.key = key;
		this.write = new LinkedList<>();
		
		this.read = ByteBuffer.allocate(BUFFER_MIN_SIZE);
		this.read.limit(this.read.position()); //No data
		
		key.interestOps(key.interestOps() | SelectionKey.OP_READ);
		resetBandwidth();
	}
	
	/**
	 * Closes the ServerSession, cancelling the key and closing the channel
	 * given in the constructor.
	 */
	public void close(boolean flush) {
		if (this.closing || this.isConnected() == false) {
			return;
		}
		
		this.closing = true;
		
		if (flush == false) {
			this.key.cancel();
			
			try {
				this.channel.close();
			}
			catch (IOException e) {
			}
		}
	}
	
	/**
	 * Fetches a direct reference to the ByteBuffer used for storing incoming
	 * data. Any data read from this buffer may be destroyed after the read.
	 * @return the buffer, never null
	 */
	public ByteBuffer getInput() {
		return this.read;
	}
	
	/**
	 * Fetches the remote IP for this session or null if invalid or closed
	 * @return the remote address
	 */
	public InetSocketAddress getIP() {
		try {
			return (InetSocketAddress) this.channel.getRemoteAddress();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Returns true if the SelectionKey is valid and the SocketChannel is
	 * connected
	 * @return true if connected
	 */
	public boolean isConnected() {
		return this.key.isValid() && this.channel.isConnected();
	}
	
	/**
	 * Called when this session receives one or more bytes
	 */
	public abstract void process();
	
	/**
	 * Writes any available data to the channel, then reads any available data
	 * from the channel. If any data is read, process() is called in the current
	 * thread.
	 * @throws IOException if there was an error reading or writing to the
	 *         socket.
	 */
	protected final void pump() throws IOException {
		synchronized (this) {
			if (this.key.isWritable()) {
				try {
					while (this.write.isEmpty() == false) {
						ByteBuffer bb = this.write.getFirst();
						up += this.channel.write(bb);
						if (bb.remaining() == 0) {
							//Whole buffer was written, attempt to write the next
							this.write.removeFirst();
							continue;
						}
						else {
							//No bytes were written
							break;
						}
					}
				}
				catch (IOException e) {
					//Remove closed session, can't write any more data so close without flushing.
					this.close(false);
					return;
				}
				
				if (this.write.isEmpty()) {
					this.key.interestOps(this.key.interestOps() & ~SelectionKey.OP_WRITE);
				}
			}
		}
		
		synchronized (this) {
			if (this.key.isReadable()) {
				//Prepare for writing
				if (this.read.position() >= BUFFER_MIN_SIZE) {
					//Our buffer has had a considerable chunk of data read from it
					//we should discard the used data. This ensures we don't hold
					//all the data for a single session in memory until it is closed
					ByteBuffer r = ByteBuffer.allocate(this.read.capacity());
					r.put(this.read);
					
					//Now we want to make the new buffer look like the old buffer
					r.limit(r.position());
					r.position(0);
					
					this.read = r;
				}
				
				//The previously existing position of the buffer
				int start = this.read.position();
				//We want to write data to only the end of the buffer
				this.read.position(this.read.limit());
				//Expand our limit to the maximum capacity
				this.read.limit(this.read.capacity());
				
				//The number of bytes read
				int reads = 0;
				
				//True if we found an EOF marker (-1)
				boolean close = false;
				
				do {
					try {
						//Attempt to read data, throws IOException
						int size = this.channel.read(this.read);
						if (size == -1) {
							//No data available, end of stream
							close = true;
							break;
						}
						else if (size == 0) {
							//No data is available to be read
							break;
						}
						else {
							//We successfully read some data
							reads += size;
							down += size;
							
							if (this.read.hasRemaining() == false) {
								//Our buffer has run out of space to write to! Thus we double
								//the size of the buffer.
								ByteBuffer r = ByteBuffer.allocate(this.read.capacity() * 2);
								
								//Rewind the original buffer, and place it in r
								this.read.limit(this.read.position());
								this.read.position(start);
								r.put(this.read);
								
								//Our readable buffer is now r
								this.read = r;
								
								//When we read next, we will want to read from 0
								//instead of the old start value
								start = 0;
							}
							else {
								//We are done reading, our buffer was not filled therefore
								//there is no more data available at this time
								break;
							}
						}
					}
					catch (IOException e) {
						//Occurs when -1 isn't read, eg cable unplugged
						close = true;
						break;
					}
					//Repeat until we have space in our buffer (Ergo, no data left waiting)
				} while (this.read.hasRemaining() == false);
				
				//Prepare for reading again. This is basically flip()
				this.read.limit(this.read.position());
				this.read.position(start);
				
				if (reads > 0) {
					//Only call process() if we have data which has been read successfully
					try {
						this.process();
					}
					catch (Throwable t) {
						t.printStackTrace();
					}
				}
				
				if (close) {
					//Notify this on close. We can't flush though, it was just closed by the client!
					this.close(false);
				}
			}
		}
		
		if (this.closing && this.write.isEmpty()) {
			this.key.cancel();
			
			try {
				this.channel.close();
			}
			catch (IOException e) {
			}
			return;
		}
	}
	
	/**
	 * Writes the given data to the server in an async thread.
	 * @param bb the data to write
	 */
	public void write(ByteBuffer bb) {
		if (this.closing || this.isConnected() == false) {
			throw new IllegalStateException("Session is closed or closing. Cannot write to it.");
		}
		
		assert bb.remaining() > 0;
		
		synchronized (this) {
			this.write.addLast(bb);
			this.key.interestOps(this.key.interestOps() | SelectionKey.OP_WRITE);
			this.key.selector().wakeup();
		}
	}
}