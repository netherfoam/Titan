package org.maxgamer.rs.assets.streamers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author netherfoam
 */
public interface Decoder<T> {
    T decode(ByteArrayInputStream out) throws IOException;
}
