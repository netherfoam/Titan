package org.maxgamer.rs.interfaces;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class Interface extends Window {

	protected Window parent;

	/**
	 * True if the interface may be clicked on, false if it is click through
	 * (Example, some overlays like the wilderness overlay are actually
	 * interfaces, and they ignore mouse clicks).
	 */
	protected boolean clickable;

	/**
	 * The position inside the parent this interface is to be placed in. This is
	 * neither the parent nor the child interface ID, it is not an interface at
	 * all. It has a unique ID for each position inside the parent.
	 */
	protected short childPos;

	/**
	 * Represents an interface for a single player.
	 * 
	 * @param p
	 *            The player
	 * @param childId
	 *            The unique id for this interface, this is actually unique.
	 */
	public Interface(Player p, Window parent, int childPos, boolean clickable) {
		super(p);
		this.parent = parent;
		this.clickable = clickable;
		
		if(childPos > 0xFFFF){
			throw new IllegalArgumentException("Bad childPos " + childPos);
		}
		this.childPos = (short) childPos; 
	}

	/**
	 * True if this interface is clickable, false if clicks go through the
	 * interface
	 * 
	 * @return True if this interface is clickable, false if clicks go through
	 *         the interface
	 */
	public boolean isClickable() {
		return clickable;
	}

	/**
	 * Closes this interface, but does not notify the player's windows
	 */

	public void onClose() {

	}

	/**
	 * Opens this interface, but does not notify the player's windows
	 */

	public void onOpen() {

	}

	/**
	 * True if this interface is currently visible to the player, false if it is
	 * not
	 */
	public final boolean isOpen() {
		return getPlayer().getPanes().getActive().isOpen(this);
	}

	/**
	 * When this interface has its 'X' pressed, should the client close the
	 * interface immediately and notify the server (false) or should it notify
	 * the server, in which case, the server will tell the client to close it
	 * later.
	 * 
	 * @return true if closing is handled server-sided, false otherwise.
	 */
	public abstract boolean isServerSidedClose();

	/**
	 * True if this interface remains open when the player moves, false if it
	 * should auto-close when the player attempts to move.
	 * 
	 * @return true if the interface can be used on the run.
	 */
	public abstract boolean isMobile();

	public Window getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}