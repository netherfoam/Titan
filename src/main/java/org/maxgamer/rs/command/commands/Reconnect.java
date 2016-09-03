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
public class Reconnect implements GenericCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Arg0: Target name");
            return;
        }
        Persona p = Core.getServer().getPersonas().getPersona(args[0], true);
        if (p instanceof Player) {
            Player pl = (Player) p;
            pl.getSession().close(true);
        } else {
            sender.sendMessage(p + " is not a player.");
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}