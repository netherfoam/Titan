package org.maxgamer.rs.network.protocol;

import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.io.packet.PacketManager;

/**
 * @author netherfoam
 */
public abstract class ProtocolHandler<T extends Client> {
	/** The player this protocol belongs to */
	protected final T p;
	
	/**
	 * Creates a new protocol handler. This does not call the
	 * Player.setProtocol() method.
	 * @param p The player
	 */
	public ProtocolHandler(T p) {
		this.p = p;
	}
	
	/**
	 * The player who this protocol belongs to
	 * @return the player
	 */
	public T getPlayer() {
		return p;
	}
	
	/**
	 * Fetches the revision number the player is on.
	 * @return the revision number
	 */
	public abstract int getRevision();
	
	public abstract PacketManager<T> getPacketManager();
	
	/**
	 * Sends the player the required updates after a tick. If necessary, this
	 * will also update the player's map. This method sends all nearby players
	 * and their mask changes to the player, allowing them to see movement,
	 * animations, teleports and more. This method does not call the reset
	 * method on any masks.
	 */
	public abstract void sendUpdates();
	
	/**
	 * Sends a message to a player
	 * @param type the message type
	 * @param userFrom the user it was from, eg 'netherfoam sent trade request'
	 *        or null for none
	 * @param text the message 'sent trade request'
	 */
	public abstract void sendMessage(int type, String userFrom, String text);
	
	public abstract void sendMessage(String text);
	
	public abstract void sendFriend(String oldName, boolean online, String world);
	
	public abstract void removeFriend(String friend);
	
	public abstract void removeIgnore(String name);
	
	public abstract void sendUnlockFriendsList();
	
	public abstract void sendIgnores(String name);
	
	public abstract void sendUnlockIgnores();

	public abstract void sendSound(int i, int j, int k);
	
}