package org.maxgamer.rs.events.mob.persona.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * Guaranteed to be thrown when a player is destroyed. This is not cancellable.
 * If you want to try to stop the player logging out, use
 * {@link PlayerLogOutEvent}, though there are ways of bypassing the logout
 * event. This event is not bypassable.
 * @author netherfoam
 */
public class PlayerDestroyEvent extends PlayerEvent {
	public PlayerDestroyEvent(Player p) {
		super(p);
	}
}