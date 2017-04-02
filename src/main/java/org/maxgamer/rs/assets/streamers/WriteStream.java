package org.maxgamer.rs.assets.streamers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author netherfoam
 */
public class WriteStream {
    private ByteArrayOutputStream out;

    public WriteStream(ByteArrayOutputStream out) {
        this.out = out;
    }

    public <T> WriteStream write(Encoder<T> encoder, T value) throws IOException {
        encoder.encode(out, value);

        return this;
    }
}
