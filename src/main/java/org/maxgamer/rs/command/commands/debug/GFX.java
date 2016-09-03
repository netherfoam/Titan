package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.Graphics;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = {"graphics"})
public class GFX implements PlayerCommand {

    @Override
    public void execute(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage("Arg0: GFX Id)");
            return;
        }
        p.getUpdateMask().setGraphics(new Graphics(Integer.parseInt(args[0])));
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}