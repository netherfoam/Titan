package org.maxgamer.rs.model.interfaces;

import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;

/**
 * @author netherfoam
 */
public abstract class Window {
	/** Player who can access this interface */
	protected final Player player;
	protected int interfaceId = -1;

	public Window(Player p) {
		if (p == null) {
			throw new NullPointerException("Player may not be null");
		}

		this.player = p;
	}

	public void setChildId(int id) {
		if (id < 0 || id > 0xFFFF) {
			throw new IllegalArgumentException("InterfaceId must be a short, and therebefore between 0 and " + (0xFFFF));
		}

		if (this.isOpen()) {
			throw new IllegalStateException("Interface is already open and therefore should not have its type ID modified");
		}

		this.interfaceId = id;
	}

	public int getChildId() {
		// TODO: Rename
		return interfaceId;
	}

	public final Player getPlayer() {
		return player;
	}

	/**
	 * Sends the right click/use configuration for items/spells/prayers on this interface. To
	 * convert dementhium to Blaze, dementhium is of the format:<br>
	 * sendAMask(Player player, int set1, int set2, int interfaceId1, int childId1, int
	 * interfaceId2, int childId2)<br>
	 * Where interfaceId2 << 16 | childId2 = is our int flags,<br>
	 * set2 is our length,<br>
	 * set1 is our offset,<br>
	 * childId1 is our componentId<br>
	 * 
	 * @param flags
	 *            The flags. Use a SettingsBuilder to generate these easily
	 * @param offset
	 *            the starting index of the containers items. Eg for inventory this is 0.
	 * @param length
	 *            the number of items. Eg for inventory this is 27
	 * @param componentId
	 *            the child ID of the interface to modify. This is frequently 0.
	 */
	public void setAccessMask(int flags, int offset, int length, int componentId) {
		if(interfaceId == -1){
			throw new IllegalStateException("InterfaceID has not yet been set! Call setChildId() first!");
		}
		if (offset > 0xFFFF)
			throw new IllegalArgumentException("Length must be <= " + 0xFFFF + ", given " + length);
		if (length < 0)
			throw new IllegalArgumentException("length must be >= 0, given " + length);
		if (length > 0xFFFF)
			throw new IllegalArgumentException("Length must be <= " + 0xFFFF + ", given " + length);
		if (componentId > 0xFFFF)
			throw new IllegalArgumentException("ChildId must be <= " + 0xFFFF + ", given " + componentId);

		// TODO: Move this to Protocol
		RSOutgoingPacket out = new RSOutgoingPacket(119);
		out.writeInt2(flags);
		out.writeShortA(length);
		out.writeShortA(offset);
		out.writeLEInt((this.getChildId() << 16) | componentId);

		getPlayer().write(out);
	}

	/**
	 * Unlocks the specified {@code options} for the specified componentId with the specified offset
	 * and length.
	 * 
	 * @param offset
	 *            the starting index of the containers items. Eg for inventory this is 0.
	 * @param length
	 *            the number of items. Eg for inventory this is 27
	 * @param componentId
	 *            the child ID of the interface to modify. This is frequently 0.
	 * @param options
	 *            the options to unlock in this window
	 */
	public void setUnlockOptions(int offset, int length, int componentId, int... options) {
		int hash = 0;
		for (int o : options)
			hash |= 2 << o;
		setAccessMask(hash, offset, length, componentId);
	}

	/**
	 * Sets the String overlay for the given component for this interface.
	 * 
	 * @param componentId
	 *            the ID, these start at 0
	 * @param s
	 *            the String to set
	 */
	public void setString(int componentId, String s) {
		if(interfaceId == -1){
			throw new IllegalStateException("InterfaceID has not yet been set! Call setChildId() first!");
		}
		
		RSOutgoingPacket out = new RSOutgoingPacket(33);

		out.writePJStr1(String.valueOf(s)); // String.valueOf() turns null into 'null'

		out.writeLEShort(componentId);
		out.writeLEShort(getChildId());

		getPlayer().write(out);
	}

	public void setComponentVisible(int componentId, boolean visible) {
		if(interfaceId == -1){
			throw new IllegalStateException("InterfaceID has not yet been set! Call setChildId() first!");
		}
		
		getPlayer().getProtocol().unlockInterfaceComponent(interfaceId, componentId, visible);
	}

	public abstract void onOpen();

	public abstract void onClose();

	public abstract boolean isOpen();

	/**
	 * Called when the owner of this interface clicks on a button in this interface
	 * 
	 * @param option
	 *            the option number that was used. Eg, right click provides multiple options
	 * @param buttonId
	 *            the buttonId that was clicked
	 * @param slotId
	 *            the slot that was clicked, possibly 65536 (-1)
	 * @param itemId
	 *            the item ID that was clicked, possibly -1, not to be trusted.
	 */
	public abstract void onClick(int option, int buttonId, int slotId, int itemId);

	public void onClick(Mob target, int buttonId, int slotId, int itemId, boolean run) {
		Log.debug("Unhandled button on target. Button=" + buttonId + ", slot=" + slotId + ", item=" + itemId + ", target=" + target + ", run=" + run + ", owner=" + getPlayer());
	}

	public void onDrag(Window to, int fromItemId, int toItemId, int tabId, int fromSlot, int toSlot) {
		Log.debug("Unhandled drag. Target window=" + to + ", fromItem=" + fromItemId + ", toItem=" + toItemId + ", tab=" + tabId + ", fromSlot=" + fromSlot + ", toSlot=" + toSlot + ", owner=" + getPlayer());
	}

	public void onUse(Window to, int fromButtonId, int fromItemId, int fromSlot, int toButtonId, int toItemId, int toSlot) {
		Log.debug("Unhandled drag. Target window=" + to + ", fromItem=" + fromItemId + ", toItem=" + toItemId + ",  fromSlot=" + fromSlot + ", toSlot=" + toSlot + ", owner=" + getPlayer());
	}
}