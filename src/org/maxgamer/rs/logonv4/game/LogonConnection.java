package org.maxgamer.rs.logonv4.game;

import org.maxgamer.rs.cache.RSInputStream;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.logonv4.LSIncomingPacket;
import org.maxgamer.rs.logonv4.LSOutgoingPacket;
import org.maxgamer.rs.model.events.server.LogonConnectEvent;
import org.maxgamer.rs.model.events.server.LogonDisconnectEvent;
import org.maxgamer.rs.network.io.stream.RSOutputStream;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.util.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.BufferUnderflowException;

/**
 * @author netherfoam
 */
public class LogonConnection {
	private Thread thread;
	private ConfigSection config;
	private LogonDecoder decoder;
	private LogonAPI api;
	private int worldId = -1;
	private OutputStream out;
	
	public LogonConnection(ConfigSection config) {
		this.config = config;
	}
	
	public LogonAPI getAPI() {
		return api;
	}
	
	public void start() {
		if (this.thread != null) throw new IllegalStateException("Thread running");
		this.api = new LogonAPI(this);
		this.decoder = new LogonDecoder(this.api);
		this.thread = new Thread("Game->Logon thread") {
			@Override
			public void run() {
				boolean firstAttempt = true;
				while (LogonConnection.this.thread == this) {
					Socket socket = null;
					InputStream in = null;
					OutputStream out = null;
					try {
						if (firstAttempt) Log.debug("Game connecting to Logon");
						socket = new Socket(config.getString("host", "localhost"), config.getInt("port", 2709));
						out = socket.getOutputStream();
						in = socket.getInputStream();
						
						RSOutputStream buffer = new RSOutputStream(32);
						buffer.writePJStr1(config.getString("pass"));
						buffer.writeInt(1); //Version 1
						buffer.writePJStr1(Core.getServer().getRegion());
						buffer.writePJStr1(Core.getServer().getActivity());
						buffer.writeByte(0); //Country
						buffer.writeByte((byte) Core.getServer().getFlags());
						buffer.writePJStr1(Core.getServer().getIP());
						
						out.write(buffer.getPayload());
						
						int authResult = in.read();
						if (authResult == 1) {
							//Success
							worldId = in.read();
							Log.info("LogonServer authenticated as World " + worldId);
							if (Core.getServer().getNetwork().isRunning() == false) {
								Core.getServer().getNetwork().start();
							}
							
							Core.submit(new Runnable() {
								@Override
								public void run() {
									new LogonConnectEvent().call();
								}
							}, false);
							
							LogonConnection.this.out = out; //Now ready to write!
							
							final Thread runner = this;
							Thread pinger = new Thread("logon-pinger") {
								@Override
								public void run() {
									while (runner == LogonConnection.this.thread) {
										LSOutgoingPacket out = new LSOutgoingPacket(4);
										try {
											write(out);
										}
										catch (IOException e) {
											return;
										}
										
										try {
											Thread.sleep(3000);
										}
										catch (InterruptedException e) {
											return; //We've been asked to stop
										}
									}
								}
							};
							pinger.setDaemon(true);
							pinger.start();
							
							try {
								LogonConnection.this.run(socket);
							}
							catch (IOException e) {
							}
							
							//TODO: This has no effect as we set firstAttempt = false below, but its a minor message and implied
							//firstAttempt = true; //next attempt will print a message
							Log.debug("LogonServer connection closed");
						}
						else {
							Log.severe("Connection rejected by LogonServer, got code " + authResult);
							LogonConnection.this.stop();
						}
					}
					catch (ConnectException e) {
						if (firstAttempt) {
							Log.warning("Failed to connect to LoginServer.");
						}
						//Unable to connect to login server
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					finally {
						if (firstAttempt) firstAttempt = false;
						try {
							if (socket != null) socket.close();
							if (out != null) out.close();
							if (in != null) in.close();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if (Core.getServer().getNetwork().isRunning()) {
						Core.submit(new Runnable() {
							@Override
							public void run() {
								new LogonDisconnectEvent().call();
							}
						}, false);
						
						Core.getServer().getNetwork().stop();
					}
					
					try {
						Thread.sleep(500);
					}
					catch (InterruptedException e) {
					}
				}
			}
		};
		this.thread.start();
	}
	
	public void run(Socket socket) throws IOException {
		InputStream in = socket.getInputStream();
		
		while (socket.isConnected()) {
			LSIncomingPacket packet;
			try {
				packet = LSIncomingPacket.parse(new RSInputStream(in));
			}
			catch (IOException e) {
				break;
			}
			catch (BufferUnderflowException e) {
				break;
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			//Log.debug("logonserver gave packet " + packet.getOpcode());
			decoder.decode(packet.getOpcode(), packet);
		}
	}
	
	public void stop() {
		if (this.thread == null) {
			throw new IllegalStateException("Game Thread not running");
		}
		this.thread.interrupt();
		this.thread = null;
		this.out = null;
	}
	
	/**
	 * true after calling start(), false after calling stop()
	 * @return
	 */
	public boolean isRunning() {
		return this.thread != null && this.thread.isAlive();
	}
	
	/**
	 * True if we can write data to the logon server, may occur some moments
	 * after calling start()
	 * @return
	 */
	public boolean isConnected() {
		return out != null && isRunning();
	}
	
	/**
	 * The current world ID or -1 if not connected or connection hasn't been
	 * accepted
	 * @return the world id or -1 if not connected
	 */
	public int getWorldId() {
		return worldId;
	}
	
	public void write(LSOutgoingPacket out) throws IOException {
		if (out == null || !this.isRunning()) {
			throw new IOException("Logon Server is not connected!");
		}
		
		if(out.length() > 0xFFFF){
			throw new IOException("Cannot write a LSOutgoingPacket of length " + out.length() + ", max is " + 0xFFFF);
		}
		
		synchronized (this.out) {
			this.out.write(out.toByteArray());
		}
	}
}