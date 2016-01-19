package org.maxgamer.rs.model.javascript.dialogue;

import java.io.File;
import java.io.IOException;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.interfaces.impl.dialogue.ThoughtDialogue;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;
import org.mozilla.javascript.ContinuationPending;

import co.paralleluniverse.fibers.SuspendExecution;

public class DialogueAction extends Action{
	private NPC npc;
	private String option;
	
	public DialogueAction(Mob mob, NPC target, String option) {
		super(mob);
		this.npc = target;
		this.option = option;
	}
	
	@Override
	public void run() throws SuspendExecution{
		Player player = (Player) getOwner();
		player.face(npc);
		if(npc.getActions().isEmpty()){
			npc.face(player);
		}
		
		File f = Core.getServer().getDialogue().get(npc, option);
		if(f == null){
			ThoughtDialogue thought = new ThoughtDialogue(player) {
				@Override
				public void onContinue() {}
			};
			
			thought.setText("The " + npc.getName() + " seems... vacant");
			player.getWindow().open(thought);
			return;
		}
		else{
			JavaScriptFiber fiber = new JavaScriptFiber(Core.CLASS_LOADER);
			fiber.set("player", player);
			fiber.set("npc", npc);
			
			try {
				fiber.parse("lib/core.js");
				fiber.parse("lib/dialogue.js");
				fiber.parse(f);
			}
			catch(ContinuationPending p){
				/* We're waiting for an event to happen. It is someone elses responsibility to call
				 * the resume method now though. */
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		/* Wait until the fiber terminates, this player is "busy" */
		while(true){
			Action.wait(1);
		}
	}

	@Override
	protected void onCancel() {
		
	}

	@Override
	protected boolean isCancellable() {
		return true;
	}
	
}