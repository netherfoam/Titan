package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

public class WhoIs implements GenericCommand{

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length < 1){
			sender.sendMessage("Usage: ::whois [target]");
			return;
		}
		
		Persona p = Core.getServer().getPersona(args[0]);
		
		if(p == null){
			sender.sendMessage("Couldn't find " + args[0]);
			return;
		}
		
		sender.sendMessage(p.getName() + " is level " + p.getModel().getCombatLevel());
		sender.sendMessage("Their total skill level is " + p.getSkills().getTotal());
		sender.sendMessage("They're currently at " + p.getLocation());
		sender.sendMessage("There are " + (p.getLocation().getNearby(Player.class, 12)).size() + " players near them.");
	}

	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}