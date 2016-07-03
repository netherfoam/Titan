package org.maxgamer.rs.structure;

import org.maxgamer.rs.util.log.Log;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a ServerHost that holds many ServerSessions. This is for IO and
 * allows async read/writing.
 * @author netherfoam
 *
 * @param <T> The type of session this host will create and manage
 */
public abstract class ServerHost<T extends ServerSession> implements Runnable {
	/**
	 * The map of SelectionKey to ServerSessions used by this host
	 */
	private HashMap<SelectionKey, T> sessions = new HashMap<SelectionKey, T>();
	
	/**
	 * The last used selector
	 */
	private Selector selector;
	
	/**
	 * The port which this network server is running on
	 */
	private int port;
	
	/**
	 * The server socket
	 */
	private ServerSocketChannel serverChannel;
	
	/**
	 * The thread which is monitoring this ServerHost
	 */
	private Thread thread;
	
	/**
	 * Constructs, but does not start, a new server host.
	 * @param port the port to run on
	 */
	public ServerHost(int port) {
		if(port <= 0) {
			throw new IllegalArgumentException("Port must be > 0, given " + port);
		}
		this.port = port;
	}
	
	/**
	 * Returns an unmodifiable collection of all of the sessions currently
	 * connected to the host.
	 * @return an unmodifiable collection of all of the sessions currently
	 *         connected to the host.
	 */
	public Collection<T> getSessions() {
		Iterator<T> sit = sessions.values().iterator();
		while (sit.hasNext()) {
			T t = sit.next();
			if (t.isConnected() == false) {
				sit.remove();
			}
		}
		
		return Collections.unmodifiableCollection(sessions.values());
	}
	
	/**
	 * Returns the port this host runs on
	 * @return the port this host runs on
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns true if the selector has been created and is open.
	 * @return true if the selector has been created and is open.
	 */
	public boolean isRunning() {
		return selector != null && selector.isOpen();
	}
	
	/**
	 * Starts the server in an async thread
	 */
	public void start() {
		if (thread != null) {
			throw new IllegalStateException("Server running");
		}
		
		thread = new Thread(this, "ServerHost-" + this); //TODO Name thread
		thread.start();
	}
	
	public void stop() {
		if (thread == null) {
			throw new IllegalStateException("Server stopped");
		}
		try {
			this.selector.close();
			this.serverChannel.close();
		}
		catch (IOException e) {
		}
		this.thread = null;
	}
	
	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();
		
		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		
		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.port);
		serverChannel.socket().bind(isa);
		
		// Register the server socket channel, indicating an interest in 
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		
		return socketSelector;
	}
	
	@Override
	public void run() {
		try {
			try {
				Selector selector = initSelector();
				this.selector = selector;
			}
			catch (BindException e) {
				e.printStackTrace();
				Log.severe("Failed to bind socket. Is there another server running on port " + port + "?");
				System.exit(3);
			}
			while (selector.isOpen()) {
				try {
					selector.select();
					Iterator<SelectionKey> sit = this.selector.selectedKeys().iterator();
					while (sit.hasNext()) {
						//Is this necessary/useful?
						SelectionKey key = sit.next();
						sit.remove();
						
						if (key.isValid() == false) {
							ServerSession session = sessions.get(key);
							session.close(false);
							sessions.remove(key);
							continue;
						}
						
						if (key.isAcceptable()) {
							accept(key);
						}
						else {
							ServerSession session = sessions.get(key);
							try {
								session.pump();
							}
							catch (Throwable t) {
								t.printStackTrace();
							}
						}
					}
				}
				catch (ClosedSelectorException e) {
					return; //Socket closed
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		
		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		//Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);
		
		SelectionKey sessionKey = socketChannel.register(this.selector, 0);
		
		T session = null;
		
		try {
			session = connect(socketChannel, sessionKey);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if (session == null) {
			sessionKey.cancel();
			socketChannel.close();
			return;
		}
		
		this.sessions.put(sessionKey, session);
	}
	
	/**
	 * This method should construct a new ServerSession from the given
	 * SocketChannel and SelectionKey and then return it. This is called for all
	 * new connections.
	 * @param channel the SocketChannel
	 * @param key the SelectionKey
	 * @return the ServerSession, never null
	 */
	public abstract T connect(SocketChannel channel, SelectionKey key) throws IOException;
}