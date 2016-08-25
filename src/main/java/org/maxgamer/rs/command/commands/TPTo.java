package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class TPTo implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		if (args.length < 0) {
			p.sendMessage("Arg0 must be the user to teleport.");
			return;
		}
		
		Persona victim = Core.getServer().getPersonas().getPersona(args[0], true);
		if (victim == null) {
			p.sendMessage("Target " + args[0] + " not online.");
			return;
		}
		Location dest = victim.getLocation();
		
		p.teleport(dest);
		p.sendMessage("Teleported to " + victim.getName());
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}