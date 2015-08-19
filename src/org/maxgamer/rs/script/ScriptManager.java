package org.maxgamer.rs.script;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

public class ScriptManager {
	/**
	 * The scripts we've loaded
	 */
	private Collection<Class<? extends ActionHandler>> scripts;
	
	private ScriptClassLoader loader;
	
	/**
	 * Constructs a script manager for the given folder
	 * @param folder the folder we're getting scripts from
	 */
	public ScriptManager() {
		this.scripts = new ArrayList<>();
		this.loader = new ScriptClassLoader(Core.CLASS_LOADER);
	}
	
	/**
	 * Loads all of the scripts from the given folder
	 * @param folder the folder to load all of the scripts from
	 */
	@SuppressWarnings("unchecked")
	public void load(File folder) {
		//ScriptLoader<ActionHandler> s = new ScriptLoader<ActionHandler>(ActionHandler.class, quasar);
		loader.reload(folder);
		Collection<Class<?>> classes = loader.getClasses(); 
		
		for (Class<?> c : classes) {
			if(isSuperClass(c, ActionHandler.class) == false) continue;
			if(c.getAnnotation(Script.class) == null){
				Log.warning("Script ActionHandler " + c.getName() + " does not have an @Script annotation, it will not be loaded.");
				continue;
			}
			
			scripts.add((Class<? extends ActionHandler>)c);
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
		
		for(Class<? extends ActionHandler> handler : this.scripts){
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
		Class<? extends ActionHandler> clazz = null;
		if(target == null) throw new NullPointerException("Target may not be null");
		
		for(Class<? extends ActionHandler> handler : this.scripts){
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
	
	/**
	 * Returns true if the given base class is a subclass of the given
	 * superclass.
	 * @param base The base class
	 * @param superClazz The class which might be a super class
	 * @return true if the given base class is a subclass of the given
	 *         superclass.
	 */
	private static boolean isSuperClass(Class<?> base, Class<?> superClazz) {
		while (base != null) {
			if (base.equals(superClazz)) {
				return true;
			}
			base = base.getSuperclass();
		}
		return false;
	}
}