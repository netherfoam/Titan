package org.maxgamer.rs.logon.logon.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.logon.Profile;
import org.maxgamer.rs.logon.logon.LogonServer;
import org.maxgamer.rs.logon.logon.WorldHost;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "hosts" })
public class Players implements GenericCommand {
	
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		for (WorldHost host : LogonServer.getLogon().getSessions()) {
			sender.sendMessage(host.getId() + " '" + host.getName() + "' @" + host.getIP().getAddress().getHostAddress());
			for (Profile p : host.getOnline()) {
				sender.sendMessage(p.getName());
			}
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}