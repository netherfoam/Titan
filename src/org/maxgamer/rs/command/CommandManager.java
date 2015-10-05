package org.maxgamer.rs.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maxgamer.rs.command.commands.Animate;
import org.maxgamer.rs.command.commands.Announce;
import org.maxgamer.rs.command.commands.Ascend;
import org.maxgamer.rs.command.commands.Autocasts;
import org.maxgamer.rs.command.commands.BankCmd;
import org.maxgamer.rs.command.commands.Clear;
import org.maxgamer.rs.command.commands.Clients;
import org.maxgamer.rs.command.commands.Clip;
import org.maxgamer.rs.command.commands.CloseInterface;
import org.maxgamer.rs.command.commands.Connections;
import org.maxgamer.rs.command.commands.CopyCat;
import org.maxgamer.rs.command.commands.Debug;
import org.maxgamer.rs.command.commands.Descend;
import org.maxgamer.rs.command.commands.DialogueCmd;
import org.maxgamer.rs.command.commands.Die;
import org.maxgamer.rs.command.commands.GC;
import org.maxgamer.rs.command.commands.GFX;
import org.maxgamer.rs.command.commands.Gear;
import org.maxgamer.rs.command.commands.Hide;
import org.maxgamer.rs.command.commands.HideObjects;
import org.maxgamer.rs.command.commands.Instance;
import org.maxgamer.rs.command.commands.InterfaceList;
import org.maxgamer.rs.command.commands.InterfaceShow;
import org.maxgamer.rs.command.commands.Item;
import org.maxgamer.rs.command.commands.ItemScriptDump;
import org.maxgamer.rs.command.commands.Kick;
import org.maxgamer.rs.command.commands.Kill;
import org.maxgamer.rs.command.commands.LogonStatus;
import org.maxgamer.rs.command.commands.ModuleCmd;
import org.maxgamer.rs.command.commands.Nearby;
import org.maxgamer.rs.command.commands.Picker;
import org.maxgamer.rs.command.commands.Position;
import org.maxgamer.rs.command.commands.Queues;
import org.maxgamer.rs.command.commands.RangeGear;
import org.maxgamer.rs.command.commands.Rank;
import org.maxgamer.rs.command.commands.Reconnect;
import org.maxgamer.rs.command.commands.Reload;
import org.maxgamer.rs.command.commands.Restore;
import org.maxgamer.rs.command.commands.Save;
import org.maxgamer.rs.command.commands.Script;
import org.maxgamer.rs.command.commands.Servers;
import org.maxgamer.rs.command.commands.ShowFlags;
import org.maxgamer.rs.command.commands.SkillLevel;
import org.maxgamer.rs.command.commands.Sort;
import org.maxgamer.rs.command.commands.Sound;
import org.maxgamer.rs.command.commands.Spawn;
import org.maxgamer.rs.command.commands.SpawnNPC;
import org.maxgamer.rs.command.commands.SpawnObject;
import org.maxgamer.rs.command.commands.Status;
import org.maxgamer.rs.command.commands.Stop;
import org.maxgamer.rs.command.commands.Sudo;
import org.maxgamer.rs.command.commands.SwapPrayer;
import org.maxgamer.rs.command.commands.TPTo;
import org.maxgamer.rs.command.commands.Teleport;
import org.maxgamer.rs.command.commands.Timings;
import org.maxgamer.rs.command.commands.Title;
import org.maxgamer.rs.command.commands.Tphere;
import org.maxgamer.rs.command.commands.Vendor;
import org.maxgamer.rs.command.commands.Whisper;
import org.maxgamer.rs.command.commands.WorldMapBank;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.event.EventManager;
import org.maxgamer.rs.events.server.CommandEvent;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.structure.TrieSet;
import org.maxgamer.rs.structure.configs.ConfigSection;

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
	 * Loads the command manager and all of its commands.
	 */
	public void load() {
		this.register("animate", new Animate());
		this.register("announce", new Announce());
		this.register("ascend", new Ascend());
		this.register("autocasts", new Autocasts());
		this.register("bankcmd", new BankCmd());
		this.register("character", new org.maxgamer.rs.command.commands.Character());
		this.register("clear", new Clear());
		this.register("clients", new Clients());
		this.register("clip", new Clip());
		this.register("closeinterface", new CloseInterface());
		this.register("connections", new Connections());
		this.register("copycat", new CopyCat());
		this.register("debug", new Debug());
		this.register("descend", new Descend());
		this.register("dialoguecmd", new DialogueCmd());
		this.register("die", new Die());
		this.register("gc", new GC());
		this.register("gear", new Gear());
		this.register("gfx", new GFX());
		this.register("hide", new Hide());
		this.register("hideobjects", new HideObjects());
		this.register("instance", new Instance());
		this.register("interfacelist", new InterfaceList());
		this.register("interfaceshow", new InterfaceShow());
		this.register("item", new Item());
		this.register("itemscriptdump", new ItemScriptDump());
		this.register("kick", new Kick());
		this.register("kill", new Kill());
		this.register("list", new org.maxgamer.rs.command.commands.List());
		this.register("logonstatus", new LogonStatus());
		this.register("modulecmd", new ModuleCmd());
		this.register("nearby", new Nearby());
		this.register("picker", new Picker());
		this.register("position", new Position());
		this.register("queues", new Queues());
		this.register("rangegear", new RangeGear());
		this.register("rank", new Rank());
		this.register("reconnect", new Reconnect());
		this.register("reload", new Reload());
		this.register("restore", new Restore());
		this.register("save", new Save());
		this.register("script", new Script());
		this.register("servers", new Servers());
		this.register("showflags", new ShowFlags());
		this.register("skilllevel", new SkillLevel());
		this.register("sort", new Sort());
		this.register("sound", new Sound());
		this.register("spawn", new Spawn());
		this.register("spawnNPC", new SpawnNPC());
		this.register("spawnobject", new SpawnObject());
		this.register("spellbookcmd", new org.maxgamer.rs.command.commands.SpellbookCmd());
		this.register("status", new Status());
		this.register("stop", new Stop());
		this.register("Sudo", new Sudo());
		this.register("swapprayer", new SwapPrayer());
		this.register("teleport", new Teleport());
		this.register("timings", new Timings());
		this.register("title", new Title());
		this.register("tphere", new Tphere());
		this.register("TPTo", new TPTo());
		this.register("vendor", new Vendor());
		this.register("whisper", new Whisper());
		this.register("worldmapbank", new WorldMapBank());
		
		ConfigSection config = Core.getWorldConfig().getSection("commands", null);
		if (config != null) {
			for (String alias : config.getKeys()) {
				Command command = getCommand(config.getString(alias));
				if (command == null) {
					continue;
				}
				
				register(alias, command);
			}
		}
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
		commands.remove(command);
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