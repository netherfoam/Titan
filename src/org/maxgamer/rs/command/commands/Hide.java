package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.HeapDump;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Hide implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		if (p.isHidden()) {
			p.show();
		}
		else {
			p.hide();
		}
		HeapDump.dumpHeap("heap.bin", true);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}