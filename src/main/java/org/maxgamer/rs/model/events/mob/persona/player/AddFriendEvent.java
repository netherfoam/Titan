package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.model.events.RSEvent;
import org.maxgamer.rs.network.Client;

/**
 * @author Alva
 */
public class AddFriendEvent extends RSEvent {

    private Client c;
    private String friend;
    private int chatRank = 0;

    public AddFriendEvent(Client p, String fr, int rank) {
        this.c = p;
        this.friend = fr;
        this.chatRank = rank;
    }

    public int getChatRank() {
        return chatRank;
    }

    public String getChatFriend() {
        return friend;
    }

    public Client getListOwner() {
        return c;
    }
}
