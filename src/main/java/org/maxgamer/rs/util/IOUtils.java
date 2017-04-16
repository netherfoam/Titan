package org.maxgamer.rs.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author netherfoam
 */
public class IOUtils {
    public static void closeQuietly(Closeable closeable) {
        if(closeable == null) return;

        try {
            closeable.close();
        } catch (IOException e) {
            // Quietly
        }
    }
}
