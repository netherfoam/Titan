package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

public class Version implements GenericCommand {

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        sender.sendMessage("For the latest version, check out http://titan.maxgamer.org");
        sender.sendMessage("Author: " + Core.AUTHOR);
        sender.sendMessage("Build: " + Core.BUILD);
    }

    @Override
    public int getRankRequired() {
        return Rights.USER;
    }
}