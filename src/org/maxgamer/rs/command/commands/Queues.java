package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "actions" })
public class Queues implements GenericCommand {
	
	@Override
	public void execute(CommandSender s, String[] args) {
		s.sendMessage("ActionQueues for all players: ");
		for (Persona p : Core.getServer().getPersonas()) {
			s.sendMessage(p + ": " + p.getActions());
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}