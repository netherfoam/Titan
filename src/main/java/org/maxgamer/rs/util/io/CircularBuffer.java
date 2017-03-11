package org.maxgamer.rs.util.io;

import java.io.InputStream;
import java.io.OutputStream;

public class CircularBuffer implements ByteReader, ByteWriter {
    /**
     * The buffer data
     */
    protected byte[] data;

    /**
     * the index to write at next. When this equals readpos, we have no data available
     */
    protected int writepos = 0;

    /**
     * The index of the next byte read.  When this equals readpos, we have no data available
     */
    protected int readpos = 0;

    /**
     * The position that was marked, we cannot advance past this.
     */
    protected int mark = 0;

    /**
     * Constructs a circular buffer of the given size.
     * This buffer will auto-resize in factors of two
     * of the given initial size when more space
     * is required.
     *
     * @param initSize the given size
     */
    public CircularBuffer(int initSize) {
        if (initSize <= 0) {
            throw new IllegalArgumentException("May not create a circular buffer with size <= 0, given: " + initSize);
        }
        this.data = new byte[initSize + 1];
    }

    /**
     * Writes the given byte, resizing if necessary
     *
     * @param b the byte to write
     * @
     */
    public void writeByte(byte b) {
        synchronized (this) {
            if ((writepos + 1) % data.length == mark) {
                resize((this.data.length - 1) * 2);
            }

            data[writepos++] = b;
            if (writepos >= data.length) {
                writepos = 0;
            }
        }
    }

    private void resize(int newSize) {
        int oldRead = this.readpos;
        int oldMark = this.mark;
        this.reset(); //We want all of the data from our mark (inclusive) up to our writepos (exclusive)

        int totalWritable = this.available();
        if (newSize < totalWritable) throw new IllegalArgumentException("Cannot fit " + available() + " bytes into a " + newSize + " array!");

        byte[] newData = new byte[newSize + 1]; //+1, see constructor.
        this.read(newData, 0, totalWritable);

        this.mark = 0;
        if (oldRead >= oldMark) {
            this.readpos = oldRead - oldMark;
        } else {
            this.readpos = (data.length - oldMark + oldRead);

        }
        this.writepos = totalWritable;

        this.data = newData;
    }

    /**
     * Reads a byte.
     *
     * @return The byte read
     * @throws IndexOutOfBoundsException if the buffer is empty
     */
    public byte readByte() {
        synchronized (this) {
            if (readpos == writepos) {
                throw new IndexOutOfBoundsException("The buffer is empty!");
            }

            byte b = data[readpos++];

            if (readpos >= data.length) {
                readpos = 0;
            }

            return b;
        }
    }

    /**
     * @return the number of available bytes
     */
    public int available() {
        if (readpos <= writepos) {
            return writepos - readpos;
        } else {
            return (data.length) - (readpos - writepos);
        }
    }

    public void writeShort(short s) {
        writeByte((byte) (s >> 8));
        writeByte((byte) s);
    }

    public void writeInt(int i) {
        writeShort((short) (i >> 16));
        writeShort((short) i);
    }

    public void writeLong(long l) {
        writeInt((int) (l >> 32));
        writeInt((int) l);
    }

    public void writeFloat(float f) {
        writeInt(Float.floatToIntBits(f));
    }

    public void writeDouble(double d) {
        writeLong(Double.doubleToRawLongBits(d));
    }

    public short readShort() {
        return (short) (((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
    }

    public int readInt() {
        return (((readShort() & 0xFFFF) << 16) | (readShort() & 0xFFFF));
    }

    public long readLong() {
        return (((readInt()) << 0) | (readInt()));
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public boolean isEmpty() {
        return readpos == writepos;
    }

    public void mark() {
        this.mark = this.readpos;
    }

    public void reset() {
        this.readpos = this.mark;
    }

    @Override
    public void write(byte[] src, int start, int end) {
        synchronized (this) {
            while (start < end) {
                this.writeByte(src[start++]);
            }
        }
    }

    @Override
    public void write(byte[] src) {
        this.write(src, 0, src.length);
    }

    @Override
    public int read(byte[] dest, int start, int end) {
        int read = end - start;
        synchronized (this) {
            while (start < end) {
                dest[start++] = readByte();
            }
        }

        return read;
    }

    @Override
    public int read(byte[] dest) {
        return read(dest, 0, dest.length);
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) {
                CircularBuffer.this.writeByte((byte) b);
            }
        };
    }

    @Override
    public InputStream getInputStream() {
        return new InputStream() {
            @Override
            public int read() {
                try {
                    return CircularBuffer.this.readByte() & 0xFF; //Must be 0-255
                } catch (IndexOutOfBoundsException e) {
                    return -1;
                }
            }
        };
    }
}