package org.maxgamer.rs.model.minigame;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.events.mob.persona.PersonaDeathEvent;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerLeaveWorldEvent;
import org.maxgamer.rs.model.map.MapBuilder;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.structure.Filter;
import org.maxgamer.rs.structure.configs.ConfigSection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * TODO finish this implementation
 *
 * @author Albert Beaupre
 */
public abstract class MiniGame extends Tickable implements EventListener {

    /**
     * This holds a various amount of configurations for this {@code MiniGame}.
     */
    protected final ConfigSection configs;
    private final ConcurrentHashMap<String, Persona> players; // The set of players in this minigame
    private final CopyOnWriteArrayList<Mob> mobs; // The list of mobs in this minigame
    private final boolean[] rules; // An array of rule flags set
    /**
     * The {@code MapBuilder} assigned to this {@code MiniGame}.
     */
    protected MapBuilder mapBuilder;
    private boolean running; // The flag to check if this minigame is running
    private boolean terminated; // The flag to check if this minigame was terminated TODO log
    // terminated minigames for economy reasons
    private long timeStarted; // The time in milliseconds that this minigame started
    private WorldMap associatedMap; // The map associated with this minigame

    /**
     * Constructs a new {@code MiniGame} with empty arguments.
     */
    public MiniGame() {
        this.mobs = new CopyOnWriteArrayList<Mob>();
        this.players = new ConcurrentHashMap<String, Persona>();
        this.configs = new ConfigSection();
        this.rules = new boolean[MiniGameRule.values().length];
        this.setMapBuilder(new MapBuilder());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogout(PlayerLeaveWorldEvent event) {
        leave(event.getMob(), true, MiniGameCause.LOG_OUT);
    }

    @EventHandler()
    public void onDeath(PersonaDeathEvent event) {
        if (ruleSet(MiniGameRule.SAFE_ON_DEATH))
            event.setSafe(true);
    }

    /**
     * This method is executed when this {@code MiniGame} has started.
     *
     * @see #start()
     */
    protected abstract void onStart();

    /**
     * This method is executed when this {@code MiniGame} has stopped.
     *
     * @see #stop()
     */
    protected abstract void onStop(MiniGameCause cause);

    /**
     * This method is executed when the specified {@code player} joins this {@code MiniGame}.
     *
     * @param player    the player to join this minigame
     * @param joinCause the cause type that caused the player to join
     * @return true if the player joined successfully
     */
    protected abstract boolean join(Persona player, MiniGameCause joinCause);

    /**
     * This method is executed when the specified {@code player} leaves this {@code MiniGame}.
     *
     * @param player     the player to lave this minigame
     * @param force      the flag to check if the player is being forced to leave
     * @param leaveCause the cause type that caused the player to leave
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
     * Sends the specified {@code message} to all playing {@code Persona} in this {@code MiniGame}.
     *
     * @param message the message to send
     */
    public void sendGlobalMessage(String message) {
        for (Persona p : players.values())
            p.sendMessage(message);
    }

    /**
     * Starts this {@code MiniGame}.
     */
    public void start() {
        running = true;
        // setup minigame - can be overridden, normal method will heal/restore/teleport
        onStart();
        timeStarted = System.currentTimeMillis();
        for (Persona person : players.values()) {
            if (!join(person, MiniGameCause.MINI_GAME_START)) {

            }
        }
        queue(1);
    }

    /**
     * Stops this {@code MiniGame}.
     */
    public void stop(MiniGameCause cause) {
        running = false;
        // clean up - normal method will heal/restore/teleport
        onStop(cause);
        for (Persona p : players.values()) {
            if (p == null)
                continue;
            if (leave(p, false, MiniGameCause.MINI_GAME_END)) {
                removePlayer(p);
            }
        }
        cancel();
        for (Mob mob : mobs)
            removeMob(mob);
        if (associatedMap != null)
            associatedMap = null;
        configs.clear();
    }

    /**
     * This {@code MiniGame} is first stopped and then terminated.
     */
    public void terminate() {
        stop(MiniGameCause.TERMINATED);
        terminated = true;
    }

    @Override
    public final void tick() {
        if (!running) {
            return;
        }
        for (Iterator<Persona> it = players.values().iterator(); it.hasNext(); ) {
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
        for (Iterator<Mob> it = mobs.iterator(); it.hasNext(); ) {
            Mob mob = it.next();
            if (mob == null || mob.isDestroyed()) {
                removeMob(mob);
                continue;
            }
        }
        tickMiniGame();
        queue(1);
    }

    /**
     * Returns {@code true} if the specified {@code persona} is playing this {@code MiniGame}.
     *
     * @param persona the persona to check
     * @return true if the persona is playing; return false otherwise
     */
    public boolean isPlaying(Persona persona) {
        if (!persona.isLoaded() || persona.isDestroyed())
            return false;
        return players.containsKey(persona.getName());
    }

    /**
     * Returns {@code true} if this {@code MiniGame} is running.
     *
     * @return true if running; return false otherwise
     * @see #isQueued()
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * <b>Adds</b> the specified {@code mobArray} to this {@code MiniGame}.
     *
     * @param mobArray the array of mobs to add
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
     * @param mob the mob to remove
     */
    public void removeMob(Mob mob) {
        mob.hide();
        mob.destroy();
        mobs.remove(mob);
    }

    /**
     * Retrieves a {@code Mob} based on the specified {@code filter}.
     *
     * @param filter the filter to check for a mob
     * @return the mob returned based on the filter
     */
    @SuppressWarnings("unchecked")
    public <M extends Mob> M getMob(Filter<M> filter) {
        for (Mob mob : mobs)
            if (filter.accept((M) mob))
                return (M) mob;
        return null;
    }

    @SuppressWarnings("unchecked")
    public <N extends NPC> N getNPC(int id, Class<N> cast) {
        for (Mob mob : mobs)
            if (mob instanceof NPC && ((NPC) mob).getId() == id)
                return (N) mob;
        return null;
    }

    /**
     * Retrieves a {@code Player} for the specified {@code name}.
     *
     * @param name the name of the player
     * @return the player for the name
     */
    public Persona getPlayer(String name) {
        return players.get(name);
    }

    /**
     * <b>Adds</b> the specified {@code persona} to this {@code MiniGame}.
     *
     * @param persona the player to add
     */
    public void addPlayer(Persona persona) {
        players.put(persona.getName(), persona);
    }

    /**
     * <b>Removes</b> the specified {@code persona} from this {@code MiniGame}.
     *
     * @param persona the player to remove
     */
    public void removePlayer(Persona persona) {
        players.remove(persona.getName());
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
     * @param mapBuilder the map builder to assign to this minigame
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
        return Collections.unmodifiableCollection(players.values());
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
        return unit.convert(System.currentTimeMillis() - timeStarted, TimeUnit.MILLISECONDS);
    }

    public WorldMap getAssociatedMap() {
        if (associatedMap == null)
            return Core.getServer().getMaps().get("mainland");
        return associatedMap;
    }

    public void setAssociatedMap(WorldMap associatedMap) {
        this.associatedMap = associatedMap;
    }

    public ConfigSection getConfigs() {
        return configs;
    }

    /**
     * @author Albert Beaupre
     */
    public enum MiniGameCause {
        LOG_OUT,
        MINI_GAME_WON,
        MINI_GAME_LOST,
        MINI_GAME_END,
        MINI_GAME_START,
        NOT_PLAYING,
        TERMINATED,
        FORFEIT
    }

}
