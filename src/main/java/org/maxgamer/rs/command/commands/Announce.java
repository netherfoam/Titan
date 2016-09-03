package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.structure.ChatColor;

/**
 * @author netherfoam
 */
@CmdName(names = {"broadcast"})
public class Announce implements GenericCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage:");
            sender.sendMessage("Arg0..n: Message");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            sb.append(" " + args[i]);
        }

        String s = sb.toString();
        for (Client c : Core.getServer().getClients()) {
            c.sendMessage(ChatColor.get(0, 245, 115) + "Announcement: " + s + "</col>");
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.MOD;
    }
}