package org.maxgamer.rs.interfaces.impl.dialogue;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class DialogueFork extends DialogueInterface {
	public DialogueFork(Player p, String... options) {
		super(p, (short) (225 + options.length * 2));
		if (options.length < 2 || options.length > 5) {
			throw new IllegalArgumentException("Options length must be between 2 and 5 inclusive.");
		}
		for (int i = 0; i < options.length; i++) {
			setString(2 + i, options[i]);
		}
	}
	
	@Override
	public void onClose() {
		super.onClose();
	}
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		getPlayer().getWindow().close(this);
	}
}