package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.network.Session;

/**
 * @author netherfoam
 */
public class Connections implements GenericCommand {
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage("Active Connections: (" + Core.getServer().getNetwork().getSessions().size() + ")");
		//for(Entry<Socket, Session> entry : Core.getServer().getNetwork().getSessions().entrySet()){
		for (Session session : Core.getServer().getNetwork().getSessions()) {
			sender.sendMessage("Connected: " + session.isConnected() + ", " + session.getHandler().getClass().getSimpleName());
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}