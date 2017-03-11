package org.maxgamer.rs.network.server;

import org.maxgamer.rs.core.server.Server;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.structure.ServerHost;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * A simple implementation of ServerHost that uses a Session class. This is the
 * server for all client IO
 *
 * @author netherfoam
 */
public class RS2Server extends ServerHost<Session> {
    /**
     * A map of session ID to session, these sessions are guaranteed to be still
     * open on our side.
     */
    private HashMap<Integer, Session> sessions = new HashMap<>();

    /**
     * Constructs a new RS2Server for the given port and the given Server
     *
     * @param port   the port
     * @param server the server
     * @throws IOException if the port could not be bound
     */
    public RS2Server(int port, Server server) throws IOException {
        super(port);
    }

    @Override
    public Session connect(SocketChannel channel, SelectionKey key) {
        Session s = new Session(this, channel, key);

        sessions.put(s.getSessionId(), s);

        return s;
    }

    @Override
    public void stop() {
        for (Session s : this.getSessions()) {
            s.close(false);
        }
        super.stop();
    }

    /**
     * Gets a session where session.getSessionId() == id, or null if not found.
     * Session is guaranteed to be open on our side of the connection.
     *
     * @param id the ID for the session
     * @return the session or null if not found
     */
    public Session getSessionByID(int id) {
        return sessions.get(id);
    }

    /**
     * Used by Session.close() to notify us a session is closing. This removes
     * it from the internal map of session ID to session, thus nullifying calls
     * to {@link RS2Server#getSessionByID(int)}.
     *
     * @param s the session that is closing
     */
    public void onClose(Session s) {
        sessions.remove(s.getSessionId());
    }
}