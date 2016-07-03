package org.maxgamer.rs.logon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class OpcodeDecoder<T> {
	private HashMap<Integer, Method> handlers = new HashMap<Integer, Method>();
	
	public OpcodeDecoder() {
		for (Method method : this.getClass().getMethods()) {
			Opcode op = method.getAnnotation(Opcode.class);
			if (op == null) continue;
			handlers.put(op.opcode(), method);
		}
	}
	
	public void decode(int opcode, T in) {
		Method m = handlers.get(opcode);
		if (m == null) {
			System.out.println("Bad opcode given, got " + opcode + " but can handle " + handlers.keySet());
			return;
		}
		
		try {
			m.invoke(this, in);
		}
		catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}