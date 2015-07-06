package org.maxgamer.rs.command.commands;

import java.io.File;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.npc.NPCGroup;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Reload implements GenericCommand {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		//Core.getServer().getScriptEngine().reload();
		sender.sendMessage("Scripts reloaded.");
		Core.getWorldConfig().reload();
		sender.sendMessage("World config reloaded.");
		Core.getServer().getCommands().load(new File("bin" + File.separator + "org" + File.separator + "maxgamer" + File.separator + "rs" + File.separator + "command" + File.separator + "commands"));
		sender.sendMessage("Commands reloaded");
		NPCGroup.reload();
		sender.sendMessage("NPCGroups reloaded");
		Core.getServer().getModules().unload();
		Core.getServer().getModules().load();
		sender.sendMessage("Modules reloaded.");
		Core.getScripts().clear();
		Core.getScripts().load(new File("scripts"));
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}