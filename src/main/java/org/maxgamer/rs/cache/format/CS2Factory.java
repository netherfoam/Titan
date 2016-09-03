package org.maxgamer.rs.cache.format;

import java.nio.ByteBuffer;

public class CS2Factory extends FormatFactory<CS2> {

    public CS2Factory() {
        super("cs2");
    }

    @Override
    public CS2 decode(ByteBuffer bb) throws Throwable {
        return CS2.decode(-1, bb);
    }
}