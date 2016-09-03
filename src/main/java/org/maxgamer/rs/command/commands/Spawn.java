package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names = {"stuck"})
public class Spawn implements PlayerCommand {

    @Override
    public void execute(Player player, String[] args) throws Exception {
        player.teleport(Persona.DEFAULT_PLAYER_SPAWN);
        player.sendMessage("Teleported to Spawn");
    }

    @Override
    public int getRankRequired() {
        return Rights.USER;
    }
}