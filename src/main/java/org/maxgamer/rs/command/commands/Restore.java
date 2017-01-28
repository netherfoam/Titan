package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = {"reset"})
public class Restore implements PlayerCommand {

    @Override
    public void execute(Player player, String[] args) throws Exception {
        if (args.length > 0) {
            Persona target = Core.getServer().getPersonas().getPersona(args[0], true);
            if (target == null) {
                player.sendMessage("Player not found: " + args[0]);
            } else {
                player.restore();
            }
        } else {
            player.restore();
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}