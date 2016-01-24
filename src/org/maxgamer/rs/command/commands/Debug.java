package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class Debug implements PlayerCommand {
	@Override
	public void execute(Player p, String[] args) {
		Location l = p.getLocation();
		p.sendMessage("Location: " + l);
		p.sendMessage("isLoaded: " + p.getMap().isLoaded(l.getChunkX(), l.getChunkY(), l.z));
		p.sendMessage("isDestroyed(): " + p.isDestroyed() + " isLoaded(): " + p.isLoaded());
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}