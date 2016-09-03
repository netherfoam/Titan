package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

public class Config implements PlayerCommand {

    @Override
    public void execute(Player player, String[] args) throws Exception {
        int id = Integer.parseInt(args[0]);
        int value = Integer.parseInt(args[1]);
        player.getProtocol().sendConfig(id, value);
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}
