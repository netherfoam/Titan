package org.maxgamer.rs.model.javascript.interaction;

import java.io.File;

import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;

public class InteractionManager{
	private static final File INTERACTION_FOLDER = new File(JavaScriptFiber.SCRIPT_FOLDER, "interaction");
	
	/**
	 * Gets the interactions file for the given entity
	 * @param entity the entity to interact with
	 * @return the entity file or null if the file doesn't exist
	 */
	public File get(Entity entity){
		Class<?> clazz = entity.getClass();
		
		// We exhaust all superclass options as well as the base class
		while(clazz != Entity.class){
			String name = clazz.getSimpleName().toLowerCase();
			
			File f = new File(INTERACTION_FOLDER + File.separator + name, entity.getName() + ".js");
			if(f.exists()){
				return f;
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return null;
	}
	
	public String toFunction(String option){
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
}