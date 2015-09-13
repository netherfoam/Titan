package org.maxgamer.rs.cache.format;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.maxgamer.rs.definition.GameObjectProto;

public class GameObjectFactory extends FormatFactory<GameObjectProto>{
	public GameObjectFactory() {
		super("objproto");
	}

	@Override
	public GameObjectProto decode(ByteBuffer bb) throws IOException, BufferUnderflowException {
		if(bb.remaining() <= 1) throw new IOException(); //Not valid.
		
		return GameObjectProto.decode(-1, bb);
	}
}