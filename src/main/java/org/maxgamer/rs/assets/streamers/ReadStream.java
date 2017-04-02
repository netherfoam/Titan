package org.maxgamer.rs.assets.streamers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author netherfoam
 */
public class ReadStream {
    private ByteArrayInputStream input;

    public ReadStream(ByteArrayInputStream input) {
        this.input = input;
    }

    public <T> T read(Decoder<T> decoder) throws IOException {
        return decoder.decode(input);
    }
}
