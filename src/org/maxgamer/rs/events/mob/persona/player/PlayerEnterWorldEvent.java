package org.maxgamer.rs.events.mob.persona.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * Called when a player enters the world, eg. Logs in from the lobby.
 * @author netherfoam
 */
public class PlayerEnterWorldEvent extends PlayerEvent {
	public PlayerEnterWorldEvent(Player p) {
		super(p);
	}
}