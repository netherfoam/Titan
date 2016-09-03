package org.maxgamer.rs.model.interfaces.impl.dialogue;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.impl.chat.ChatInterface;

/**
 * @author netherfoam
 */
public abstract class Dialogue extends Interface {

    public Dialogue(Player p) {
        //Dialogue interfaces are part of the chatbox interface.
        super(p, p.getWindow().getInterface(ChatInterface.CHILD_ID), (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 7 : 13), true);
    }

    @Override
    public boolean isServerSidedClose() {
        return false;
    }
}