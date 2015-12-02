package org.maxgamer.rs.model.javascript.dialogue;

import java.io.IOException;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.events.mob.MobUseNPCEvent;
import org.maxgamer.rs.interfaces.impl.dialogue.ThoughtDialogue;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.action.FriendFollow;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;
import org.maxgamer.rs.model.map.path.AStar;

import co.paralleluniverse.fibers.SuspendExecution;

public class TalkToListener implements EventListener {
	
	@EventHandler(priority = EventPriority.HIGHEST, consumer=true, skipIfCancelled=true)
	public void onTalkTo(final MobUseNPCEvent e) throws IOException{
		if(e.getMob() instanceof Player == false) return; //We only do this for players
		if(e.getOption().equalsIgnoreCase("Talk-to") == false) return; //Only handle 'talk-to' option

		final FriendFollow follow = new FriendFollow(e.getMob(), e.getTarget(), 1, 10, new AStar(5)){
			@Override
			public void onWait(){
				this.yield();
			}
		};
		
		Action talkAction = new Action(e.getMob()){
			private JavaScriptFiber fiber;
			
			@Override
			public void run() throws SuspendExecution{
				NPC npc = e.getTarget();
				Player player = (Player) e.getMob();
				player.face(npc);
				if(npc.getActions().isEmpty()){
					npc.face(player);
				}
				
				fiber = Core.getServer().getDialogue().get(npc, e.getOption());
				if(fiber == null){
					ThoughtDialogue thought = new ThoughtDialogue(player) {
						@Override
						public void onContinue() {}
					};
					
					thought.setText("The " + npc.getName() + " seems... vacant");
					player.getWindow().open(thought);
					return;
				}
				else{
					try {
						fiber.set("player", player);
						fiber.start();
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				
				if(e.getMob().getActions().isQueued(follow)){
					e.getMob().getActions().cancel(follow);
				}
				
				/* Wait until the fiber terminates, this player is "busy" */
				while(fiber.isFinished() == false){
					Action.wait(1);
				}
			}

			@Override
			protected void onCancel() {
				//Stop following
				if(e.getMob().getActions().isQueued(follow)){
					e.getMob().getActions().cancel(follow);
				}
				if(follow.isQueued()){
					follow.cancel();
				}
				if(fiber != null){
					fiber.stop();
				}
			}

			@Override
			protected boolean isCancellable() {
				return true;
			}
		};
		
		e.getMob().getActions().queue(follow);
		e.getMob().getActions().insertAfter(follow, talkAction);
		e.consume();
	}
}
