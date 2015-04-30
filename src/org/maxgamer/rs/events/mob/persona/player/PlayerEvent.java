package org.maxgamer.rs.events.mob.persona.player;

import org.maxgamer.rs.events.mob.persona.PersonaEvent;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class PlayerEvent extends PersonaEvent {
	public PlayerEvent(Player p) {
		super(p);
	}
	
	@Override
	public Player getMob() {
		return (Player) super.getMob();
	}
}