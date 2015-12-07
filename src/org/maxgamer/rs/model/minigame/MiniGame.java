package org.maxgamer.rs.model.minigame;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.map.MapBuilder;

/**
 * TODO finish this implementation
 * 
 * @author Albert Beaupre
 */
public abstract class MiniGame extends Tickable implements EventListener {

	private final HashSet<Persona> players; // The set of players in this minigame
	private final ArrayList<Mob> mobs; // The list of mobs in this minigame
	private final boolean[] rules;

	// TODO log terminated minigames for economy reasons
	private boolean terminated; // The flag to check if this minigame was terminated

	/**
	 * The {@code MapBuilder} assigned to this {@code MiniGame}.
	 */
	protected MapBuilder mapBuilder;

	/**
	 * Constructs a new {@code MiniGame} with empty arguments.
	 */
	public MiniGame() {
		this.mobs = new ArrayList<Mob>();
		this.players = new HashSet<Persona>();
		this.rules = new boolean[MiniGameRule.values().length];
		this.setMapBuilder(new MapBuilder());
	}

	/**
	 * This method is executed when this {@code MiniGame} has started.
	 * 
	 * @see #start()
	 */
	protected abstract void begin();

	/**
	 * This method is executed when this {@code MiniGame} has stopped.
	 * 
	 * @see #stop()
	 */
	protected abstract void end();

	/**
	 * This method is executed when the specified {@code player} joins this {@code MiniGame}.
	 * 
	 * @param player
	 *            the player to join this minigame
	 * @return true if the player joined successfully
	 */
	protected abstract boolean join(Persona player);

	/**
	 * This method is executed when the specified {@code player} leaves this {@code MiniGame}.
	 * 
	 * @param player
	 *            the player to lave this minigame
	 * @param force
	 *            the flag to check if the player is being forced to leave
	 * @return true if the player left successfully
	 */
	protected abstract boolean leave(Persona player, boolean force);

	/**
	 * Starts this {@code MiniGame}.
	 */
	public void start() {
		// setup minigame - can be overridden, normal method will heal/restore/teleport
		for (Persona person : players) {
			if (!join(person)) {

			}
		}
		begin();
		queue(1);
	}

	/**
	 * Stops this {@code Minigame}.
	 */
	public void stop() {
		// clean up - normal method will heal/restore/teleport
		for (Persona p : players) {
			if (leave(p, false)) {

			}
		}
		end();
		cancel();
	}

	public void terminate() {
		terminated = true;
		stop();
	}

	/**
	 * Returns {@code true} if the specified {@code persona} is playing this {@code Minigame}.
	 * 
	 * @param persona
	 *            the persona to check
	 * @return true if the persona is playing; return false otherwise
	 */
	public boolean isPlaying(Persona persona) {
		return players.contains(persona);
	}

	/**
	 * Returns {@code true} if this {@code Minigame} is running.
	 * 
	 * @return true if running; return false otherwise
	 * 
	 * @see #isQueued()
	 */
	public boolean isRunning() {
		return super.isQueued();
	}

	/**
	 * <b>Adds</b> the specified {@code persona} to this {@code Minigame}.
	 * 
	 * @param persona
	 *            the player to add
	 * @return true if the player was added; return false otherwise
	 */
	public boolean addPlayer(Persona persona) {
		return players.add(persona);
	}

	/**
	 * <b>Removes</b> the specified {@code persona} from this {@code Minigame}.
	 * 
	 * @param persona
	 *            the player to remove
	 * @return true if the player was removed; return false otherwise
	 */
	public boolean removePlayer(Persona persona) {
		return players.remove(persona);
	}

	/**
	 * Returns the {@code MapBuilder} assigned to this {@code MiniGame}.
	 * 
	 * @return the map builder assigned
	 */
	public MapBuilder getMapBuilder() {
		return mapBuilder;
	}

	/**
	 * Assigns the {@code MapBuilder} of this {@code MiniGame} to the specified {@code mapBuilder}.
	 * 
	 * @param mapBuilder
	 *            the map builder to assign to this minigame
	 */
	public void setMapBuilder(MapBuilder mapBuilder) {
		this.mapBuilder = mapBuilder;
	}

	/**
	 * Returns a {@code Collection} of {@code Persona} in this {@code MiniGame}.
	 * 
	 * @return the players
	 */
	public Collection<Persona> getPlayers() {
		return Collections.unmodifiableCollection(players);
	}

	/**
	 * Returns a {@code Collection} of {@code Mob} in this {@code MiniGame}.
	 * 
	 * @return the mobs
	 */
	public Collection<Mob> getMobs() {
		return Collections.unmodifiableCollection(mobs);
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void setRule(MiniGameRule rule, boolean set) {
		rules[rule.ordinal()] = set;
	}

	public boolean[] getRules() {
		return rules;
	}

}
