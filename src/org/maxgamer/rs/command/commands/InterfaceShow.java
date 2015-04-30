package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class InterfaceShow implements PlayerCommand {
	@Override
	public void execute(Player p, String[] args) {
		if (args.length < 3) {
			p.sendMessage("Arg0: ParentId");
			p.sendMessage("Arg1: ChildId");
			p.sendMessage("Arg2: ChildPos");
			return;
		}
		try {
			int parent = Integer.parseInt(args[0]);
			int childId = Integer.parseInt(args[1]);
			int childPos = Integer.parseInt(args[2]);
			
			p.sendMessage("Sending Interface... Parent: " + parent + ", ChildId: " + childId + ", ChildPos: " + childPos);
			p.getProtocol().sendInterface(true, parent, childPos, childId);
		}
		catch (NumberFormatException e) {
			p.sendMessage("All 3 args must be integers. One is not. Given " + args[0] + ", " + args[1] + ", " + args[2]);
			return;
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}