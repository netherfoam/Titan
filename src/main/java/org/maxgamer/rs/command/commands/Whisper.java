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
@CmdName(names = {"tell", "pm", "message"})
public class Whisper implements GenericCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) {
            sender.sendMessage("Usage:");
            sender.sendMessage("Arg0: Name of player");
            sender.sendMessage("Arg1..n: Message");
            return;
        }
        Client target = Core.getServer().getClient(args[0], true);
        if (target == null) {
            sender.sendMessage("Player " + args[0] + " not found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(args[1]);
        for (int i = 2; i < args.length; i++) {
            sb.append(" " + args[i]);
        }
        String s = sb.toString();

        target.sendMessage(ChatColor.get(255, 0, 255) + "Whisper from " + sender.getName() + ": " + s + "</col>");
        sender.sendMessage(ChatColor.get(200, 0, 200) + "Whisper to " + target.getName() + ": " + s + "</col>");
    }

    @Override
    public int getRankRequired() {
        return Rights.USER;
    }
}