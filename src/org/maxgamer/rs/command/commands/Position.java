package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "location" })
public class Position implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		p.sendMessage("Location: " + p.getLocation());
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}