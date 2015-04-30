package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.impl.dialogue.DialogueFork;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class DialogueCmd implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		DialogueFork d = new DialogueFork(player, args);
		player.getWindow().open(d);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}