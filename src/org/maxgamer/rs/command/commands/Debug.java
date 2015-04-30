package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Debug implements GenericCommand {
	@Override
	public void execute(CommandSender sender, String[] args) {
		System.out.println(Core.getServer().getNetwork().toString());
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}