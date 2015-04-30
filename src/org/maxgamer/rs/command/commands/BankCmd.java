package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.impl.primary.BankInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "bank" })
public class BankCmd implements PlayerCommand {
	
	@Override
	public void execute(Player p, String[] args) {
		BankInterface interf = new BankInterface(p);
		p.getWindow().open(interf);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}