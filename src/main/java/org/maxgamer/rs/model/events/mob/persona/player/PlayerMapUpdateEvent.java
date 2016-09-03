package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * Called when the player is sent a new map request - Eg, they must now load a
 * set of regions of the world map.
 *
 * @author netherfoam
 */
public class PlayerMapUpdateEvent extends PlayerEvent implements Cancellable {
    private boolean first;
    private boolean cancel;

    /**
     * @param p     The player
     * @param first If this is the first time the player is receiving the map
     *              since connection.
     */
    public PlayerMapUpdateEvent(Player p, boolean first) {
        super(p);
        this.first = first;
    }

    /**
     * Returns true if this is the first map update the player has received
     * since connection
     *
     * @return true if this is the first map update the player has received
     * since connection
     */
    public boolean isInitialUpdate() {
        return first;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}