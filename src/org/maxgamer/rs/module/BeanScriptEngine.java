package org.maxgamer.rs.module;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.structure.timings.StopWatch;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * A script engine class for handling scripts in the scripts/ folder. This
 * allows uncompiled BeanShell scripts to be executed and reloaded
 * @author netherfoam
 */
public class BeanScriptEngine {
	
	/**
	 * The scripts we've loaded. The key is the name of the folder, with a
	 * forward slash (/) as the key, and the interpretor (thus the namespace)
	 * for each script.
	 */
	private HashMap<String, Interpreter> scripts = new HashMap<String, Interpreter>();
	
	/**
	 * Constructs a new BeanScriptEngine. This does not load the scripts.
	 */
	public BeanScriptEngine() {
		
	}
	
	/**
	 * Executes the given function from the given script with the given args,
	 * and returns the value that is returned from the function.
	 * @param name the name of the script, with forward slashes (/) to separate
	 *        the directories, and without the file extension (.java or .bsh)
	 * @param func the name of the function to execute
	 * @param args the arguments to execute the function with, may be null to
	 *        represent no args.
	 * @return the return value from the script or null if there was an error
	 *         executing it
	 */
	public Object run(String name, String func, Object... args) {
		Interpreter script = scripts.get(name + ".java"); //TODO: Remove .java
		if (script == null) {
			//TODO: This is debug for the server, and we may want to remove this later.
			Log.debug("Requested to run script that doesn't exist " + name);
			Log.debug("Available Scripts: " + scripts.keySet());
			return null; //No script for handling that.
		}
		if (args == null) {
			args = new Object[0];
		}
		
		StopWatch w = Core.getTimings().start("BeanShell Scripts");
		try {
			NameSpace ns = script.getNameSpace();
			for (Object o : args) {
				ns.importObject(o);
			}
			return ns.invokeMethod(func, args, script);
		}
		catch (EvalError e) {
			Log.warning("Error parsing script " + name);
			e.printStackTrace();
		}
		finally {
			w.stop();
		}
		return null;
	}
	
	/**
	 * Reloads all scripts from the scripts/ folder.
	 */
	public void reload() {
		//Unregister any previously existing command scripts
		if (scripts != null) {
			for (Entry<String, Interpreter> entry : getScripts("commands").entrySet()) {
				String key = entry.getKey();
				String name = key.substring(key.lastIndexOf("/"), key.length());
				Core.getServer().getCommands().unregister(name);
			}
		}
		scripts = new HashMap<String, Interpreter>();
		
		File folder = new File("scripts");
		for (File f : folder.listFiles()) {
			if (f.isDirectory() || f.getName().endsWith(".bsh") || f.getName().endsWith(".java")) {
				load(f, "");
			}
		}
		
		//Register the command scripts
		for (Entry<String, Interpreter> entry : getScripts("commands").entrySet()) {
			String key = entry.getKey();
			String name = key.substring(key.lastIndexOf("/") + 1, key.lastIndexOf("."));
			final Interpreter script = entry.getValue();
			Core.getServer().getCommands().register(name, new GenericCommand() {
				
				@Override
				public int getRankRequired() {
					NameSpace ns = script.getNameSpace();
					try {
						return (Integer) ns.invokeMethod("getRankRequired", new Object[0], script);
					}
					catch (Exception e) {
						return Rights.ADMIN;
					}
				}
				
				@Override
				public void execute(CommandSender sender, String[] args) throws Exception {
					NameSpace ns = script.getNameSpace();
					try {
						ns.invokeMethod("execute", new Object[] { sender, args }, script);
					}
					catch (Exception e) {
						sender.sendMessage("There was an error processing the command.");
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	/**
	 * Fetches all scripts which are from the given folder. If the given folder
	 * does not end with a forward slash (/), then one is appended. This then
	 * searches for all scripts registered from that folder. The keys in the map
	 * are the full keys, such that run() will accept the String key alone.
	 * @param folder the name of the folder, case sensitive
	 * @return a map of script_source to script
	 */
	public HashMap<String, Interpreter> getScripts(String folder) {
		HashMap<String, Interpreter> list = new HashMap<String, Interpreter>();
		if (folder.endsWith("/") == false) folder = folder + "/";
		
		for (Entry<String, Interpreter> entry : this.scripts.entrySet()) {
			if (entry.getKey().startsWith(folder)) {
				list.put(entry.getKey(), entry.getValue());
			}
		}
		return list;
	}
	
	/**
	 * Loads the given folder and all subfolders as scripts for this engine. The
	 * files are parsed immediately.
	 * @param folder the folder to read from
	 * @param prefix the prefix, use an empty string.
	 */
	public void load(File file, String prefix) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.isDirectory() || f.getName().endsWith(".bsh") || f.getName().endsWith(".java")) {
					load(f, prefix + file.getName() + "/");
				}
			}
			return;
		}
		
		try {
			//Load file from folder
			String key = prefix + file.getName();
			
			FileReader r = new FileReader(file);
			Interpreter i = new Interpreter(r, System.out, System.err, false);
			i.run();
			scripts.put(key, i);
		}
		catch (IOException e) {
			Log.warning("Failed to load BSH script: " + file.getPath());
			e.printStackTrace();
		}
	}
}