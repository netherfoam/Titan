package org.maxgamer.rs.model.events.mob.persona.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.persona.PersonaEvent;

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