package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * An Event called when the player attempts to log out or their connection drops
 * (Eg, if they close the window). This event is cancellable, but can be avoided
 * by simply calling destroy() on the Player. It is used for handling when a
 * player may not log out yet.
 *
 * @author netherfoam
 */
public class PlayerLeaveWorldEvent extends PlayerEvent implements Cancellable {
    private boolean cancel;

    public PlayerLeaveWorldEvent(Player p) {
        super(p);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}