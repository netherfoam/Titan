package org.maxgamer.rs.model.interfaces.impl.chat;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class IntRequestInterface extends IntInputInterface {
	private String question;
	
	public IntRequestInterface(Player p, String question) {
		super(p);
		if (question == null) question = "";
		this.question = question;
	}
	
	/**
	 * Closes this interface, but does not notify the player's windows
	 */
	@Override
	public void onClose() {
		super.onClose();
	}
	
	/**
	 * Opens this interface, but does not notify the player's windows
	 */
	@Override
	public void onOpen() {
		super.onOpen();
		getPlayer().getProtocol().invoke(108, question);
	}
	
	public abstract void onInput(long value);
}