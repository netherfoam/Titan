package org.maxgamer.rs.util.io;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteWriter {
    void writeByte(byte b) throws IOException;

    void writeShort(short s) throws IOException;

    void writeInt(int i) throws IOException;

    void writeLong(long l) throws IOException;

    void writeFloat(float f) throws IOException;

    void writeDouble(double d) throws IOException;

    void write(byte[] src, int start, int end) throws IOException;

    void write(byte[] src) throws IOException;

    OutputStream getOutputStream();
}