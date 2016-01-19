package org.maxgamer.rs.model.javascript.dialogue;

import java.io.IOException;

import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.MobUseNPCEvent;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;

public class TalkToListener implements EventListener {
	
	@EventHandler(priority = EventPriority.HIGHEST, consumer=true, skipIfCancelled=true)
	public void onTalkTo(final MobUseNPCEvent e) throws IOException{
		if(e.getMob() instanceof Player == false) return; //We only do this for players
		if(e.getOption().equalsIgnoreCase("Talk-to") == false) return; //Only handle 'talk-to' option

		// TODO: If the NPC moves while we are walking to them, this will not update the destination.
		// This is a bug.
		
		AStar finder = new AStar(10);
		Path path = finder.findPath(e.getMob(), e.getTarget().getLocation(), 1);
		
		Action talk = new DialogueAction(e.getMob(), e.getTarget(), e.getOption());
		WalkAction walk = new WalkAction(e.getMob(), path);
		
		if(path.hasFailed()){
			e.getMob().getActions().queue(walk);
		}
		else if(path.isEmpty()){
			e.getMob().getActions().queue(talk);
		}
		else{
			talk.pair(walk);
			e.getMob().getActions().queue(walk);
			e.getMob().getActions().insertAfter(walk, talk);
		}
		
		e.consume();
	}
}
