package org.maxgamer.rs.script;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.maxgamer.io.ScriptLoader;
import org.maxgamer.io.ScriptLoader.ClassTransformer;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.instrument.QuasarInstrumentor;

public class ScriptManager {
	/**
	 * Quick class transformer to request that Quasar parse classes first, because we manage
	 * to somehow skip the JavaAgent otherwise.
	 */
	private ClassTransformer quasar = new ClassTransformer() {
		@Override
		public byte[] transform(String clazz, byte[] src) {
			QuasarInstrumentor inst = new QuasarInstrumentor(Thread.currentThread().getContextClassLoader());
			return inst.instrumentClass(clazz, src);
		}
	};
	
	/**
	 * The scripts we've loaded
	 */
	private HashMap<String, Class<ActionHandler>> scripts;
	
	/**
	 * Constructs a script manager for the given folder
	 * @param folder the folder we're getting scripts from
	 */
	public ScriptManager() {
		this.scripts = new HashMap<>();
	}
	
	/**
	 * Loads all of the scripts from the given folder
	 * @param folder the folder to load all of the scripts from
	 */
	public void load(File folder) {
		ScriptLoader<ActionHandler> s = new ScriptLoader<ActionHandler>(ActionHandler.class, quasar);
		HashMap<File, Class<ActionHandler>> files = s.getScripts(folder);
		
		for (Entry<File, Class<ActionHandler>> entry : files.entrySet()) {
			if(entry.getValue().getAnnotation(Script.class) == null){
				Log.warning("Script " + entry.getKey() + " does not have an @Script annotation, it will not be loaded.");
				continue;
			}
			
			scripts.put(entry.getKey().getPath().toLowerCase(), entry.getValue());
		}
	}
	
	/**
	 * Clears all existing scripts from this script manager
	 */
	public void clear() {
		scripts.clear();
	}
	
	private static boolean contains(int[] array, int v){
		for(int i : array){
			if(i == v) return true;
		}
		return false;
	}
	
	private static boolean contains(String[] array, String v){
		for(String s : array){
			if(s.equalsIgnoreCase(v)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * returns true if the given script exists
	 * @param names the script folder names, see also {@link ScriptManager#get(Mob, Action, Map, String...)}
	 * @return true if the given script exists else false
	 * @throws NullPointerException if clazz is null
	 */
	public boolean has(Object target, int id, String name, String option) {
		if(target == null) throw new NullPointerException("Class may not be null");
		
		for(Class<ActionHandler> handler : this.scripts.values()){
			Script s = handler.getAnnotation(Script.class);
			
			if(s.type().isInstance(target) == false){
				continue;
			}
			
			if(s.ids().length > 0 && contains(s.ids(), id) == false){
				continue;
			}
			
			if(s.names().length > 0 && contains(s.names(), name) == false){
				continue;
			}
			
			if(s.options().length > 0 && contains(s.options(), option) == false){
				continue;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Fetches an environment (Similar to an instance) of a script with the
	 * given parameters
	 * @param mob the mob who activated the script
	 * @param args the arguments used for the script when invoking it
	 * @param names the names for the script. The method attempts to find the
	 *        most specific script relating to the name. The last argument is
	 *        the file name, the others are folder names. Eg,
	 *        "gameobject_actions", "Rock", "Mine" will search both
	 *        "gameobject_actions\Rock\Mine.java" then
	 *        "gameobject_actions\Mine.java", then "Mine.java", returning null
	 *        if none succeed
	 */
	public ScriptSpace get(Mob mob, Action a, Map<String, Object> args, Object target, int id, String name, String option) {
		Class<ActionHandler> clazz = null;
		if(target == null) throw new NullPointerException("Target may not be null");
		
		for(Class<ActionHandler> handler : this.scripts.values()){
			Script s = handler.getAnnotation(Script.class);
			
			if(s.type().isInstance(target) == false){
				continue;
			}
			
			if(s.ids().length > 0 && contains(s.ids(), id) == false){
				continue;
			}
			
			if(s.names().length > 0 && contains(s.names(), name) == false){
				continue;
			}
			
			if(s.options().length > 0 && contains(s.options(), option) == false){
				continue;
			}
			
			clazz = handler;
			break;
		}
		
		if(clazz == null){
			return null;
		}
		
		//We found a script
		try {
			final ActionHandler h = clazz.newInstance();
			h.setAction(a);
			ScriptSpace script = new ScriptSpace(mob, args) {
				@Override
				public void run() throws SuspendExecution {
					h.run(this.getMob(), this.getArgs());
				}
			};
			return script;
			
		}
		catch (Throwable t) {
			Log.warning("Error constructing script " + id + "[" + name + "]#" + option);
			t.printStackTrace();
			return null;
		}
	}
}