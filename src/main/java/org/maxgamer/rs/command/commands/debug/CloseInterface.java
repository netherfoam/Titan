package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;

/**
 * @author netherfoam
 */
public class CloseInterface implements PlayerCommand {

    @Override
    public void execute(Player player, String[] args) throws Exception {
        String name = args[0].toLowerCase();

        for (Interface iface : player.getWindow().getInterfaces()) {
            if (iface.getClass().getSimpleName().toLowerCase().startsWith(name)) {
                player.getWindow().close(iface);
                player.sendMessage("Closed interface: " + iface.getClass().getSimpleName());
            }
        }
    }

    @Override
    public int getRankRequired() {
        return 2;
    }
}