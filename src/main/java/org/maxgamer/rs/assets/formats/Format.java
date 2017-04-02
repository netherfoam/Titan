package org.maxgamer.rs.assets.formats;

import org.maxgamer.rs.assets.codec.Codec;
import org.maxgamer.rs.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Format class for decoding information from a codec
 *
 * @author netherfoam
 */
public abstract class Format extends Codec {
    /**
     * Represents a packet whose format could be read, but content is unknown. A preserved
     * packet will be re-encoded, though its meaning is unknown.
     */
    private static class UnknownPacket {
        /**
         * The opcode of the packet
         */
        private final int opcode;

        /**
         * The content in the packet
         */
        private final byte[] content;

        /**
         * Constructs a new unknown packet for preservation
         * @param opcode the opcode (byte)
         * @param content the content
         */
        public UnknownPacket(int opcode, byte[] content) {
            if(opcode <= 0 || opcode > 255) throw new IllegalArgumentException("Opcodes are 1-255");

            this.opcode = opcode;
            this.content = content;
        }
    }

    public class Stasis {
        /**
         * The buffer we're monitoring
         */
        private ByteBuffer buffer;

        /**
         * The position of the buffer when begin() was invoked
         */
        private int startPosition = -1;

        /**
         * The position of the buffer when preserve() was invoked
         */
        private int endPosition = -1;

        /**
         * Constructs a new stasis preserver for the given buffer
         */
        private Stasis(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        /**
         * Marks the beginning of the stasis block
         */
        public void begin() {
            if(startPosition != -1) throw new IllegalStateException("Stasis already begun");

            this.startPosition = buffer.position();
        }

        /**
         * Marks the end of the stasis block
         */
        public void preserve() {
            if(startPosition == -1) throw new IllegalStateException("Stasis hasn't begun");
            if(endPosition != -1) throw new IllegalStateException("Stasis already ended");

            endPosition = buffer.position();
        }

        /**
         * Returns true if the stasis block is ready for preservation, false if it was never finished
         * @return true if the stasis block is ready for preservation
         */
        public boolean isFinished() {
            if(endPosition == -1) return false;

            return true;
        }

        /**
         * The block of data that was preserved. This is never null, but may be empty.
         * @return the data preserved
         * @throws IllegalStateException if the block was never preserved
         */
        public byte[] get() {
            if(!isFinished()) throw new IllegalStateException("Stasis was never finished");

            byte[] data = new byte[endPosition - startPosition];

            int pos = buffer.position();

            buffer.position(startPosition);
            buffer.get(data);
            buffer.position(pos);

            return data;
        }
    }

    /**
     * The list of packets which were preserved, because we don't know what they are.
     */
    private List<UnknownPacket> unknownPackets = new ArrayList<>();

    @Override
    public final void decode(ByteBuffer bb) throws IOException {
        int opcode;

        while((opcode = bb.get() & 0xFF) != 0) {
            Stasis stasis = new Stasis(bb);
            stasis.begin();
            decode(opcode, bb, stasis);

            if(stasis.isFinished()) {
                unknownPackets.add(new UnknownPacket(opcode, stasis.get()));
            }
        }

        if (bb.remaining() > 0) {
            Log.warning("ItemDefinition Buffer remaining " + bb.remaining());
        }
    }

    @Override
    public final ByteBuffer encode() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        encode(out);

        for(UnknownPacket packet : unknownPackets) {
            out.write((byte) packet.opcode);
            out.write(packet.content);
        }

        // End of file marker
        out.write((byte) 0);

        return ByteBuffer.wrap(out.toByteArray());
    }

    public abstract void encode(ByteArrayOutputStream out) throws IOException;
    public abstract void decode(int opcode, ByteBuffer buffer, Stasis stasis) throws IOException;
}
