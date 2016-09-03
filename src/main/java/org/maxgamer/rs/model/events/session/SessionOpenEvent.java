package org.maxgamer.rs.model.events.session;

import org.maxgamer.rs.network.Session;

/**
 * Represents when a new connection is created with the server. This connection
 * is not yet authenticated, and should not be trusted.
 * <p>
 * This event is called when a remote client opens a socket with the server.
 *
 * @author netherfoam
 */
public class SessionOpenEvent extends SessionEvent {
    public SessionOpenEvent(Session s) {
        super(s);
    }
}