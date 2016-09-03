package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Rank implements GenericCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 1) {
            sender.sendMessage("Arg0: Name of player");
            sender.sendMessage("Arg1: New Rank (Optional)");
            return;
        }

        Persona p = Core.getServer().getPersonas().getPersona(args[0], true);
        if (p == null) {
            sender.sendMessage("Player not online: " + args[0]);
            return;
        }

        if (p instanceof Player == false) {
            sender.sendMessage("Only players have rights. Bots have rights of 0.");
            return;
        }

        Player pl = (Player) p;
        if (args.length < 2) {
            sender.sendMessage("Rights of " + pl.getName() + ": " + pl.getRights());
            return;
        }

        try {
            pl.setRights(Integer.parseInt(args[1]));
            sender.sendMessage(pl.getName() + "'s new rights: " + pl.getRights());
            return;
        } catch (NumberFormatException e) {
            sender.sendMessage("Arg1 must be an integer (Rights level, 0-2)");
            return;
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}