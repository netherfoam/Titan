package org.maxgamer.rs.model.lobby;

import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.structure.TrieSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class Lobby {
    /**
     * The map of name(lowercase) to lobby client
     */
    private HashMap<String, LobbyPlayer> clients;
    private TrieSet names;

    /**
     * Creates a new, empty lobby.
     */
    public Lobby() {
        this.clients = new HashMap<>();
        this.names = new TrieSet();
    }

    /**
     * Adds the given player to this lobby. If an existing client is in the
     * lobby of the same name,this will override it. (That is dangerous)
     *
     * @param c the player to add
     */
    public void add(LobbyPlayer c) {
        String s = c.getName().toLowerCase();
        this.clients.put(s, c);
        this.names.add(s);
    }

    /**
     * Removes the given player from this lobby. If the player is not in the
     * lobby, nothing changes.
     *
     * @param c the lobby player
     */
    public void remove(LobbyPlayer c) {
        String s = c.getName().toLowerCase();
        this.clients.remove(s);
        names.add(s);
    }

    public int size() {
        return clients.size();
    }

    /**
     * Returns an immutable list of players which are currently in the lobby.
     *
     * @return an immutable list of players which are currently in the lobby.
     */
    public Collection<LobbyPlayer> getPlayers() {
        return Collections.unmodifiableCollection(this.clients.values());
    }

    /**
     * Fetches the lobby player by name from this lobby.
     *
     * @param name the name of the player, case insensitive.
     * @return the player or null if not found.
     */
    public LobbyPlayer getPlayer(String name, boolean autocomplete) {
        name = name.toLowerCase();
        if (autocomplete) {
            String s = names.nearestKey(name);
            if (s != null) {
                return this.clients.get(s);
            }
            //If not, there might be a bug if the next call to clients.get() succeeds.
        }

        return this.clients.get(name);
    }
}