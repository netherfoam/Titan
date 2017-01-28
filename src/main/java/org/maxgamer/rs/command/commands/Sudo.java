package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.Command;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Sudo implements GenericCommand {

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) {
            sender.sendMessage("Arg0: Target Player");
            sender.sendMessage("Arg1: Command");
            sender.sendMessage("ArgN: CommandArgs");
            return;
        }

        Persona target = Core.getServer().getPersonas().getPersona(args[0], true);
        if (target == null) {
            sender.sendMessage("Target " + args[0] + " not found");
            return;
        }

        if (target instanceof Player) {
            Player p = (Player) target;
            Command c = Core.getServer().getCommands().getCommand(args[1]);
            if (c == null) {
                sender.sendMessage("Command not found");
                return;
            }

            //Copy the remaining arguments
            String[] newArgs = new String[args.length - 2];
            System.arraycopy(args, 2, newArgs, 0, newArgs.length);

            if (c instanceof PlayerCommand) {
                ((PlayerCommand) c).execute(p, newArgs);
            } else if (c instanceof GenericCommand) {
                ((GenericCommand) c).execute(p, newArgs);
            } else {
                sender.sendMessage("Unknown command type.");
            }
        } else {
            sender.sendMessage("Target must be a player.");
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}