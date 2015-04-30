package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
@CmdName(names = { "bottom", "down" })
public class Descend implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		Location l = player.getLocation();
		if (l.z <= 0) {
			player.sendMessage("May not descend any further.");
			return;
		}
		player.teleport(l.add(0, 0, -1));
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
	
}