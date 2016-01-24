package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.area.Area;

/**
 * @author netherfoam
 */
@CmdName(names = { "location", "position", "gps", "whereami" })
public class GPS implements PlayerCommand {

	@Override
	public void execute(Player p, String[] args) {
		int rx = p.getLocation().getRegionX();
		int ry = p.getLocation().getRegionY();
		int cx = p.getLocation().getChunkX();
		int cy = p.getLocation().getChunkY();
		p.sendMessage("Location: " + p.getLocation() + ", Region: (" + rx + ", " + ry + ")" + ", Chunk: (" + cx + ", " + cy + ")");
		
		StringBuilder sb = new StringBuilder();
		for(Area a : p.getMap().getAreas().getAreas(p.getLocation())){
			sb.append(a.getClass().getSimpleName() + " ");
		}
		if(sb.length() <= 0){
			sb.append("None");
		}
		p.sendMessage("Areas: [" + sb + "]");
	}

	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}