package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Window;

/**
 * @author netherfoam
 */
public class WindowClickEvent extends PlayerEvent implements Cancellable {
    private Window interf;
    private int opcode;
    private int button;
    private int slot;
    private int itemId;
    private boolean cancel;

    public WindowClickEvent(Player p, Window interf, int opcode, int button, int slot, int itemId) {
        super(p);
        this.interf = interf;
        this.opcode = opcode;
        this.button = button;
        this.slot = slot;
        this.itemId = itemId;
    }

    public Window getInterface() {
        return interf;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getButton() {
        return button;
    }

    public int getSlot() {
        return slot;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}