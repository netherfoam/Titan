package org.maxgamer.rs.interfaces.impl.dialogue;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class StringRequestInterface extends StringInputInterface {
	private String question;
	
	/**
	 * Constructs a new StringRequestInterface. A request interface will force
	 * the box to pop up to the player, unlike a standard StringInputInterface
	 * which assumes that the player already has the box popped up (Such as when
	 * adding friends, the client automatically adds the dialogue box)
	 * @param p the player
	 * @param question the question to ask (Eg "Who?" "Where?")
	 */
	public StringRequestInterface(Player p, String question) {
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
		getPlayer().getProtocol().invoke(110, question);
	}
	
	public abstract void onInput(String value);
}