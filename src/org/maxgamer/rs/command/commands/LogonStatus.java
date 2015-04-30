package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.logonv4.game.LogonConnection;
import org.maxgamer.rs.logonv4.game.LogonAPI.RemoteWorld;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class LogonStatus implements GenericCommand {
	@Override
	public void execute(CommandSender sender, String[] args) {
		LogonConnection l = Core.getServer().getLogon();
		sender.sendMessage("Current World ID: " + l.getWorldId());
		sender.sendMessage("Running: " + l.isRunning() + " Connected: " + l.isConnected());
		for (RemoteWorld w : l.getAPI().getWorlds()) {
			sender.sendMessage("World [" + w.getWorldId() + "]:");
			sender.sendMessage(" - Name: " + w.getName());
			sender.sendMessage(" - IP: " + w.getIP());
			sender.sendMessage(" - Online: " + w.getClients());
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}