package org.maxgamer.rs.assets.streamers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author netherfoam
 */
public interface Encoder<T> {
    void encode(ByteArrayOutputStream out, T object) throws IOException;
}
