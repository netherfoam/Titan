package org.maxgamer.rs.interfaces.impl.dialogue;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class DialogueInterface extends Interface {
	
	public DialogueInterface(Player p, short childId) {
		//Dialogue interfaces are part of the chatbox interface.
		//super(p, p.getWindow().getInterface(ChatInterface.CHILD_ID), childId, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 7 : 13), true);
		super(p, p.getWindow().getInterface(ChatInterface.CHILD_ID), childId, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 7 : 13), true);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
}