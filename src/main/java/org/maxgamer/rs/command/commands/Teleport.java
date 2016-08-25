package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class Teleport implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		if (args.length < 2) {
			p.sendMessage("Arg0: x");
			p.sendMessage("Arg1: y");
			p.sendMessage("Arg2: z (optional)");
			return;
		}
		
		try {
			Location dest = new Location(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args.length >= 3 ? Integer.parseInt(args[2]) : 0);
			p.teleport(dest);
			p.sendMessage("Teleported to " + dest);
		}
		catch (NumberFormatException e) {
			p.sendMessage("Invalid number given for location, given " + e.getMessage());
			return;
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}