package org.maxgamer.rs.model.javascript.dialogue;

import java.io.File;

import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;

public class DialogueManager {
	private static final File DIALOGUE_FOLDER = new File(JavaScriptFiber.SCRIPT_FOLDER, "dialogue");
	
	/**
	 * Fetches the JavaScript file that handles dialogue for the given NPC and option
	 * @param npc the npc
	 * @param option the option that was clicked
	 * @return the File or null if it does not exist
	 */
	public File get(NPC npc, String option){
		File file = new File(DIALOGUE_FOLDER + "/npc/" + npc.getName().toLowerCase() + "-" + option.toLowerCase() + ".js");
		if(file.exists() == false){
			System.out.println(file.getPath() + " not found");
			file = new File(DIALOGUE_FOLDER + "/npc/" + npc.getName().toLowerCase() + ".js");
			
			if(file.exists() == false){
				System.out.println(file.getPath() + " not found");
				return null;
			}
		}
		
		return file;
	}
}
