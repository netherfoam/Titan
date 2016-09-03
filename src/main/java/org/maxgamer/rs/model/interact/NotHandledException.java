package org.maxgamer.rs.model.interact;

/**
 * Raised when an InteractionHandler does not support the requested argument types or values - indicating
 * that another handler should be used.
 *
 * @author netherfoam
 */
public class NotHandledException extends Exception {
    private static final long serialVersionUID = 6572211890281845430L;
}