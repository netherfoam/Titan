package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "switchprayer" })
public class SwapPrayer implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		player.getPrayer().swapPrayerBook();
		player.sendMessage("You are now using: " + (player.getPrayer().isAncient() ? "curses." : "normal prayers."));
	}
	
	@Override
	public int getRankRequired() {
		return Rights.USER;
	}
}
