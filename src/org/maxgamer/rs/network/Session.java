package org.maxgamer.rs.network;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.ScreenSettings;
import org.maxgamer.rs.model.events.session.SessionCloseEvent;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.network.io.rawhandler.RawConnectionHandler;
import org.maxgamer.rs.network.io.rawhandler.RawHandler;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;
import org.maxgamer.rs.network.server.RS2Server;
import org.maxgamer.rs.structure.ServerSession;

/**
 * Represents a currently online player's profile.
 * @author netherfoam
 */
public class Session extends ServerSession {
	/**
	 * The next available Session ID. One of these is generated for each new
	 * session that is constructed.
	 */
	private static int nextSessionId = 0;
	
	/** The time that this session last received data from the client */
	private long lastPing;
	
	/**
	 * The unique session number for this session. This is not the same as the
	 * client UUID
	 */
	private int sessionNumber = nextSessionId++;
	
	/**
	 * The revision that the client is using TODO: Move this to Client
	 */
	private int revision = -1;
	
	/**
	 * The current packet handler used for this session
	 */
	private RawHandler handler;
	
	/** These are notified when the session is disconnected */
	private LinkedList<Runnable> closeHandlers = new LinkedList<Runnable>();
	
	/**
	 * The client's current screen settings TODO: Move this to Client
	 */
	private ScreenSettings screen = new ScreenSettings(); //TODO: Move this
	
	/**
	 * TODO: Does the client actually use this? This is the encryption byte used
	 * to send data to the client. Any bytes sent are XORed with this value (val
	 * ^ encryption)
	 */
	//private byte encryption;
	
	private RS2Server server;
	
	/**
	 * Creates a new client
	 * @param s The connection to the client
	 * @throws IOException If the input or output of the socket is invalid
	 */
	public Session(RS2Server server, SocketChannel channel, SelectionKey key) {
		super(channel, key);
		this.server = server;
		this.lastPing = System.currentTimeMillis();
		this.setHandler(new RawConnectionHandler(this));
		closeHandlers.add(new Runnable() {
			@Override
			public void run() {
				Log.debug("Session closed, was at handler stage: " + handler.getClass().getSimpleName());
			}
		});
	}
	
	/**
	 * Returns the revision the player is on, eg 637.
	 * @return The revision eg 637 or -1 if unknown so far.
	 */
	public int getRevision() {
		return revision;
	}
	
	public RawHandler getHandler() {
		return handler;
	}
	
	/**
	 * A Unique number allocated to this session. This is generated server-sided
	 * and is unique for this server. It starts at 0 and increments infinitely
	 * @return the unique session ID for this session
	 */
	public int getSessionId() {
		return sessionNumber;
	}
	
	/**
	 * Sets the encryption byte for this client. The encryption byte is XOR'ed
	 * (^) with all data sent to the client. It is not present in the Eclipse639
	 * client.
	 * @param b the byte to set
	 */
	/*
	 * public void setEncryption(byte b){ this.encryption = b; }
	 */
	
	/**
	 * Sets the players revision.
	 * @param rev The version number
	 */
	public void setRevision(int rev) {
		this.revision = rev;
	}
	
	/**
	 * Players screen settings
	 * @return the screen settings
	 */
	public ScreenSettings getScreenSettings() {
		return screen;
	}
	
	/**
	 * Gives the session a new raw data handler. The raw handler will be
	 * notified of all data and decides what to do with it. A raw handler should
	 * mark the stream once it receives data, and reset the stream if it reads
	 * data but not enough is available.
	 * 
	 * When more data is received from the client, any previously unhandled data
	 * will still be in the buffer. The raw handler is for things such as login
	 * protocols, the lobby and supplying data to packet managers.
	 * @param handler The packet handler you want.
	 */
	public void setHandler(RawHandler handler) {
		this.handler = handler;
		handle();
	}
	
	private void handle() {
		synchronized (this) {
			if (handler != null && getInput().hasRemaining()) {
				int pos = getInput().position();
				RSByteBuffer buffer = new RSByteBuffer(this.getInput());
				try {
					handler.handle(buffer);
				}
				catch (IndexOutOfBoundsException e) {
					getInput().position(pos);
				}
				catch (BufferUnderflowException e) {
					getInput().position(pos);
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Epoch time in milliseconds that the player last sent a packet to the
	 * server. (Not time since last ping)
	 * @return the time of last ping.
	 */
	public long getLastPing() {
		return lastPing;
	}
	
	/**
	 * Calls the run() method on the given runnable when this session is closed.
	 * @param r the runnable to run.
	 */
	public void addCloseHandler(Runnable r) {
		if (r == null) throw new NullPointerException("CloseHandlers may not be null.");
		this.closeHandlers.add(r);
	}
	
	/**
	 * Undoes the effects of addCloseHandler(r), so that the given runnable will
	 * no longer be notified of the session closing.
	 * @param r the runnable to run
	 * @return true if it was removed, false if it was never in the list.
	 */
	public boolean removeCloseHandler(Runnable r) {
		if (r == null) return false; //We don't have nulls in this.
		return this.closeHandlers.remove(r);
	}
	
	/**
	 * Appends the given data to this client's buffer of incoming bytes. This
	 * method is called asynchronously from the Core's executor service
	 * @param data The data to append
	 */
	public void process() {
		this.lastPing = System.currentTimeMillis();
		
		//TODO
		/*
		 * if(this.inBuffer.available() + (end - start) >
		 * Core.getWorldConfig().getInt("network.session-overflow", 8192)){
		 * Log.debug(this +
		 * " is attempting to process too much data, closing session.");
		 * this.close(); }
		 */
		
		Core.submit(new Runnable() {
			@Override
			public void run() {
				handle();
			}
		}, false);
	}
	
	public void write(byte... data) {
		this.write(ByteBuffer.wrap(data));
	}
	
	/**
	 * Writes the given packet to the client and flushes the connection.
	 * @param packet The packet to write
	 * @throws IOException If the socket is closed
	 * @throws IllegalArgumentException If the packet length is invalid, or too
	 *         much or too little data is supplied.
	 */
	public void write(RSOutgoingPacket packet) throws IOException {
		byte[] data = packet.getPayload();
		//CircularBuffer b = new CircularBuffer(3 + data.length);
		ByteBuffer b = ByteBuffer.allocate(3 + data.length);
		
		//Write opcode first
		b.put((byte) packet.getOpcode());
		
		//We must supply the length (length is 0-255 bytes)
		if (packet.getLength() == -1) {
			b.put((byte) data.length);
		}
		//We must supply the length (length is 0-65535 bytes)
		else if (packet.getLength() == -2) {
			b.putShort((short) data.length);
		}
		//We don't know how to send that packet!
		else if (packet.getLength() < -2) {
			throw new IllegalArgumentException("Packet length must be positive or -1 or -2.");
		}
		//Check for inconsistency, we're given too much or too little data to write and we know the size is fixed
		else if (packet.getLength() != data.length) {
			throw new IllegalArgumentException("Packet " + packet.getOpcode() + " size must be " + packet.getLength() + " but was given " + data.length + " bytes to write!");
		}
		
		//Now we can write our payload
		//b.writeByte(data);
		for (byte c : data) {
			b.put(c);
		}
		b.flip();
		this.write(b);
	}
	
	/**
	 * Closes the players current socket. Since the player automatically
	 * reconnects during most stages, you will want to write a disconnection
	 * packet to them before calling this.
	 */
	@Override
	public void close(boolean flush) {
		super.close(flush);
		
		server.onClose(this);
		
		ArrayList<Runnable> closeHandlers = new ArrayList<Runnable>(this.closeHandlers);
		this.closeHandlers.clear(); //Empties them, so that we can't end up in a close() cycle.
		
		for (Runnable r : closeHandlers) {
			r.run();
		}
		
		SessionCloseEvent e = new SessionCloseEvent(this);
		e.call();
	}
	
	@Override
	public String toString() {
		String addr = null;
		try{
			addr = getIP().getAddress().getHostAddress();
		}
		catch(NullPointerException e){}
		return "#" + this.sessionNumber + "@" + addr;
	}
}