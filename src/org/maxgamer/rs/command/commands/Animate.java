package org.maxgamer.rs.command.commands;

import java.io.IOException;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = { "emote", "animate" })
public class Animate implements PlayerCommand {
	
	@Override
	public void execute(final Player p, String[] args) throws IOException {
		if (args.length < 1) {
			p.sendMessage("Arg0: AnimationID)");
			return;
		}
		int anim = Integer.parseInt(args[0]);
		p.getUpdateMask().setAnimation(new Animation(anim), 100);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}