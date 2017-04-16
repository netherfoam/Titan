package org.maxgamer.rs.assets.formats;

import org.maxgamer.rs.network.io.stream.RSOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a set of bits from a config value that can be interpreted as a set
 *
 * @author netherfoam
 */
public class BitVarConfigFormat extends Format {
    /**
     * The id of the config that is to be interpreted as a bit set
     */
    private int id;

    /**
     * The starting bit to interpret eg bit 10
     */
    private int start;

    /**
     * The ending bit to interpret eg bit 10
     */
    private int end;

    public BitVarConfigFormat(ByteBuffer content) throws IOException {
        this.decode(content);
    }

    public BitVarConfigFormat(int id, int end, int start) {
        this.id = id;
        this.end = end;
        this.start = start;
    }

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {
        RSOutputStream stream = new RSOutputStream();
        stream.writeByte(1); // Opcode
        stream.writeShort(id);
        stream.writeByte(start);
        stream.writeByte(end);

        out.write(stream.getPayload());
    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        if(opcode == 1) {
            id = buffer.getShort() & 0xFFFF;
            start = buffer.get() & 0xFF;
            end = buffer.get() & 0xFF;
            return;
        }

        throw new IllegalArgumentException("Unsupported opcode: " + opcode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitVarConfigFormat that = (BitVarConfigFormat) o;

        if (id != that.id) return false;
        if (end != that.end) return false;

        return start == that.start;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + end;
        result = 31 * result + start;

        return result;
    }
}
