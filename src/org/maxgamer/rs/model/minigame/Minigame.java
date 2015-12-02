package org.maxgamer.rs.model.minigame;

import java.util.HashSet;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.model.entity.mob.persona.Persona;

/**
 * 
 * @author Albert Beaupre
 * @author netherfoam
 */
public abstract class Minigame extends Tickable implements EventListener {

	private HashSet<Persona> players; // A list of players playing in this minigame

	/**
	 * Constructs a new {@code Minigame} for the specified {@code maxPlayerSize}.
	 * 
	 * @param maxPlayerSize
	 *            the maximum amount of players allowed in this minigame
	 */
	public Minigame(int maxPlayerSize) {
		this.players = new HashSet<>(maxPlayerSize);
	}
	
	protected abstract void begin();
	protected abstract void end();
	protected abstract boolean join(Persona p);
	protected abstract boolean leave(Persona p, boolean force);

	/**
	 * Starts this {@code Minigame}.
	 */
	public final void start() {
		// setup minigame - can be overridden, normal method will heal/restore/teleport
		for (Persona person : players) {
			if (!join(person)) {
				
			}
		}
		begin();
		queue(0);
	}

	/**
	 * Stops this {@code Minigame}.
	 */
	public final void stop() {
		// clean up - normal method will heal/restore/teleport
		for (Persona p : players) {
			if (leave(p, false))
				players.remove(p);
		}
		end();
		cancel();
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

}
