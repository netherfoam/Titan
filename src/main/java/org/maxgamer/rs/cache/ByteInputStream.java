package org.maxgamer.rs.cache;

public class ByteInputStream {

    public byte[] buffer;
    public int pos;

    public ByteInputStream(byte[] data) {
        buffer = data;
        pos = 0;
    }

    public int readExtendedSmart() {
        int total = 0;
        int smart = readSmart();
        while (smart == 0x7FFF) {
            smart = readSmart();
            total += 0x7FFF;
        }
        total += smart;
        return total;
    }

    public int readSmart() {
        int i = buffer[pos] & 0xff;
        if (i >= 128) return readUShort() - 32768;
        return readUByte();
    }

    public int readUShort() {
        pos += 2;
        return ((buffer[pos - 2] & 0xff) << 8) | (buffer[pos - 1] & 0xff);
    }

    public int readByte() {
        return buffer[pos++];
    }

    public int readUByte() {
        return buffer[pos++] & 0xff;
    }
}