package org.maxgamer.rs.assets.codec;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Abstract Codec class allows encoding and decoding of a binary object from the cache.
 *
 * @author netherfoam
 */
public abstract class Codec {
    /**
     * Constructs a new, blank codec. Constructors should make use of this to initialize
     * some sane defaults
     */
    public Codec() {
        // Empty, this is a new codec being made from scratch
    }

    /**
     * Decode the given ByteBuffer.
     * @param bb the byte buffer
     * @throws IOException if there's an error decoding the buffer
     */
    public abstract void decode(ByteBuffer bb) throws IOException;

    /**
     * Encode this object into a ByteBuffer, which may be consumed by {{@link #decode(ByteBuffer)}} later.
     * @return the encoded buffer, ready to be read from
     * @throws IOException if there's an error encoding the buffer
     */
    public abstract ByteBuffer encode() throws IOException;
}
