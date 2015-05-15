package org.maxgamer.rs.logonv4.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.logonv4.LSOutgoingPacket;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.Session;

/**
 * @author netherfoam
 */
public class LogonAPI {
	protected static class AuthRequest {
		protected Session session;
		protected String name;
		protected String pass;
		protected int clientUUID;
		protected boolean lobby;
		
		protected AuthRequest() {
		}
	}
	
	public static class RemoteWorld {
		protected HashSet<String> players = new HashSet<>();
		protected String ip;
		protected String name;
		protected String activity;
		protected int country;
		protected int flags;
		protected int worldId;
		
		protected RemoteWorld() {
		}
		
		public boolean isOnline(String name) {
			return players.contains(name.toLowerCase());
		}
		
		public String getIP() {
			return ip;
		}
		
		public String getName() {
			return name;
		}
		
		public String getActivity() {
			return activity;
		}
		
		public int getCountry() {
			return country;
		}
		
		public int getFlags() {
			return flags;
		}
		
		public int getWorldId() {
			return this.worldId;
		}
		
		public Collection<String> getClients() {
			return Collections.unmodifiableCollection(players);
		}
		
		public int size() {
			return players.size();
		}
	}
	
	protected HashMap<Integer, AuthRequest> authRequests = new HashMap<>();
	protected HashMap<Integer, RemoteWorld> worlds = new HashMap<>();
	
	private LogonConnection game;
	
	public LogonAPI(LogonConnection game) {
		this.game = game;
	}
	
	public RemoteWorld getWorld(int id) {
		return worlds.get(id);
	}
	
	public Collection<RemoteWorld> getWorlds() {
		return Collections.unmodifiableCollection(worlds.values());
	}
	
	public boolean authenticate(Session session, String name, String pass, int clientUUID, boolean lobby) {
		AuthRequest r = new AuthRequest();
		r.session = session;
		r.name = name;
		r.pass = pass;
		r.clientUUID = clientUUID;
		r.lobby = lobby;
		
		authRequests.put(session.getSessionId(), r);
		
		LSOutgoingPacket out = new LSOutgoingPacket(1);
		
		out.writeInt(session.getSessionId());
		out.writePJStr1(name);
		out.writePJStr1(pass);
		out.writePJStr1(session.getIP().getAddress().getHostAddress());
		Log.debug("Requesting auth for " + name);
		try {
			game.write(out);
			return true;
		}
		catch (IOException e) {
			Log.debug("Failed to authenticate - login server offline");
			return false;
		}
	}
	
	public boolean leave(Client client) {
		String name = client.getName();
		LSOutgoingPacket out = new LSOutgoingPacket(2);
		out.writePJStr1(name);
		String s = client.serialize().toString();
		out.writeInt(s.length());
		for (int i = 0; i < s.length(); i++)
			out.write(s.charAt(i));
		
		try {
			game.write(out);
			return true;
		}
		catch (IOException e) {
			Log.debug("Failed to leave - Logon connection is down");
			return false;
		}
	}
	
	public boolean save(Collection<Client> clients) {
		LSOutgoingPacket out = new LSOutgoingPacket(3);
		for (Client c : clients) {
			out.writePJStr1(c.getName());
			String s = c.serialize().toString();
			out.writeInt(s.length());
			for (int i = 0; i < s.length(); i++){
				out.write(s.charAt(i));
			}
		}
		
		try {
			game.write(out);
			Log.debug("Saved " + clients);
			return true;
		}
		catch (IOException e) {
			Log.debug("Failed to save - Logon connection is down");
			return false;
		}
	}
}