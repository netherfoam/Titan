package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.SubMap;
import org.maxgamer.rs.model.map.WorldMap;

/**
 * @author netherfoam
 */
public class Instance implements PlayerCommand {

	@Override
	public void execute(Player p, String[] args) {
		WorldMap map = new SubMap(p.getName() + "-instance", new Position(2624, 2560), 64, 64);
		Location l = new Location(map, 2640, 2576, 0);
		p.teleport(l);
		p.sendMessage("Teleported to generated area");
	}

	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}