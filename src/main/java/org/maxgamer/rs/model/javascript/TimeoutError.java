package org.maxgamer.rs.model.javascript;

public class TimeoutError extends Error {
    private static final long serialVersionUID = -6996505250345759482L;

    public TimeoutError(String message) {
        super(message);
    }
}