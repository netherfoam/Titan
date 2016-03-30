package org.maxgamer.rs.model.entity.mob.persona.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.logonv4.game.LogonAPI.RemoteWorld;
import org.maxgamer.rs.model.events.mob.persona.player.AddFriendEvent;
import org.maxgamer.rs.model.events.mob.persona.player.RemoveFriendEvent;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author netherfoam, alva
 */
public final class FriendsList implements YMLSerializable {
	public static final int MAX_FRIENDS = 200;
	public static final int MAX_IGNORES = 100;
	
	// HashMap of (Lowercase name) to (Proper case name) for friends.
	private HashMap<String, String> friends = new HashMap<String, String>();
	
	// HashMap of (Lowercase name) to (Proper case name) for ignores.
	private HashMap<String, String> ignores = new HashMap<String, String>();
	
	/**
	 * The owner of the friends list.
	 */
	protected final Client p;
	
	/**
	 * Constructs a new FriendList for the given player
	 * 
	 * @param p the player
	 */
	public FriendsList(Client p) {
		this.p = p;
	}
	
	/**
	 * The owner of this friends list
	 * 
	 * @return The owner of this friends list
	 */
	public Client getPlayer() {
		return p;
	}
	
	/**
	 * Fetches the friends stored in the list which can't be instantiated.
	 * 
	 * @return Return a deep copy of the friends stored in the list, possibly
	 *         empty.
	 */
	public ArrayList<String> getFriends() {
		return new ArrayList<String>(this.friends.values());
	}
	
	/**
	 * Fetches the ignores stored in the list which can't be instantiated.
	 * 
	 * @return Return a deep copy of the ignores stored in the list, possibly
	 *         empty.
	 */
	public ArrayList<String> getIgnores() {
		return new ArrayList<String>(this.ignores.values());
	}
	
	/**
	 * Add a friend to the List and send the friend (a check whether online or
	 * not) to the player's friendslist ingame. FriendsList can't contain more
	 * than 200 friends. This updates the client
	 * 
	 * @param name of friend/player.
	 * @return true if the friend was added, false if there are too many friends
	 *         already
	 */
	public boolean addFriend(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name of friend may not be null or empty");
		}
		
		if (isFriend(name)) {
			throw new IllegalArgumentException("That name is already friended");
		}
		
		if (friends.size() >= MAX_FRIENDS) {
			return false;
		}
		
		//This works because we have a RemoteServer which represents this server.
		//This we don't need to search our online players.
		RemoteWorld remote = null;
		for (RemoteWorld server : Core.getServer().getLogon().getAPI().getWorlds()) {
			if (server.isOnline(name)) {
				remote = server;
				break;
			}
		}
		
		friends.put(name.toLowerCase(), name);
		
		AddFriendEvent e = new AddFriendEvent(p, name.toLowerCase(), 0);
		e.call();
		
		if (remote == null) {
			setOnline(name, false, null);
		}
		else {
			setOnline(name, true, remote.getWorldId() + ": " + remote.getName());
		}
		
		return true;
	}
	
	/**
	 * Returns true if the given name is on the friends list
	 * 
	 * @param name the name of the friend
	 * @return true if the given name is on the friends list
	 */
	public boolean isFriend(String name) {
		return friends.containsKey(name.toLowerCase());
	}
	
	/**
	 * Sets the status of the given player to online. This method is case
	 * insensitive.
	 * 
	 * @param name the name of the player to set to online
	 * @param online true for online, false if offline
	 */
	public void setOnline(String name, boolean online, String world) {
		String oldName = friends.get(name.toLowerCase());
		if (oldName == null) {
			throw new IllegalArgumentException("The user " + name + " is not a friend of " + getPlayer().getName() + " therefore they can't be set to online.");
		}
		
		getPlayer().getProtocol().sendFriend(oldName, online, world);
	}
	
	/**
	 * Returns true if the given name is on the ignore list
	 * 
	 * @param name the name of the ignore
	 * @return true if the given name is on the ignore list
	 */
	public boolean isIgnore(String name) {
		return ignores.containsKey(name.toLowerCase());
	}
	
	/**
	 * Add an ignore the List and send the ignore to the player ingame.
	 * IgnoresList can't contain more than 100 ignores.
	 * 
	 * @param name of ignore/player.
	 */
	public boolean addIgnore(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name of ignore may not be null or empty");
		}
		
		if (isIgnore(name)) {
			throw new IllegalArgumentException("That name is already being ignored");
		}
		
		if (ignores.size() >= MAX_IGNORES) {
			return false;
		}
		ignores.put(name.toLowerCase(), name);
		p.getProtocol().sendIgnores(name);
		return true;
	}
	
	/**
	 * Removes the friend from the List. This does not remove the player from
	 * the client's friends list, that is done by the client automatically when
	 * selecting "Delete Friend"
	 * 
	 * @param friend 's name.
	 * @param update If we should invoke the ignore removal client script on the
	 *        player (Automatically done by the client in some cases)
	 * @return true if removed, false if not found
	 */
	public boolean removeFriend(String friend, boolean update) {
		if (update) {
			getPlayer().getProtocol().removeFriend(friend);
		}
		
		RemoveFriendEvent e = new RemoveFriendEvent(p, friend.toLowerCase());
		e.call();
		
		return friends.remove(friend.toLowerCase()) != null;
	}
	
	/**
	 * Removes the ignore from the List. This does not remove the player from
	 * the client's ignore list, that is done by the client automatically when
	 * selecting "Delete Ignore"
	 * 
	 * @param ignore 's name.
	 * @param update If we should invoke the ignore removal client script on the
	 *        player (Automatically done by the client in some cases)
	 * @return true if removed, false if not found
	 */
	public boolean removeIgnore(String ignore, boolean update) {
		if (update) {
			getPlayer().getProtocol().removeIgnore(ignore);
		}
		return ignores.remove(ignore.toLowerCase()) != null;
	}
	
	/**
	 * Saves the FriendsList to YML file.
	 */
	@Override
	public ConfigSection serialize() {
		ConfigSection s = new ConfigSection();
		int i = 0;
		for (String name : friends.values()) {
			s.set("friends." + (i++), name);
		}
		for (String name : ignores.values()) {
			s.set("ignores." + (i++), name);
		}
		return s;
	}
	
	/**
	 * Loading the friendslist from YML file.
	 */
	@Override
	public void deserialize(ConfigSection map) {
		getPlayer().getProtocol().sendUnlockIgnores();
		
		ConfigSection friends = map.getSection("friends", null);
		if (friends != null) {
			for (String s : friends.getKeys()) {
				String name = friends.getString(s);
				if(isFriend(name) == false) addFriend(name);
			}
		}
		
		ConfigSection ignores = map.getSection("ignores", null);
		if (ignores != null) {
			for (String s : ignores.getKeys()) {
				String name = ignores.getString(s);
				
				// TODO: This is a workaround, because ignores do not seem to be
				// cleared
				// when the player logs out of the game and rejoins, thus we end
				// up with
				// duplicates. This removes the names forcefully before adding
				// them, so
				// no duplicates can should occur under normal use. This is
				// still not right.
				getPlayer().getProtocol().removeIgnore(name);
				
				addIgnore(name);
			}
		}
		
		if (this.friends.isEmpty()) {
			p.getProtocol().sendUnlockFriendsList();
		}
	}
	
	public boolean isFriendsFull(){
		return this.friends.size() >= MAX_FRIENDS;
	}
	
	public boolean isIgnoresFull(){
		return this.friends.size() >= MAX_FRIENDS;
	}
}