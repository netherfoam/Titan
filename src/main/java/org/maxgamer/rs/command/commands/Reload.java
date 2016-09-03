package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.structure.configs.FileConfig;

/**
 * @author netherfoam
 */
public class Reload implements GenericCommand {
    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        sender.sendMessage("Scripts reloaded.");
        ((FileConfig) Core.getServer().getConfig()).reload();
        sender.sendMessage("World config reloaded.");
        Core.getServer().getModules().unload();
        Core.getServer().getModules().load();
        sender.sendMessage("Modules reloaded.");
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}