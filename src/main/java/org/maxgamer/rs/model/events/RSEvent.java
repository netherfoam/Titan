package org.maxgamer.rs.model.events;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.IllegalThreadException;
import org.maxgamer.rs.event.Event;

/**
 * @author netherfoam
 */
public class RSEvent extends Event {
    /**
     * Calls this action through the ActionManager. Convenience method
     */
    public void call() {
        if (!this.isAsync() && !Core.getServer().getThread().isServerThread()) {
            throw new IllegalThreadException("Events must be called from the Server thread, current thread is " + Thread.currentThread().toString());
        }
        Core.getServer().getEvents().callEvent(this);
    }

    public boolean isAsync() {
        return false;
    }
}