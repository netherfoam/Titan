package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.network.Client;

/**
 * @author netherfoam
 */
@CmdName(names = { "users" })
public class Clients implements GenericCommand {
	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage("Active Clients: (" + Core.getServer().getClients().size() + ")");
		for (Client client : Core.getServer().getClients()) {
			sender.sendMessage("Connected: " + client.getName());
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.USER;
	}
	
}