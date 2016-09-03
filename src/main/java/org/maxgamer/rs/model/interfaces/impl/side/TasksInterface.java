package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.SideInterface;

/**
 * @author netherfoam
 */
public class TasksInterface extends SideInterface {
    public TasksInterface(Player p) {
        super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 203 : 88));
        setChildId(1056);
    }

    @Override
    public boolean isMobile() {
        return true;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {

    }
}
