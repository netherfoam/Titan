package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Stop implements GenericCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Shutting down server...");
        System.exit(0);
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}