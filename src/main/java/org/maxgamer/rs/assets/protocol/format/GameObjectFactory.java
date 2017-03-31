package org.maxgamer.rs.assets.protocol.format;

import org.maxgamer.rs.model.map.object.GameObjectProto;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class GameObjectFactory extends FormatFactory<GameObjectProto> {
    public GameObjectFactory() {
        super("objproto");
    }

    @Override
    public GameObjectProto decode(ByteBuffer bb) throws IOException, BufferUnderflowException {
        if (bb.remaining() <= 1) throw new IOException(); //Not valid.

        return GameObjectProto.decode(-1, bb);
    }
}