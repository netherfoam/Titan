package org.maxgamer.rs.util.io;

import java.io.IOException;
import java.io.InputStream;

public interface ByteReader {
    byte readByte() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    int available() throws IOException;

    boolean isEmpty();

    int read(byte[] dest, int start, int end) throws IOException;

    int read(byte[] dest) throws IOException;

    InputStream getInputStream();

    void mark();

    void reset();
}