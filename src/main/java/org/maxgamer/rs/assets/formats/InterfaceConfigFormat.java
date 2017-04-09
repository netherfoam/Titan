package org.maxgamer.rs.assets.formats;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Also known as BConfig, ButtonConfig, GraphicsConfig.
 *
 * @author netherfoam
 */
public class InterfaceConfigFormat extends Format {
    private int id;
    private int start;
    private int end;

    @Override
    public void encode(ByteArrayOutputStream out) throws IOException {
        out.write(1);
        out.write(id >> 8);
        out.write(id);

        out.write(start);
        out.write(end);
    }

    @Override
    public void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException {
        if (opcode == 1) {
            id = buffer.getShort() & 0xFFFF;
            start = buffer.get() & 0xFF;
            end = buffer.get() & 0xFF;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
