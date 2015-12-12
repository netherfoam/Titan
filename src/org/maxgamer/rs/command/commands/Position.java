package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "location" })
public class Position implements PlayerCommand {

	@Override
	public void execute(Player p, String[] args) {
		int rx = p.getLocation().getRegionX();
		int ry = p.getLocation().getRegionY();
		int cx = p.getLocation().getChunkX();
		int cy = p.getLocation().getChunkY();
		p.sendMessage("Location: " + p.getLocation() + ", Region: (" + rx + ", " + ry + ")" + ", Chunk: (" + cx + ", " + cy + ")");
	}

	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}