package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.logonv4.game.LogonAPI.RemoteWorld;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Servers implements GenericCommand {
	
	@Override
	public void execute(CommandSender s, String[] args) {
		for (RemoteWorld server : Core.getServer().getLogon().getAPI().getWorlds()) {
			s.sendMessage("Server " + server.getWorldId() + ": " + server.getName() + " @" + server.getIP() + ", Online: " + server.size());
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}