package org.maxgamer.rs.network.protocol;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.EmailStatus;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.io.packet.PacketManager;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.network.io.stream.RSOutputStream;
import org.maxgamer.rs.structure.Util;

import com.mysql.jdbc.NotImplemented;

/**
 * @author netherfoam
 */
public class Lobby637Protocol extends LobbyProtocol {
	public static final PacketManager<LobbyPlayer> PACKET_MANAGER;
	
	static {
		PACKET_MANAGER = new PacketManager<LobbyPlayer>();
		PACKET_MANAGER.setHandler(12, new PacketProcessor<LobbyPlayer>() {
			@Override
			public void process(LobbyPlayer c, RSIncomingPacket p) throws Exception {
				//Heartbeat packet.
				//TODO: We should probably send some kind of response.
			}
		});
		
		PACKET_MANAGER.setHandler(84, new PacketProcessor<LobbyPlayer>() {
			@Override
			public void process(LobbyPlayer c, RSIncomingPacket p) throws Exception {
				//World query packet
				c.sendWorldData();
			}
		});
	}
	
	public Lobby637Protocol(LobbyPlayer p) {
		super(p);
	}
	
	@Override
	public int getRevision() {
		return 639;
	}
	
	@Override
	public PacketManager<LobbyPlayer> getPacketManager() {
		return PACKET_MANAGER;
	}
	
	@Override
	public void sendUpdates() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendMessage(int type, String userFrom, String text) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendMessage(String text) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendFriend(String name, boolean victimOnline, String world) {
		RSOutgoingPacket out = new RSOutgoingPacket(10);
		out.writeByte(0); //boolean 1 = true, else false
		out.writePJStr1(name);//Username
		out.writePJStr1(""); //Display name maybe? Uhm, I can't see anywhere where the client uses this but it reads it and saves it?
		
		out.writeShort(victimOnline ? 1 : 0);//Checks whether online or not.// World ID?
		out.writeByte(0); //byte
		if (victimOnline) {//<col=00FF00> is not necessary here
			//out.writePJStr1(Core.getServer().getDefinition().getWorldId() + ": " + Core.getServer().getDefinition().getText()); //World name
			out.writePJStr1(world);
			out.writeByte(0); //Some boolean, 1=true, else false
		}
		getPlayer().write(out);
	}
	
	@Override
	public void sendUnlockFriendsList() {
		RSOutgoingPacket out = new RSOutgoingPacket(10);
		getPlayer().write(out);
	}
	
	@Override
	public void sendIgnores(String ignore) {
		RSOutgoingPacket out = new RSOutgoingPacket(85);
		
		//Some bool, if last bit is not set, the name is added to the end of the list. 
		//Otherwise it replaces the old name which .equals it?
		//The second last bit, is also some boolean value, which is only used if the
		//last bit is not set.
		out.writeByte(0);
		
		out.writePJStr1(ignore);
		out.writePJStr1(""); //Display name, or empty for same as ignore
		out.writePJStr1(ignore); //Last known as name
		out.writePJStr1(""); //Last known display name or empty for same as last known name
		getPlayer().write(out);
	}
	
	/**
	 * Unlocks/resets the ignores list.
	 */
	@Override
	public void sendUnlockIgnores() {
		getPlayer().write(new RSOutgoingPacket(12));
	}
	
	/**
	 * Runs the client script to remove the given friend
	 * @param name the name of the friend case insensitive
	 */
	@Override
	public void removeFriend(String name) {
		//invoke(removeFriendScript, -1, 5, "", name);
		//TODO
	}
	
	/**
	 * Runs the client script to remove the given ignore
	 * @param name the name of the ignore case insensitive
	 */
	@Override
	public void removeIgnore(String name) {
		//invoke(removeIgnoreScript, name);
		//TODO
	}
	
	@Override
	public void sendAuth(AuthResult result, String lastIp, long lastSeen) {
		RSOutputStream bb = new RSOutputStream(36 + lastIp.length() + getPlayer().getName().length());
		bb.write((byte) 0);
		bb.write((byte) 0);
		bb.write((byte) 0);
		bb.write((byte) 0);
		bb.write((byte) 0);
		bb.writeShort((short) 30); //Membership Days
		bb.writeShort((short) 1); //Recovery Questions
		bb.writeShort((short) 0); //Unread messages
		
		//RuneScape 2 started on day 11745 days after 1970.
		bb.writeShort((short) ((lastSeen / 86400000) - 11745)); //Time since last online
		
		if (Util.isIP(lastIp)) {
			bb.writeInt(Util.IPAddressToNumber(lastIp));
		}
		else {
			bb.writeInt(Util.IPAddressToNumber("0.0.0.0")); //First join 
		}
		bb.write(EmailStatus.NO_EMAIL.getNetworkId()); //Email Status
		bb.writeShort((short) 0);
		bb.writeShort((short) 0);
		bb.write((byte) 0);
		
		bb.writePJStr2(getPlayer().getName());
		bb.writeByte((byte) 0);
		bb.writeInt(1);
		bb.writeShort((short) Core.getServer().getLogon().getWorldId());
		bb.writePJStr2(lastIp);
		
		byte[] data = bb.getPayload();
		getPlayer().getSession().write((byte) data.length);
		getPlayer().getSession().write(data);
	}

	@Override
	public void sendSound(int i, int j, int k) {
		throw new RuntimeException("Not implemented");
	}
}