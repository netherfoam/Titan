package org.maxgamer.rs.logon.logon;

import java.nio.ByteBuffer;

import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.logon.LSOutgoingPacket;
import org.maxgamer.rs.logon.Profile;
import org.maxgamer.rs.network.io.stream.RSInputBuffer;

/**
 * Validates a connection from a potential Game Server by a password. This must be done in order to prevent any unauthorised access.
 * 
 * @author netherfoam
 */
public class AuthHandler implements Handler {
	private WorldHost host;
	
	public AuthHandler(WorldHost host) {
		this.host = host;
	}
	
	@Override
	public void handle(RSInputBuffer in) {
		String pass = in.readPJStr1();
		@SuppressWarnings("unused")
		int version = in.readInt();
		
		if (LogonServer.getLogon().isHostPass(pass)) {
			String region = in.readPJStr1();
			String activity = in.readPJStr1();
			int country = in.readByte();
			int flags = in.readByte();
			String ip = in.readPJStr1();
			
			int id = host.accept(region, activity, flags, country, ip);
			
			if (id != -1) {
				host.write(ByteBuffer.wrap(new byte[] { 1, (byte) id }));
				host.setHandler(new GameDecoder(host));
				
				//Notify all currently connected servers about the new server
				LSOutgoingPacket out = new LSOutgoingPacket(2);
				out.writeByte(1);
				out.writeByte(id);
				
				out.writePJStr1(ip);
				out.writePJStr1(region);
				out.writePJStr1(activity);
				out.writeByte((byte) country);
				out.writeByte((byte) flags);
				
				for (WorldHost server : host.getServer().getSessions()) {
					if (server.isAuthenticated()) {
						server.write(out);
					}
				}
				
				//Notify the new server about all currently connected servers
				for (WorldHost server : host.getServer().getSessions()) {
					if (server.isAuthenticated() == false) continue;
					
					out = new LSOutgoingPacket(2);
					out.writeByte(1);
					out.writeByte(server.getId());
					
					out.writePJStr1(server.getHostIP());
					out.writePJStr1(server.getName());
					out.writePJStr1(server.getActivity());
					out.writeByte((byte) server.getCountry());
					out.writeByte((byte) server.getFlags());
					
					this.host.write(out);
				}
				
				//Notify the new server about all currently connected players
				for (WorldHost host : this.host.getServer().getSessions()) {
					if (host.isAuthenticated() == false) continue;
					
					for (Profile profile : host.getOnline()) {
						LSOutgoingPacket join = new LSOutgoingPacket(1);
						join.writeByte((byte) 1);
						join.writeByte((byte) host.getId());
						join.writePJStr1(profile.getName());
						this.host.write(join);
					}
				}
				
				Log.info(host + " authenticated");
				return;
			}
		}
		Log.info(host + " rejected");
		host.write(ByteBuffer.wrap(new byte[] { 0 }));
		host.close(true);
	}
}