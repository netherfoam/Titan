package org.maxgamer.rs.cache;

import java.io.IOException;

/**
 * @author netherfoam
 */
public class EncryptedException extends IOException {
    private static final long serialVersionUID = 2034720445866447527L;

    public EncryptedException(String message, IOException e) {
        super(message, e);
    }

}