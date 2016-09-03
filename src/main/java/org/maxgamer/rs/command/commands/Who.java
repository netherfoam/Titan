package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.Session;

/**
 * @author netherfoam
 */
@CmdName(names = {"list", "online", "who"})
public class Who implements GenericCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("--Sessions--");
        for (Session s : Core.getServer().getNetwork().getSessions()) {
            sender.sendMessage(s.toString());
        }
        sender.sendMessage("--Lobby Players--");
        for (LobbyPlayer p : Core.getServer().getLobby().getPlayers()) {
            sender.sendMessage(p.toString());
        }
        sender.sendMessage("--Players--");
        for (Persona p : Core.getServer().getPersonas()) {
            sender.sendMessage(p.getSpawnIndex() + "# " + p.getName() + "@" + p.getLocation() + " HP: " + p.getHealth() + "/" + p.getMaxHealth());
        }
        sender.sendMessage(Core.getServer().getPersonas().getCount() + " Online");
    }

    @Override
    public int getRankRequired() {
        return Rights.USER;
    }

}