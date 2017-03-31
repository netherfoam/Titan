package org.maxgamer.rs.logon;

import org.maxgamer.rs.assets.protocol.RSInputStream;
import org.maxgamer.rs.network.io.stream.RSInputBuffer;
import org.maxgamer.rs.util.io.ByteReader;
import org.maxgamer.rs.util.io.CircularBuffer;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Represents a packet received from the client
 *
 * @author netherfoam
 */
public class LSIncomingPacket extends RSInputBuffer implements ByteReader {
    //Value 0-255
    private int opcode;

    private LSIncomingPacket(int opcode, RSInputBuffer b, int length) throws BufferUnderflowException, IndexOutOfBoundsException {
        super(b, length);
        this.opcode = opcode;
    }

    private LSIncomingPacket(int opcode, CircularBuffer b, int length) throws IOException {
        super(b, length);
        this.opcode = opcode;
    }

    public LSIncomingPacket(int opcode, ByteBuffer bb, int length) {
        super(bb, length);
        this.opcode = opcode;
    }

    public static LSIncomingPacket parse(RSInputBuffer b) throws BufferUnderflowException, IndexOutOfBoundsException {
        int opcode = b.readByte() & 0xFF;
        int length = b.readShort() & 0xFFFF;

        return new LSIncomingPacket(opcode, b, length);
    }

    public static LSIncomingPacket parse(RSInputStream in) throws IOException, BufferUnderflowException {
        int opcode = in.read();
        if (opcode == -1) throw new BufferUnderflowException();

        int l1 = in.read();
        if (l1 == -1) throw new BufferUnderflowException();
        int l2 = in.read();
        if (l2 == -1) throw new BufferUnderflowException();

        int length = ((l1 << 8) | l2) & 0xFFFF;

        ByteBuffer bb = ByteBuffer.allocate(length);
        while (bb.hasRemaining()) {
            int v = in.read();
            if (v == -1) throw new BufferUnderflowException();
            bb.put((byte) v);
        }
        bb.flip();
        return new LSIncomingPacket(opcode, bb, length);
    }

    public static LSIncomingPacket parse(CircularBuffer b) throws IOException {
        int opcode = b.readByte() & 0xFF;
        int length = b.readShort();

        return new LSIncomingPacket(opcode, b, length);
    }

    public int getOpcode() {
        return opcode;
    }
}