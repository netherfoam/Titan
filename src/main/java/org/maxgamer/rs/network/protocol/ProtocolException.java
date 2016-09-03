package org.maxgamer.rs.network.protocol;

import java.io.IOException;

/**
 * @author netherfoam
 */
public class ProtocolException extends IOException {
    private static final long serialVersionUID = 3465782991122791240L;

    public ProtocolException(String msg) {
        super(msg);
    }
}