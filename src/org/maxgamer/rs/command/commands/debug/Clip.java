package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.cache.MapCache;
import org.maxgamer.rs.cache.format.Landscape;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.model.map.Location;

import java.util.Arrays;

/**
 * @author netherfoam
 */
public class Clip implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		Location l = player.getLocation();
		int clip = l.getMap().getClip(l.x, l.y, l.z);
		player.sendMessage("Clip@" + l + ": " + String.format("0x%X", clip));
		player.sendMessage(Arrays.toString(ClipMasks.getClipNames(clip)));
		
		int rx = l.getRegionX();
		int ry = l.getRegionY();
		Landscape land = Landscape.parse(MapCache.getMap(rx, ry), MapCache.getObjects(rx, ry));
		for (int i = 0; i < 4; i++) {
			player.sendMessage("Land Flag Level #" + i + " => " + land.getFlags(l.x & 0x3F, l.y & 0x3F, i));
		}
	}
	
	@Override
	public int getRankRequired() {
		return 2;
	}
}