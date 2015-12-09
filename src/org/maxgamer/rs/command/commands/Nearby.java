package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Nearby implements PlayerCommand {
	@Override
	public void execute(Player player, String[] args) throws Exception {
		if (args.length < 1) {
			player.sendMessage("Args0: Radius in Tiles");
			return;
		}
		int radius;
		try {
			radius = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException e) {
			player.sendMessage(args[0] + " is not a valid radius.");
			return;
		}
		
		for (Entity g : player.getLocation().getNearby(Entity.class, radius)) {
			player.sendMessage("Near: " + g.toString() + " Dist: " + String.format("%.1f", Math.sqrt(g.getLocation().distanceSq(player.getLocation()))));
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}