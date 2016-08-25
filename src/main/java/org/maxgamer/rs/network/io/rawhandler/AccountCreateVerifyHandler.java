package org.maxgamer.rs.network.io.rawhandler;

import java.nio.ByteBuffer;

import org.maxgamer.rs.cache.XTEAKey;
import org.maxgamer.rs.util.BufferUtils;
import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * @author netherfoam
 */
public class AccountCreateVerifyHandler extends RawHandler {
	
	public AccountCreateVerifyHandler(Session s) {
		super(s);
	}
	
	@Override
	public void handle(RSByteBuffer src) {
		@SuppressWarnings("unused")
		int length = src.readShort();
		
		@SuppressWarnings("unused")
		int protocol = src.readShort(); //637
		src.readByte();
		src.readByte();
		
		int check = src.readByte();
		if (check != 10) {
			Log.debug("Bad, got check of " + check + " should be 10");
			getSession().close(false);
			return;
		}
		
		//1 XTEA key set
		int[] keys = new int[4];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = src.readInt();
		}
		
		XTEAKey key = new XTEAKey(keys);
		
		//10 random ints
		for (int i = 0; i < 10; i++) {
			src.readInt(); //Literally, random integers, never stored.
		}
		src.readShort(); //Random short, never stored
		
		byte[] data = new byte[src.available()];
		src.read(data);
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		key.decipher(bb, 0, bb.limit());
		
		String email = BufferUtils.readRS2String(bb);
		
		bb.get(); //Unknown
		for (int i = 0; i < 7; i++) {
			bb.get();
		}
		
		Log.debug("Account verification for Email " + email);
		
		/*
		 * Return codes are: 2: Valid user, display green mark 7: Server is busy
		 * 9: Account can't be created at this time 20: Username already taken
		 * 22: Please supply a valid username
		 */
		
		//TODO: Forward this request to LogonServer
		/*
		 * if(Profile.isValidUser(email) == false){ getSession().write((byte)
		 * 22); return; }
		 * 
		 * try { //User can't be taken if(Profile.get(email) != null){
		 * getSession().write((byte) 20); return; } } catch (SQLException e) {
		 * e.printStackTrace(); getSession().write((byte) 9); return; }
		 */
		
		//Valid email
		getSession().write((byte) 2);
		
		getSession().setHandler(new DebugHandler(getSession()));
	}
	
}