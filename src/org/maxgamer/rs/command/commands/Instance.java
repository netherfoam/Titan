package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.cache.EncryptedException;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Chunk;
import org.maxgamer.rs.model.map.DynamicMap;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.MapBuilder;

/**
 * @author netherfoam
 */
public class Instance implements PlayerCommand {

	@Override
	public void execute(Player p, String[] args) {
		MapBuilder builder = new MapBuilder();
		for(int i = 328; i <= 335; i++){
			for(int j = 319; j <= 328; j++){
				builder.set(i - 328, j - 319, 0, new Chunk(i, j, 0));
			}
		}
		
		DynamicMap map;
		try {
			map = builder.create(p.getName() + "-instance");
		}
		catch (EncryptedException e) {
			e.printStackTrace();
			return;
		}
		
		Location l = new Location(map, 12, 12, 0);
		p.teleport(l);
		p.sendMessage("Teleported to generated area");
	}

	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}