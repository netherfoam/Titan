package org.maxgamer.rs.assets.protocol.format;

import java.nio.ByteBuffer;

public abstract class FormatFactory<T> {
    private String extension;

    public FormatFactory(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public abstract T decode(ByteBuffer bb) throws Throwable;
}