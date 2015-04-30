package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.DynamicGameObject;

/**
 * @author netherfoam
 */
@CmdName(names = { "gobject" })
public class SpawnObject implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		int type = 10;
		if (args.length < 1) {
			player.sendMessage("Args0: Object ID");
			player.sendMessage("Args1: Object Type (Optional)");
			return;
		}
		
		if (args.length >= 2) {
			try {
				type = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e) {
				player.sendMessage("Bad type given: " + args[1]);
			}
		}
		
		try {
			DynamicGameObject g = new DynamicGameObject(Integer.parseInt(args[0]), type);
			g.setLocation(player.getLocation());
			player.sendMessage("Spawned object at your location.");
		}
		catch (NumberFormatException e) {
			player.sendMessage("Invalid object ID number given " + args[0]);
			return;
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}