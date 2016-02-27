package org.maxgamer.rs.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maxgamer.rs.event.EventManager;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.server.CommandEvent;
import org.maxgamer.rs.structure.TrieSet;

/**
 * @author netherfoam
 */
public class CommandManager {
	public static final String COMMAND_PREFIX = "::";
	
	private HashMap<String, Command> commands = new HashMap<String, Command>();
	private TrieSet names = new TrieSet();
	private EventManager events;
	
	public CommandManager(EventManager events) {
		this.events = events;
	}
	
	public void clear() {
		this.commands.clear();
		this.names.clear();
	}
	
	/**
	 * Registers the given command with the given name
	 * 
	 * @param command The name for the command
	 * @param action The command
	 */
	public void register(String command, Command action) {
		if (command != null) {
			command = command.toLowerCase();
		}
		names.add(command);
		commands.put(command, action);
		
		// Register any aliases used for the command
		CmdName annotation = action.getClass().getAnnotation(CmdName.class);
		if(annotation != null){
			for(String alias : annotation.names()){
				alias = alias.toLowerCase();
				
				if(commands.get(alias) == null){
					names.add(alias);
					commands.put(alias, action);
				}
			}
		}
	}
	
	/**
	 * Unregisters the given command
	 * 
	 * @param command The name of the command
	 */
	public void unregister(String command) {
		if (command != null) {
			command = command.toLowerCase();
		}
		names.remove(command);
		Command c = commands.remove(command);
		if(c == null) return;
		
		// Register any aliases used for the command
		CmdName annotation = c.getClass().getAnnotation(CmdName.class);
		if(annotation != null){
			for(String alias : annotation.names()){
				alias = alias.toLowerCase();
				
				if(commands.get(alias) == c){
					names.remove(alias);
					commands.remove(alias);
				}
			}
		}
	}
	
	/**
	 * Returns true if the command is registered
	 * 
	 * @param command The name for the command
	 * @return true if it is registered, false otherwise.
	 */
	public boolean isRegistered(String command) {
		if (command != null) {
			command = command.toLowerCase();
		}
		return commands.containsKey(command);
	}
	
	/**
	 * Fetches the given command, and autocompletes it if possible. E.g, i will
	 * be autocompleted to item, etc.
	 * 
	 * @param command The partial or full name
	 * @return The command on success, null on failure (Not registered)
	 */
	public Command getCommand(String command) {
		if (command != null) {
			command = command.toLowerCase();
			command = names.nearestKey(command);
		}
		return commands.get(command);
	}
	
	/**
	 * Handles a command by the given player
	 * @param sender The player
	 * @param arg The command they used, including prefix.
	 * @throws IllegalArgumentException if Player is null, String is null, or
	 *         String doesn't start with the command prefix.
	 */
	public void handle(CommandSender sender, String arg) {
		if (sender == null) {
			throw new IllegalArgumentException("Null players may not execute commands!");
		}
		if (arg == null) {
			throw new IllegalArgumentException("Players may not execute null commands!");
		}
		if (!arg.startsWith(CommandManager.COMMAND_PREFIX)) {
			throw new IllegalArgumentException("Commands must start with CommandManager.COMMAND_PREFIX");
		}
		arg = arg.substring(CommandManager.COMMAND_PREFIX.length()); //Remove the command prefix
		
		Pattern pattern = Pattern.compile("(\"[^\"]*\")|\\S+", 0);
		Matcher m = pattern.matcher(arg);
		LinkedList<String> args = new LinkedList<String>();
		while (m.find()) {
			String s = arg.substring(m.start(), m.end());
			args.add(s);
		}
		Log.info(sender.getName() + ": " + arg);
		handle(sender, args.toArray(new String[args.size()]));
	}
	
	/**
	 * Handles when a player sends a command
	 * 
	 * @param sender The player
	 * @param args The args. This must include the command used at args[0]. The
	 *        rest of the args array will be parsed to the command itself.
	 */
	public void handle(CommandSender sender, String[] args) {
		if (sender == null) {
			throw new IllegalArgumentException("Null players may not execute commands!");
		}
		if (args == null) {
			throw new IllegalArgumentException("Players may not execute null commands!");
		}
		
		try {
			if (args.length == 0 || args[0].isEmpty()) {
				sender.sendMessage("Commands Available: ");
				List<String> cmds = new ArrayList<String>(commands.keySet());
				Collections.sort(cmds, String.CASE_INSENSITIVE_ORDER);
				for (String cmd : cmds) {
					Command c = getCommand(cmd);
					
					if (c instanceof PlayerCommand) {
						if (sender instanceof Player) {
							Player p = (Player) sender;
							PlayerCommand pc = (PlayerCommand) c;
							if (p.getRights() < pc.getRankRequired()) {
								continue; //No rights.
							}
						}
						else {
							continue; //Player-only.
						}
					}
					else if (c instanceof GenericCommand) {
						if (sender instanceof Player) {
							Player p = (Player) sender;
							GenericCommand gc = (GenericCommand) c;
							if (p.getRights() < gc.getRankRequired()) {
								continue; //No rights.
							}
						}
						else {
							//Sender is console, full rights.
						}
					}
					
					sender.sendMessage(cmd);
				}
				return;
			}
			
			String cmd = args[0];
			Command command = getCommand(cmd);
			if (command == null) {
				sender.sendMessage("No such command found: " + cmd);
				return;
			}
			
			String[] subArgs = new String[args.length - 1];
			
			for (int i = 1; i < args.length; i++) {
				subArgs[i - 1] = args[i];
			}
			
			if (events != null) {
				CommandEvent e = new CommandEvent(sender, command, subArgs);
				events.callEvent(e);
				if (e.isCancelled()) {
					return;
				}
				
				//These may be modified during the event call.
				command = e.getCommand();
				subArgs = e.getArgs();
			}
			
			if (command instanceof PlayerCommand) {
				if (sender instanceof Player) {
					PlayerCommand pc = ((PlayerCommand) command);
					Player p = (Player) sender;
					if (p.getRights() < pc.getRankRequired()) {
						p.sendMessage("You don't have permission to execute that.");
						return;
					}
					pc.execute(p, subArgs);
				}
				else {
					sender.sendMessage("That command is a player-only command.");
					return;
				}
			}
			else if (command instanceof GenericCommand) {
				GenericCommand gc = (GenericCommand) command;
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (p.getRights() < gc.getRankRequired()) {
						p.sendMessage("You don't have permission to execute that.");
						return;
					}
				}
				else {
					//Sender is console, full rights.
				}
				gc.execute(sender, subArgs);
			}
			else {
				Log.warning("Unrecognsied command type: " + command.getClass().getName());
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.warning("Failed to handle command.");
			sender.sendMessage("Failed to handle command. Message: " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
}