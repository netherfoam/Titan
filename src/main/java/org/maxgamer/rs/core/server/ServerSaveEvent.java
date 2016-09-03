package org.maxgamer.rs.core.server;

import org.maxgamer.rs.model.events.RSEvent;

public class ServerSaveEvent extends RSEvent {
    public ServerSaveEvent() {
        super();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}