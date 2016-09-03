package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Sound implements PlayerCommand {

    @Override
    public void execute(Player p, String[] args) {
        p.getProtocol().sendSound(Integer.parseInt(args[0]), 255, 255);
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}