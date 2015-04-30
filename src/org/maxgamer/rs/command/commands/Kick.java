package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Kick implements GenericCommand {
	
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if (args.length < 1) {
			sender.sendMessage("Usage:");
			sender.sendMessage("Arg0: Persona Name");
			return;
		}
		
		Persona target = Core.getServer().getPersonas().getPersona(args[0], true);
		if (target == null) {
			sender.sendMessage("No such player found: " + args[0]);
			return;
		}
		
		target.destroy();
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}