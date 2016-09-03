package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerLeaveWorldEvent;
import org.maxgamer.rs.model.interfaces.SideInterface;

/**
 * @author netherfoam
 */
public class ExitInterface extends SideInterface {
    public static final int BUTTON_LOBBY = 5;
    public static final int BUTTON_LOGIN = 10;

    public ExitInterface(Player p) {
        //220 or 105
        super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 220 : 105));
        setChildId(182);
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {
        PlayerLeaveWorldEvent e;
        switch (buttonId) {
            case BUTTON_LOBBY:
                e = new PlayerLeaveWorldEvent(getPlayer());
                e.call();
                if (e.isCancelled()) {
                    return;
                }

                player.getProtocol().logout(true);
                player.destroy();
                break;
            case BUTTON_LOGIN:
                e = new PlayerLeaveWorldEvent(getPlayer());
                e.call();
                if (e.isCancelled()) {
                    return;
                }

                player.getProtocol().logout(false);
                player.destroy();
                break;
        }
    }

    @Override
    public boolean isMobile() {
        return true;
    }
}