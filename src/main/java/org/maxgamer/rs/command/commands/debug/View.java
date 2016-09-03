package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.entity.mob.persona.player.ViewDistance;

import java.util.Arrays;

/**
 * @author netherfoam
 */
public class View implements PlayerCommand {

    @Override
    public void execute(Player player, String[] args) throws Exception {
        if (args.length < 1) {
            player.sendMessage("Arg0: ViewDistance " + Arrays.toString(ViewDistance.values()));
            return;
        }

        ViewDistance v;
        try {
            v = ViewDistance.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("ViewDistance not valid: " + args[0] + ", Valid options are: " + Arrays.toString(ViewDistance.values()));
            return;
        }

        player.setViewDistance(v);
        player.sendMessage("ViewDistance set to " + v);
    }

    @Override
    public int getRankRequired() {
        return Rights.MOD;
    }
}