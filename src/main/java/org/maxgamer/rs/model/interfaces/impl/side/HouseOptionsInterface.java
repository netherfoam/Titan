package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.SideInterface;

/**
 * @author netherfoam
 */
public class HouseOptionsInterface extends SideInterface {

    public HouseOptionsInterface(Player p) {
        super(p, (short) 99);
        setChildId(398);
    }

    @Override
    public boolean isMobile() {
        return true;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {
        switch (buttonId) {
            case 19:
                getPlayer().getWindow().close(this);
                getPlayer().getWindow().open(new SettingsInterface(player));
                break;
            case 15://Building mode ON
            case 1://Building mode OFF
            case 26://In House
            case 25://At Portal
            case 27://Expel Guests
            case 29://Leave House
                break;
        }
    }
}