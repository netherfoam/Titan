package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
@CmdName(names = { "top", "up" })
public class Ascend implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		Location l = player.getLocation();
		int level;
		
		if(args.length > 0){
			try{
				level = +Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e){
				player.sendMessage("Usage: ::descend [amount]");
				return;
			}
		}
		else{
			level = 1;
		}
		player.teleport(new Location(l.getMap(), l.x, l.y, Calc.betweeni(0, 3, level + l.z)));
	}
	
	@Override
	public int getRankRequired() {
		return Rights.MOD;
	}
	
}