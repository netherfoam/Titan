package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Timings implements GenericCommand {

    @Override
    public void execute(CommandSender s, String[] args) {
        if (args.length > 0) {
            args[0] = args[0].toLowerCase();
            if ("reset".startsWith(args[0])) {
                Core.getTimings().reset();
                s.sendMessage("Timings reset.");
                return;
            } else {
                s.sendMessage("Invalid arg0 given.");
                s.sendMessage("Arg0: BLANK | reset");
                return;
            }
        }
        s.sendMessage("-- Timings Report --");
        s.sendMessage(Core.getTimings().getReport());
        s.sendMessage("Overall ServerThread Load: " + String.format("%.2f", Core.getServer().getThread().getUsage() * 100) + "%");
        Core.getServer().getThread().resetUsage();
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}