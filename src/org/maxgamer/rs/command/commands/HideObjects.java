package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.GameObject;

/**
 * @author netherfoam
 */
public class HideObjects implements PlayerCommand {
	@Override
	public void execute(Player player, String[] args) throws Exception {
		if (args.length < 1) {
			player.sendMessage("Args0: Radius in Tiles");
			return;
		}
		int radius;
		try {
			radius = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException e) {
			player.sendMessage(args[0] + " is not a valid radius.");
			return;
		}
		
		int n = 0;
		for (GameObject g : player.getLocation().getNearby(GameObject.class, radius)) {
			g.hide();
			n++;
		}
		player.sendMessage("Hid " + n + " objects");
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}