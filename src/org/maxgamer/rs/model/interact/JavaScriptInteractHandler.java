package org.maxgamer.rs.model.interact;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.interact.use.ItemOptionUse;
import org.maxgamer.rs.model.interact.use.OptionUse;
import org.maxgamer.rs.model.javascript.JavaScriptCall;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class JavaScriptInteractHandler implements InteractionHandler{
	/**
	 * The folder where interaction javascripts are stored
	 */
	private static final File INTERACTION_FOLDER = new File(JavaScriptFiber.SCRIPT_FOLDER, "interaction");
	
	/**
	 * Convert the given entity and option into a file, or null if there's no handler for that entity/file
	 * @param entity the entity, the name and class type is used
	 * @param option the option that was clicked
	 * @return the file or null if no good file was found
	 */
	private ArrayList<File> get(Interactable entity, String option){
		Class<?> clazz = entity.getClass();
		
		String entityName = entity.getName();
		if(entityName.matches("[A-Za-z0-9 \\-_]*$") == false){
			// Eg.  An 'Amulet of glory (4)' becomes 'Amulet of glory', with the special characters trimmed, and excess spaces removed.
			entityName = entityName.replaceAll("[^A-Za-z0-9\\-_ ].*", "").trim();
		}
		
		ArrayList<File> files = new ArrayList<File>();
		
		// We exhaust all superclass options as well as the base class
		while(clazz != Object.class){
			String className = clazz.getSimpleName().toLowerCase();
			
			File f = new File(INTERACTION_FOLDER + File.separator + className, entityName + ".js");
			if(f.exists()){
				files.add(f);
			}
			
			f = new File(INTERACTION_FOLDER + File.separator + className , option + ".js");
			if(f.exists()){
				files.add(f);
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return files;
	}
	
	/**
	 * Converts a given string into a camel-cased function name. Eg "Chop-down" becomes "chopDown", or "Rub Amulet" becomes "rubAmulet"
	 * @param option the option, eg "Search"
	 * @return the function name, eg "search"
	 */
	private String toFunction(String option){
		StringBuilder sb = new StringBuilder(option.length());
		option = option.toLowerCase();
		
		for(int i = 0; i < option.length(); i++){
			char c = option.charAt(i);
			
			if(c == ' ' || c == '-' || c == '_'){
				i++;
				if(option.length() <= i) break;
				c = option.charAt(i);
				sb.append(Character.toUpperCase(c));
				continue;
			}
			else{
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Handles when we receive an interaction with a slot as well, which we discard
	 */
	@Interact
	public void javascript(Mob source, Interactable target, ItemOptionUse use) throws SuspendExecution, NotHandledException {
		javascript(source, target, use.getOption());
	}

	@Interact
	public void javascript(Mob source, Interactable target, OptionUse use) throws SuspendExecution, NotHandledException {
		this.javascript(source, target, use.getOption());
	}
	
	/**
	 * Handles when we receive an interaction
	 */
	public void javascript(Mob source, Interactable target, String option) throws SuspendExecution, NotHandledException {
		ArrayList<File> files = this.get(target, option);
		String function = this.toFunction(option);
		
		if(target instanceof Mob){
			// Turn the target around so they respond to the interaction
			((Mob) target).setFacing(Facing.face(source.getCenter()));
		}
		
		for(File f : files) {
			JavaScriptFiber fiber = new JavaScriptFiber();
			
			try{
				fiber.set("fiber", fiber);
				fiber.set("player", source);
				
				if(fiber.parse("lib/core.js").isFinished() == false){
					throw new RuntimeException("lib/core.js cannot contain pauses outside of functions.");
				}
				if(fiber.parse("lib/dialogue.js").isFinished() == false){
					throw new RuntimeException("lib/dialogue.js cannot contain pauses outside of functions.");
				}
				if(fiber.parse(f).isFinished() == false){
					throw new RuntimeException(f + " cannot contain pauses outside of functions.");
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			if(target instanceof Entity){
				source.face((Entity) target);
			}
			
			JavaScriptCall call = null;
			try {
				call = fiber.invoke(function, source, target);
			}
			catch (NoSuchMethodException e) {
				continue;
			}
			
			Action.wait(1);
			while(call.isFinished() == false){
				Action.wait(1);
			}
			return;
		}
		
		if(files.isEmpty() == false) {
			System.out.println("Files " + Arrays.toString(files.toArray(new File[files.size()])) + " exists, but the function " + function + "() does not.");
		}
		
		throw new NotHandledException();
	}
}