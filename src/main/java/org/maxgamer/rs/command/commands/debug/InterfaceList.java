package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.interfaces.Interface;

/**
 * @author netherfoam
 */
public class InterfaceList implements PlayerCommand {
    @Override
    public void execute(Player p, String[] args) {
        p.sendMessage("-- Active Interfaces --");
        for (Interface i : p.getWindow().getInterfaces()) {
            p.sendMessage(i.getClass().getSimpleName() + " (" + i.getChildId() + ")");
        }
    }

    @Override
    public int getRankRequired() {
        return Rights.MOD;
    }
}