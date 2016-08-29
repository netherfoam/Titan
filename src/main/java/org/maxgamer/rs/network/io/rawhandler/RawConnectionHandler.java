package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.util.Log;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * @author netherfoam
 */
public class RawConnectionHandler extends RawHandler {
	public RawConnectionHandler(Session s) {
		super(s);
	}
	
	@Override
	public void handle(RSByteBuffer b) {
		int opcode;
		opcode = b.readByte() & 0xFF;
		
		switch (opcode) {
			case 15: //JS5, cache validation 
				getSession().setHandler(new JS5Handler(getSession()));
				break;
			case 14: //Login request/lobby
				getSession().write((byte) 0);
				getSession().setHandler(new LoginRequestHandler(getSession()));
				break;
			case 22:
				//This is when the player attempts to 'Create Account' (Eg after validating email)
				getSession().setHandler(new AccountCreateSubmitHandler(getSession()));
				break;
			case 28:
				getSession().setHandler(new AccountCreateVerifyHandler(getSession()));
				break;
			default:
				Log.warning("Unhandled RawConnectionHandler opcode#" + opcode);
				getSession().setHandler(new DebugHandler(getSession()));
		}
	}
}