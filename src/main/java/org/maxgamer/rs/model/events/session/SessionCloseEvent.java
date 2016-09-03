package org.maxgamer.rs.model.events.session;

import org.maxgamer.rs.network.Session;

/**
 * Represents when a session has its close() method called. The session should
 * not be trusted.
 *
 * @author netherfoam
 */
public class SessionCloseEvent extends SessionEvent {
    public SessionCloseEvent(Session s) {
        super(s);
    }
}