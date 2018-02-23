package org.maxgamer.rs.tiler.exception;

/**
 * TODO: Document this
 */
public class DimensionException extends RuntimeException {
    public DimensionException() {
    }

    public DimensionException(String s) {
        super(s);
    }

    public DimensionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DimensionException(Throwable throwable) {
        super(throwable);
    }

    public DimensionException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
