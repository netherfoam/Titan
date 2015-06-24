package org.maxgamer.rs.script;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.maxgamer.io.ScriptLoader;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

public class ScriptManager{
	/**
	 * The scripts we've loaded
	 */
	private HashMap<String, Class<OptionHandler>> scripts;
	
	/**
	 * Constructs a script manager for the given folder
	 * @param folder the folder we're getting scripts from
	 */
	public ScriptManager(){
		
	}
	
	public void reload(File folder){
		scripts = new HashMap<>();
		ScriptLoader<OptionHandler> s = new ScriptLoader<OptionHandler>(OptionHandler.class);
		HashMap<File, Class<OptionHandler>> files = s.getScripts(folder);
		
		for(Entry<File, Class<OptionHandler>> entry : files.entrySet()){
			scripts.put(entry.getKey().getPath().toLowerCase(), entry.getValue());
		}
	}
	
	/**
	 * Fetches an environment (Similar to an instance) of a script with the given parameters
	 * @param mob the mob who activated the script
	 * @param args the arguments used for the script when invoking it
	 * @param names the names for the script. The method attempts to find the most specific
	 * 		  script relating to the name. The last argument is the file name, the others
	 * 		  are folder names.  Eg, "gameobject_actions", "Rock", "Mine" will search both
	 * 		  "gameobject_actions\Rock\Mine.java" then "gameobject_actions\Mine.java", then
	 * 		  "Mine.java", returning null if none succeed
	 */
	public ScriptSpace get(Mob mob, Map<String, Object> args, String... names) {
		Class<OptionHandler> clazz = null;
		
		//Replace anything that isn't a valid char in java names with an underscore
		for(int i = 0; i < names.length; i++){
			names[i] = names[i].replaceAll("[^0-9A-Za-z_]", "").toLowerCase();
		}
		
		//Attempt to find which script to run, trying the most specific script first
		for(int i = 0; i < names.length; i++){
			String s = "";
			for(int n = 0; n < names.length - 1 - i; n++){
				s += names[n] + File.separatorChar;
			}
			s += names[names.length - 1] + ".class";
			
			clazz = scripts.get("scripts" + File.separatorChar + s);
			if(clazz != null) break;
		}
		
		//We had no script whatsoever
		if(clazz == null){
			Log.debug("Script not found: " + Arrays.toString(names) + ", available: " + scripts.keySet());
			return null;
		}
		
		//We found a script
		try{
			final OptionHandler h = clazz.newInstance();
			ScriptSpace script = new ScriptSpace(mob, args){
				@Override
				public void run() throws SuspendExecution{
					h.run(this.getMob(), this.getArgs());
				}
			};
			return script;
			
		}
		catch(Throwable t){
			Log.warning("Error constructing script " + Arrays.toString(names));
			t.printStackTrace();
			return null;
		}
	}
}