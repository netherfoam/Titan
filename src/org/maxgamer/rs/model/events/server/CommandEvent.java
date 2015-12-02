package org.maxgamer.rs.model.events.server;

import org.maxgamer.rs.command.Command;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.events.RSEvent;

/**
 * @author netherfoam
 */
public class CommandEvent extends RSEvent implements Cancellable {
	private CommandSender sender;
	private Command command;
	private String[] args;
	private boolean cancel;
	
	public CommandEvent(CommandSender s, Command c, String[] args) {
		this.sender = s;
		this.command = c;
		this.args = args;
	}
	
	public CommandSender getSender() {
		return sender;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public void setCommand(Command c) {
		this.command = c;
	}
	
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}