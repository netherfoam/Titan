package org.maxgamer.rs.logonv4.logon;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.maxgamer.rs.logonv4.LSOutgoingPacket;
import org.maxgamer.rs.logonv4.Profile;
import org.maxgamer.rs.network.io.stream.RSInputBuffer;
import org.maxgamer.rs.network.io.stream.RSOutputStream;
import org.maxgamer.rs.structure.ServerSession;

/**
 * @author netherfoam
 */
public class WorldHost extends ServerSession {
	private LogonServer server;
	private Handler handler;
	
	private int worldId;
	private String region;
	private int flags;
	private String activity;
	private String ip;
	private int country;
	
	private long lastPacket;
	
	private HashMap<String, Profile> online = new HashMap<String, Profile>(64);
	
	public WorldHost(SocketChannel channel, SelectionKey key, LogonServer server) {
		super(channel, key);
		this.server = server;
		this.handler = new AuthHandler(this);
		this.lastPacket = System.currentTimeMillis();
	}
	
	public Collection<Profile> getOnline() {
		return Collections.unmodifiableCollection(online.values());
	}
	
	public LogonServer getServer() {
		return server;
	}
	
	public boolean hasTimedOut() {
		return lastPacket + 10000 < System.currentTimeMillis();
	}
	
	@Override
	public void process() {
		this.lastPacket = System.currentTimeMillis();
		
		ByteBuffer bb = getInput();
		bb.mark();
		RSInputBuffer in = new RSInputBuffer(bb, bb.remaining());
		
		try {
			this.handler.handle(in);
		}
		catch (BufferUnderflowException e) {
			//If an IndexOutOfBoundsException or BufferUnderflowException is thrown here,
			//then the caller catches it and resets the buffer
			bb.reset();
		}
		catch (IndexOutOfBoundsException e) {
			bb.reset();
		}
	}
	
	public void add(Profile profile) {
		online.put(profile.getName().toLowerCase(), profile);
		
		LSOutgoingPacket out = new LSOutgoingPacket(1);
		out.writeByte((byte) 1);
		out.writeByte((byte) this.worldId);
		out.writePJStr1(profile.getName());
		
		for (WorldHost host : server.getSessions()) {
			if (host.isAuthenticated()) {
				host.write(out);
			}
		}
	}
	
	public Profile getPlayer(String name) {
		return online.get(name.toLowerCase());
	}
	
	public void remove(Profile profile) {
		Profile old = online.get(profile.getName().toLowerCase());
		if (old == null) {
			throw new IllegalArgumentException("Profile not online");
		}
		
		online.remove(profile.getName().toLowerCase());
		
		LSOutgoingPacket out = new LSOutgoingPacket(1);
		out.writeByte(0); //Offline
		out.writeByte(this.worldId);
		out.writePJStr1(profile.getName());
		
		for (WorldHost host : server.getSessions()) {
			if (host.isAuthenticated()) host.write(out);
		}
	}
	
	public boolean isAuthenticated() {
		return !(this.handler instanceof AuthHandler);
	}
	
	@Override
	public void close(boolean flush) {
		super.close(flush);
		
		if (this.isAuthenticated()) {
			ArrayList<Profile> profiles = new ArrayList<Profile>(online.values());
			for (Profile p : profiles) {
				this.remove(p); //Notifies servers
			}
			
			LSOutgoingPacket delHost = new LSOutgoingPacket(2);
			delHost.write(0);
			delHost.write(this.worldId);
			
			for (WorldHost host : getServer().getSessions()) {
				if (host.isAuthenticated()) host.write(delHost);
			}
		}
	}
	
	public void write(RSOutputStream out) {
		this.write(ByteBuffer.wrap(out.getPayload()));
	}
	
	public void write(LSOutgoingPacket out) {
		this.write(ByteBuffer.wrap(out.toByteArray()));
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
		ByteBuffer bb = getInput();
		if (bb.hasRemaining()) {
			bb.mark();
			RSInputBuffer in = new RSInputBuffer(bb, bb.remaining());
			
			try {
				this.handler.handle(in);
			}
			finally {
				//If an IndexOutOfBoundsException or BufferUnderflowException is thrown here,
				//then the caller catches it and resets the bufe
			}
		}
	}
	
	public String getHostIP() {
		return this.ip;
	}
	
	public String getName() {
		if (activity != null && activity.isEmpty() == false) {
			return activity;
		}
		return region;
	}
	
	public int getId() {
		return worldId;
	}
	
	@Override
	public String toString() {
		return worldId + ": " + getName();
	}
	
	public int accept(String region, String activity, int flags, int country, String ip) {
		this.region = region;
		this.activity = activity;
		this.flags = flags;
		this.ip = ip;
		this.country = country;
		
		int id = getServer().getFreeWorldId();
		if (id == -1) {
			return -1;
		}
		this.worldId = id;
		return this.worldId;
	}
	
	public String getActivity() {
		return activity;
	}
	
	public int getCountry() {
		return country;
	}
	
	public int getFlags() {
		return this.flags;
	}
}