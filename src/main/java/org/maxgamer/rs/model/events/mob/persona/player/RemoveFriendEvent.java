package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.model.events.RSEvent;
import org.maxgamer.rs.network.Client;

/**
 * @author Alva
 */
public class RemoveFriendEvent extends RSEvent {

    private String friend;
    private Client c;

    public RemoveFriendEvent(Client p, String fr) {
        this.c = p;
        this.friend = fr;
    }

    public String getChatFriend() {
        return friend;
    }

    public Client getListOwner() {
        return c;
    }

}
