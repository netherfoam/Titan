package org.maxgamer.rs.model.minigame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerLogOutEvent;
import org.maxgamer.rs.model.map.MapBuilder;
import org.maxgamer.rs.model.map.WorldMap;

/**
 * TODO finish this implementation
 * 
 * @author Albert Beaupre
 */
public abstract class MiniGame extends Tickable implements EventListener {

	private final HashSet<Persona> players; // The set of players in this minigame
	private final ArrayList<Mob> mobs; // The list of mobs in this minigame
	private final boolean[] rules; // An array of rule flags set

	// TODO log terminated minigames for economy reasons
	private boolean terminated; // The flag to check if this minigame was terminated
	private long timeRunning;
	private WorldMap associatedMap;

	/**
	 * The {@code MapBuilder} assigned to this {@code MiniGame}.
	 */
	protected MapBuilder mapBuilder;

	/**
	 * @author Albert Beaupre
	 */
	public enum MiniGameCause {
		LOG_OUT,
		MINI_GAME_END,
		MINI_GAME_START,
		NOT_PLAYING,
		FORFEIT
	}

	/**
	 * Constructs a new {@code MiniGame} with empty arguments.
	 */
	public MiniGame() {
		this.mobs = new ArrayList<Mob>();
		this.players = new HashSet<Persona>();
		this.rules = new boolean[MiniGameRule.values().length];
		this.setMapBuilder(new MapBuilder());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLogout(PlayerLogOutEvent event) {
		leave(event.getMob(), true, MiniGameCause.LOG_OUT);
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
	 * @param joinCause
	 *            the cause type that caused the player to join
	 * @return true if the player joined successfully
	 */
	protected abstract boolean join(Persona player, MiniGameCause joinCause);

	/**
	 * This method is executed when the specified {@code player} leaves this {@code MiniGame}.
	 * 
	 * @param player
	 *            the player to lave this minigame
	 * @param force
	 *            the flag to check if the player is being forced to leave
	 * @param leaveCause
	 *            the cause type that caused the player to leave
	 * @return true if the player left successfully
	 */
	protected abstract boolean leave(Persona player, boolean force, MiniGameCause leaveCause);

	/**
	 * This method executes the method {@link #leave(Persona, boolean, MiniGameCause)} with a
	 * default {@code MiniGameCause} of NULL.
	 * 
	 * @see #leave(Persona, boolean, MiniGameCause)
	 */
	protected boolean leave(Persona player, boolean force) {
		return leave(player, force, MiniGameCause.FORFEIT);
	}

	/**
	 * This method is executed every game tick.
	 */
	protected abstract void tickMiniGame();

	/**
	 * Starts this {@code MiniGame}.
	 */
	public void start() {
		// setup minigame - can be overridden, normal method will heal/restore/teleport
		begin();
		timeRunning = System.currentTimeMillis();
		for (Persona person : players) {
			if (!join(person, MiniGameCause.MINI_GAME_START)) {

			}
		}
		queue(1);
	}

	/**
	 * Stops this {@code MiniGame}.
	 */
	public void stop() {
		// clean up - normal method will heal/restore/teleport
		end();
		for (Persona p : players) {
			if (leave(p, false, MiniGameCause.MINI_GAME_END)) {
				removePlayer(p);
			}
		}
		cancel();
		for (Iterator<Mob> it = mobs.iterator(); it.hasNext();) {
			Mob mob = it.next();
			mob.hide();
			mob.destroy();
			it.remove();
		}
	}

	public void terminate() {
		terminated = true;
		stop();
	}

	@Override
	public final void tick() {
		for (Iterator<Persona> it = players.iterator(); it.hasNext();) {
			Persona p = it.next();
			if (p == null) {
				it.remove();
				continue;
			}

			if (!isPlaying(p)) {
				leave(p, true, MiniGameCause.NOT_PLAYING);
				removePlayer(p);
			}
		}
		tickMiniGame();
		queue(1);
	}

	/**
	 * Returns {@code true} if the specified {@code persona} is playing this {@code MiniGame}.
	 * 
	 * @param persona
	 *            the persona to check
	 * @return true if the persona is playing; return false otherwise
	 */
	public boolean isPlaying(Persona persona) {
		if (!persona.isLoaded() || persona.isDestroyed())
			return false;
		return players.contains(persona);
	}

	/**
	 * Returns {@code true} if this {@code MiniGame} is running.
	 * 
	 * @return true if running; return false otherwise
	 * 
	 * @see #isQueued()
	 */
	public boolean isRunning() {
		return super.isQueued();
	}

	/**
	 * <b>Adds</b> the specified {@code mobArray} to this {@code MiniGame}.
	 * 
	 * @param mobArray
	 *            the array of mobs to add
	 */
	public void addMob(Mob... mobArray) {
		for (Mob mob : mobArray) {
			mobs.add(mob);
			mob.respawn();
		}
	}

	/**
	 * <b>Removes</b> the specified {@code mob} from this {@code MiniGame}.
	 * 
	 * @param mob
	 *            the mob to remove
	 */
	public void removeMob(Mob mob) {
		mob.hide();
		mob.destroy();
		mobs.remove(mob);
	}

	/**
	 * <b>Adds</b> the specified {@code persona} to this {@code MiniGame}.
	 * 
	 * @param persona
	 *            the player to add
	 */
	public void addPlayer(Persona persona) {
		players.add(persona);
	}

	/**
	 * <b>Removes</b> the specified {@code persona} from this {@code MiniGame}.
	 * 
	 * @param persona
	 *            the player to remove
	 */
	public void removePlayer(Persona persona) {
		players.remove(persona);
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

	/**
	 * @return true if this minigame was terminated
	 */
	public boolean isTerminated() {
		return terminated;
	}

	public void setRule(MiniGameRule rule, boolean set) {
		rules[rule.ordinal()] = set;
	}

	public boolean ruleSet(MiniGameRule rule) {
		return rules[rule.ordinal()];
	}

	public boolean[] getRules() {
		return rules;
	}

	public long getTimeRunning(TimeUnit unit) {
		return unit.convert(System.currentTimeMillis() - timeRunning, TimeUnit.MILLISECONDS);
	}

	public WorldMap getAssociatedMap() {
		return associatedMap;
	}

	public void setAssociatedMap(WorldMap associatedMap) {
		this.associatedMap = associatedMap;
	}

}
