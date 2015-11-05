package org.maxgamer.rs.interfaces.impl.chat;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class IntInputInterface extends Interface {
	public static final short CHILD_ID = 73; //TODO: 73 is probably the incorrect childId for this interface.
	
	public IntInputInterface(Player p) {
		//super(p, p.getWindow(), CHILD_ID, (short) 13, false); //maybe 7 instead of 13 for childpos
		super(p, p.getWindow().getInterface(ChatInterface.CHILD_ID), (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 13 : 7), false);
		setChildId(CHILD_ID);
	}
	
	/**
	 * Closes this interface, but does not notify the player's windows
	 */
	@Override
	public void onClose() {
		super.onClose();
	}
	
	@Override
	public boolean isServerSidedClose() {
		return false;
	}
	
	/**
	 * Opens this interface, but does not notify the player's windows
	 */
	@Override
	public void onOpen() {
		
	}
	
	public abstract void onInput(long value);
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		//This can't be clicked
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
}