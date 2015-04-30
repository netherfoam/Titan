package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class ShowFlags implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		for (int i = -10; i <= 10; i++) {
			for (int j = -10; j <= 10; j++) {
				Location l = p.getLocation().add(i, j);
				int flags = l.getMap().getFlags(l.x, l.y, l.z);
				if ((flags) != 0) {
					Core.getServer().highlight(8, l);
				}
			}
		}
		
		p.sendMessage("Flags at your location: " + p.getLocation().getFlags());
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}