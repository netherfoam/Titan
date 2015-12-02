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
		/*
		 * Chunk[][] chunks = new Chunk[][]{ //{new Chunk(434, 399), new Chunk(434, 400), new Chunk(434, 401)}, {new Chunk(435, 399, 0), new
		 * Chunk(435, 400, 0), new Chunk(435, 401, 0)}, {new Chunk(436, 399, 0), new Chunk(436, 400, 0), new Chunk(436, 401, 0)}, //TOPLEFT is
		 * SOUTHWEST //TOPRIGHT is NORTHWEST };
		 */
		/*Chunk[][][] chunks = new Chunk[3][3][2]; // [X][Y][Z]
		for (int i = 434; i <= 436; i++) {
			for (int j = 399; j <= 401; j++) {
				Chunk c = new Chunk(i, j, 0);
				chunks[i - 434][j - 399][0] = c;
			}
		}
		
		//Second level test
		chunks[1][1][1] = new Chunk(239, 634, 1);*/
		/*
		MapBuilder builder = new MapBuilder();
		for(int i = 434; i <= 436; i++){
			for(int j = 399; j <= 401; j++){
				Chunk c = new Chunk(i, j, 0);
				builder.set(i - 434, j - 399, 0, c);
			}
		}
		builder.set(1, 1, 1, new Chunk(239, 634, 1));*/
		
		MapBuilder builder = new MapBuilder();
		for(int i = 320; i <= 330; i++){
			for(int j = 320; j <= 330; j++){
				Chunk c = new Chunk(i, j, 0);
				builder.set(i - 320, j - 320, 0, c);
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