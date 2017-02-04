package org.maxgamer.rs.command;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * Represents a command which may be used only by a player.
 *
 * @author netherfoam
 */
public interface PlayerCommand extends Command {
    /**
     * Executes this command.
     *
     * @param player The player who executed the command
     * @param args   The list of strings used with the command. This does not
     *               include the command name! Eg: Doing ::hello world! means that the
     *               args value is 'world!', and not ['hello', 'world!']!
     */
    void execute(Player player, String[] args) throws Exception;

    int getRankRequired();
}