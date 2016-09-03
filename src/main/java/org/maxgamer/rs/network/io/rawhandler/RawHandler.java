package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

/**
 * raw bytes received from a session's connection. It is the duty of this
 * handler to disperse the data as required, eg, handle the login protocol, or
 * read incoming packets and notify key parts of the server.
 *
 * @author netherfoam
 */
public abstract class RawHandler {
    private Session s;

    /**
     * Creates a new handler for the given session's data. This does not call
     * session.setHandler(), that is your job.
     *
     * @param s
     */
    public RawHandler(Session s) {
        this.s = s;
    }

    /**
     * The session this handler is attached to
     *
     * @return
     */
    public Session getSession() {
        return s;
    }

    /**
     * Handles the data from the given byte stream appropriately. Each handler
     * implementation should do something very differently with this data. This
     * method will be called when either 1) new data is received from the
     * client, or 2) the Session.setHandler() method is called while there is
     * still data which has not been processed by the connection.
     *
     * @param b The data which has been read in from the client. Be sure to call
     *          the mark() method when you read successful data, and call the
     *          reset() method if not enough data is available to make a decision
     *          (Eg, wait for more data to pile up before we can use it!)
     */
    public abstract void handle(RSByteBuffer in);
}