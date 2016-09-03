package org.maxgamer.rs.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
    private ByteBuffer bb;

    public ByteBufferInputStream(ByteBuffer buffer) {
        this.bb = buffer;
    }

    @Override
    public int read() throws IOException {
        try {
            return bb.get() & 0xFF;
        } catch (BufferUnderflowException e) {
            return -1;
        }
    }

    @Override
    public int available() {
        return bb.remaining();
    }

}