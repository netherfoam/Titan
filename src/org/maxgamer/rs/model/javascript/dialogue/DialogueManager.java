package org.maxgamer.rs.model.javascript.dialogue;

import java.io.File;
import java.io.IOException;

import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;

public class DialogueManager {
	private static final File DIALOGUE_FOLDER = new File(JavaScriptFiber.SCRIPT_FOLDER, "dialogue");
	
	/**
	 * Constructs a JavaScriptFiber for the interactin with the given NPC.
	 * This sets "npc" variable in the script and includes dialogue and core.js.
	 * @param npc the npc
	 * @param option the option that was clicked
	 * @return the JavaScriptFiber or null if no file was found for the NPC script
	 */
	public JavaScriptFiber get(NPC npc, String option) {
		File file = new File(DIALOGUE_FOLDER + "/npc/" + npc.getName().toLowerCase() + "-" + option.toLowerCase() + ".js");
		if(file.exists() == false){
			System.out.println(file.getPath() + " not found");
			file = new File(DIALOGUE_FOLDER + "/npc/" + npc.getName().toLowerCase() + ".js");
			
			if(file.exists() == false){
				System.out.println(file.getPath() + " not found");
				return null;
			}
		}
		
		try {
			JavaScriptFiber fiber = new JavaScriptFiber(file);
			fiber.set("npc", npc);
			fiber.include("lib/dialogue.js");
			return fiber;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
