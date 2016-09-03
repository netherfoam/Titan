package org.maxgamer.rs.model.interfaces.impl.chat;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;

/**
 * @author netherfoam
 */
public class ChatInterface extends Interface {
    public static final short CHILD_ID = (short) 752;

    public ChatInterface(Player p) {
        super(p, p.getWindow(), (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 192 : 69), true);
        this.setChildId(CHILD_ID);
    }

    @Override
    public boolean isServerSidedClose() {
        return true;
    }

    @Override
    public boolean isMobile() {
        return true;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {

    }
}