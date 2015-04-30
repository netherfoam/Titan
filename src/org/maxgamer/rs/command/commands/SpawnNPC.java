package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "spawnmob", "mob" })
public class SpawnNPC implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		if (args.length < 1) {
			p.sendMessage("Arg0: NPC Id");
			p.sendMessage("Arg1: Number of Spawns, optional");
			return;
		}
		try {
			int spawns = 1;
			if (args.length > 1) {
				spawns = Integer.parseInt(args[1]);
			}
			for (int i = 0; i < spawns; i++) {
				NPC n = new NPC(Integer.parseInt(args[0]), p.getLocation());
				n.setSpawn(p.getLocation());
				p.sendMessage("Spawned NPC " + args[0] + ": " + n.getDefinition().getName() + " temporarily.");
			}
		}
		catch (WorldFullException e) {
			p.sendMessage("World has no more room for NPC's");
			return;
		}
		catch (NumberFormatException e) {
			p.sendMessage("Invalid number supplied for NPC ID, given " + args[0]);
			return;
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}