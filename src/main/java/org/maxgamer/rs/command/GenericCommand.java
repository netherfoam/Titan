package org.maxgamer.rs.command;

/**
 * Represents a generic command which can be executed by a player or by the
 * server, or any object in game.
 *
 * @author netherfoam
 */
public interface GenericCommand extends Command {
    /**
     * Executes this command.
     *
     * @param player The user/object that executed the command
     * @param args   The list of strings used with the command. This does not
     *               include the command name! Eg: Doing ::hello world! means that the
     *               args value is 'world!', and not ['hello', 'world!']!
     */
    void execute(CommandSender sender, String[] args) throws Exception;

    int getRankRequired();
}