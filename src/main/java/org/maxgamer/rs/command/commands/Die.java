package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "suicide" })
public class Die implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		player.setHealth(0);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
	
}