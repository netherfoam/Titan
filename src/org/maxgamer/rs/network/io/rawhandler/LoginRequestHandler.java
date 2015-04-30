package org.maxgamer.rs.network.io.rawhandler;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.maxgamer.io.InputStreamWrapper;
import org.maxgamer.rs.cache.XTEAKey;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.ScreenSettings;
import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * @author netherfoam
 */
public class LoginRequestHandler extends RawHandler {
	public LoginRequestHandler(Session s) {
		super(s);
	}
	
	@Override
	public void handle(RSByteBuffer b) {
		if (Core.getServer().getLogon().isConnected() == false) {
			try {
				getSession().write(AuthResult.LOGIN_SERVER_OFFLINE.getCode());
				Log.info("LoginServer is offline, so login request has been declined.");
			}
			finally {
				getSession().close(true);
			}
		}
		//JoinRequest request = JoinRequest.decode(getSession(), b);
		InputStreamWrapper in = null;
		String name = null;
		String pass = null;
		int uuid = -1;
		boolean toLobby = false;
		
		try {
			int opcode = b.readByte() & 0xFF;
			Log.debug("Opcode: " + opcode);
			
			//Length of data available. (~280 ish) - Packet size.
			int len = b.readShort(); //Number of bytes remaining
			int version = b.readInt(); //Client version
			getSession().setRevision(version);
			
			b.readShort(); //Possibly a size
			int rsaHeader = b.readByte();
			if (rsaHeader != 10) {
				Log.warning("Invalid RSA Header: " + rsaHeader + ", length: " + len + ", rev: " + version);
			}
			
			//Client seed?
			int[] keys = new int[4];
			for (int i = 0; i < keys.length; i++) {
				keys[i] = b.readInt();
			}
			XTEAKey key = new XTEAKey(keys);
			
			b.readLong(); //Appears to be zero always
			
			pass = b.readPJStr1();
			
			//Client UID?
			b.readLong(); // client key, appears to be 0 always
			b.readInt(); // always 0
			uuid = b.readInt(); // other client key, randomly generated every time client starts
			
			//The rest of the packet is encrypted
			byte[] block = new byte[len - 48 - pass.length()];
			b.read(block);
			
			//Decrypt it
			ByteBuffer bb = ByteBuffer.wrap(block);
			key.decipher(bb, 0, block.length);
			
			//A nice way of reading.
			in = new InputStreamWrapper(block);
			name = in.readString();
			
			if (opcode == 16 || opcode == 18) { //Initial world join or resume (rejoin) request
				toLobby = false;
				in.readByte(); //Unknown..
				
				//Screen settings
				int mode = in.read();
				int width = in.readShort();
				int height = in.readShort();
				boolean active = in.readByte() != 0; //is window selected, I assume.
				
				ScreenSettings ss = getSession().getScreenSettings();
				ss.setDisplayMode(mode);
				ss.setWidth(width);
				ss.setHeight(height);
				ss.setWindowActive(active);
				
				for (int i = 0; i < 24; i++) {
					in.readByte();
				}
				in.readString(); //Settings
				
				in.readInt();
				for (int i = 0; i < 34; i++) {
					in.readInt();
				}
			}
			else if (opcode == 19) {
				toLobby = true;
				//LOBBY
				
				in.readByte(); // screen settings?
				in.readByte();
				for (int i = 0; i < 24; i++) {
					in.readByte();
				}
				
				in.readInt();
				for (int i = 0; i < 34; i++) {
					in.readInt();
				}
				
				//We are left with 4 unknown bytes. On my client they are (in hex) (0x24, 0x57, 0x42, 0x5C)
				while (in.available() > 0) {
					in.readByte();
				}
			}
			else {
				throw new IOException();
			}
		}
		catch (IOException e) {
			//This is caused by the player disconnecting during the login process.
			getSession().close(false);
			return;
		}
		finally {
			if (in != null) in.close();
		}
		
		//if(Core.getServer().getLogon().request(request) == false){
		Core.getServer().getLogon().getAPI().authenticate(getSession(), name, pass, uuid, toLobby);
		/*
		 * Log.info(
		 * "LoginServer has not yet responded to previous join request, so the request has been declined."
		 * ); try{ getSession().write(AuthResult.SYSTEM_UNAVAILABLE.getCode());
		 * } finally{ getSession().close(true); }
		 */
	}
}