package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class Autocasts implements PlayerCommand {

    @Override
    public void execute(final Player player, final String[] args) throws Exception {
        new Tickable() {
            int next = 0;

            @Override
            public void tick() {
                player.sendMessage("Autocast: " + next);
                player.getProtocol().sendConfig(108, next++);
                this.queue(1);
            }
        }.queue(1);
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}