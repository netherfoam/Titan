package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Title implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		player.getModel().setTitle(Integer.parseInt(args[0]));
		player.sendMessage("Title set to ID " + args[0]);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
}