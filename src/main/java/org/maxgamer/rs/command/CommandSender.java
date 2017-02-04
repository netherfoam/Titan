package org.maxgamer.rs.command;

/**
 * Represents something that may execute a command. Not just players, but the
 * console, possibly objects too.
 *
 * @author netherfoam
 */
public interface CommandSender {
    void sendMessage(String msg);

    String getName();
}