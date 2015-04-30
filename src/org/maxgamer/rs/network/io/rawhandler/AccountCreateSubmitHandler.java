package org.maxgamer.rs.network.io.rawhandler;

import java.nio.ByteBuffer;

import org.maxgamer.rs.cache.XTEAKey;
import org.maxgamer.rs.lib.BufferUtils;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * @author netherfoam
 */
public class AccountCreateSubmitHandler extends RawHandler {
	public AccountCreateSubmitHandler(Session s) {
		super(s);
	}
	
	@SuppressWarnings("unused")
	@Override
	public void handle(RSByteBuffer src) {
		int length = src.readShort();
		
		int protocol = src.readShort(); //637
		src.readByte();
		src.readByte();
		
		int check = src.readByte();
		if (check != 10) {
			Log.debug("Bad, got check of " + check + " should be 10");
			getSession().close(false);
			return;
		}
		
		int[] keys = new int[4];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = src.readInt();
		}
		
		for (int i = 0; i < 10; i++) {
			src.readInt(); //Randomly generated, never stored garbage
		}
		src.readShort(); //Randomly generated garbage, never stored
		
		XTEAKey key = new XTEAKey(keys);
		byte[] data = new byte[src.available()];
		src.read(data);
		ByteBuffer bb = ByteBuffer.wrap(data);
		key.decipher(bb, 0, bb.limit());
		
		String email = BufferUtils.readRS2String(bb);
		bb.getShort();
		String pass = BufferUtils.readRS2String(bb);
		bb.getLong();
		bb.get();
		bb.get();
		
		//24 bytes, some "read from file"?
		//These seem to all be -1
		for (int i = 0; i < 24; i++) {
			bb.get();
		}
		
		//Unknown
		boolean b = bb.get() != 0;
		if (b) {
			//TODO: This still has to be identified.
			Log.debug("-> String: " + BufferUtils.readRS2String(bb));
		}
		
		int age = bb.get() & 0xFF; //Age of player
		boolean subscribe = bb.get() != 0; //True to subscribe, false otherwise
	}
	
}