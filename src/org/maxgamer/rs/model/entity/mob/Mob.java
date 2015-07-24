package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.IllegalThreadException;
import org.maxgamer.rs.events.mob.MobLoadEvent;
import org.maxgamer.rs.events.mob.MobUnloadEvent;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.action.ActionQueue;
import org.maxgamer.rs.model.action.DeathAction;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.combat.Attack;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.entity.mob.combat.Combat;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.item.inventory.Equipment;
import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;
import org.maxgamer.rs.model.skill.SkillSet;

/**
 * Represents a Mob which has health, equipment, an action queue, an UpdateMask,
 * can run and fight.
 * @author netherfoam
 */
public abstract class Mob extends Entity implements EquipmentHolder {
	/**
	 * The update mask used for this mob, contains animations/movement
	 * updates/etc
	 */
	protected UpdateMask mask;
	
	/** The queue of actions for this mob, eg walking, combat, identifying herbs */
	protected ActionQueue actionQueue;
	
	/** The damage dealt to this mob so far */
	protected Combat combat;
	
	/** The combat stats for this mob */
	protected CombatStats combatStats;
	
	/** The items which this mob currently has equipped. */
	protected Equipment equipment;
	
	/**
	 * The mob's current health, this may exceed maxHealth under special
	 * circumstances. Health slowly normalizes towards maxHealth
	 */
	protected int health = 100;
	
	/**
	 * True if the mob is hidden from players view, false if it is visible.
	 */
	private boolean hidden = true;
	
	/**
	 * True if this mob is auto-retaliating.
	 */
	private boolean retaliate = true;
	
	private int unrootTick;
	private int rootImmunityTick;
	private boolean isLoaded;
	private Faction faction = Factions.NONE;
	private Facing facing;
	
	/**
	 * Constructs a new mob
	 * @param sizeX the size of this mob along the X (East-West) axis
	 * @param sizeY the size of this mob along the Y (North-South) axis
	 */
	public Mob(int sizeX, int sizeY) {
		super(sizeX, sizeY);
		this.actionQueue = new ActionQueue(this);
		this.combat = new Combat(this);
	}
	
	/**
	 * The faced location / target for this mob. This is not equal to the update
	 * mask facing, which only contains changes, whereas this contains the
	 * current state.
	 * @return the faced location / target for this mob.
	 */
	public Facing getFacing() {
		return this.facing;
	}
	
	public void setFacing(Facing fac) {
		this.facing = fac;
		this.getUpdateMask().setFacing(true);
	}
	
	public void setFaction(Faction faction) {
		if (faction == null) {
			faction = Factions.NONE;
		}
		
		this.faction = faction;
	}
	
	public final Faction getFaction() {
		return this.faction;
	}
	
	/**
	 * "Force text" appears above the mob temporarily, as if the Mob had said
	 * something.
	 * @param text the text to create
	 * @thread any
	 */
	public void say(String text) {
		getUpdateMask().setSay(text);
	}
	
	/**
	 * Roots this mob in place for the given duration. A rooted mob may not
	 * move.
	 * @param duration the number of ticks to root them
	 * @param immunity the duration of the "root immunity" granted after the
	 *        effect wears off, in ticks
	 * @param force flag to ignore immunity. If set to true, this will ignore
	 *        the immunity set by any other calls
	 * @return the number of ticks the mob is rooted for. This may be 0
	 *         (immune), greater than duration (previously rooted for longer) or
	 *         duration.
	 * @thread main
	 */
	public int root(int duration, int immunity, boolean force) {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		int tick = Core.getServer().getTicker().getTicks();
		
		int newUnrootTick = tick + duration;
		
		if (force == false && rootImmunityTick > tick) {
			//We are currently immune to rooting effects.
			return 0;
		}
		
		if (newUnrootTick < unrootTick) {
			//We are already rooted for longer than requested.
			return unrootTick - tick;
		}
		
		this.unrootTick = newUnrootTick;
		this.rootImmunityTick = tick + duration + immunity;
		
		return duration;
	}
	
	public abstract AttackStyle getAttackStyle();
	
	@Override
	protected void setLocation(Location loc) {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		boolean mapLoaded;
		if (loc != null) {
			mapLoaded = (loc.getMap().getClip(loc.x, loc.y, loc.z) & ClipMasks.UNLOADED_TILE) == 0;
		}
		else {
			//Null location
			mapLoaded = false;
		}
		
		super.setLocation(loc);
		
		//For some reason, the player seems to be unloading here. This means mapLoaded = false
		
		//If the map is unloaded and we are loaded, we must unload
		if (isLoaded() && mapLoaded == false) {
			this.unload();
		}
		
		//If the map is loaded, and we are not, we must load
		if (isLoaded() == false && mapLoaded) {
			this.load();
		}
	}
	
	/**
	 * Returns true if this mob is Loaded. A mob is only considered loaded if it
	 * has had load() called on it. A mob may be unloaded by calling unload().
	 * As a rule, a mob never starts as loaded. A mob may be destroyed without
	 * being ever loaded. A mob will be unloaded if it is destroyed. If the
	 * section of world the mob is in is loaded, the mob is guaranteed to be
	 * loaded. If the map is unloaded, then the mob may be still loaded. A mob
	 * may never have load() called if it is loaded, or unload() called if it is
	 * unloaded. When a mob is destroyed, if it is loaded it will be unloaded.
	 * By implication, setting the location of a mob to a location that is not
	 * loaded from one that is, will unload the mob. Visa versa, moving a mob
	 * from an unloaded location to a loaded one will load the mob.
	 * @return true if this mob is loaded.
	 */
	public final boolean isLoaded() {
		return isLoaded;
	}
	
	/**
	 * @thread main
	 */
	public final void load() {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		if (isLoaded) throw new IllegalStateException("Mob is already loaded!");
		isLoaded = true;
		
		try {
			this.onLoad();
		}
		catch (Exception e) {
			Log.warning("Exception calling Mob.onLoad() for " + this);
			e.printStackTrace();
		}
		
		MobLoadEvent ev = new MobLoadEvent(this);
		ev.call();
	}
	
	/**
	 * @thread main
	 */
	public final void unload() {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		if (isLoaded == false) throw new IllegalStateException("Mob is already loaded!");
		isLoaded = false;
		
		MobUnloadEvent ev = new MobUnloadEvent(this);
		ev.call();
		
		try {
			this.onUnload();
		}
		catch (Exception e) {
			Log.warning("Exception calling Mob.onUnload() for " + this);
			e.printStackTrace();
		}
	}
	
	protected abstract void onLoad();
	
	protected abstract void onUnload();
	
	public abstract void onDeath();
	
	/**
	 * Forcibly roots the mob until unroot() is called.
	 */
	public void root() {
		root(Integer.MAX_VALUE - Core.getServer().getTicker().getTicks(), 0, true);
	}
	
	/**
	 * Cancels any root() effect. This does not modify the unroot immunity
	 */
	public void unroot() {
		unrootTick = 0;
	}
	
	/**
	 * Returns true if this mob may not move. False if it may move.
	 * @return true if this mob may not move.
	 */
	public boolean isRooted() {
		return Core.getServer().getTicker().getTicks() < unrootTick;
	}
	
	/**
	 * The index in the client. This is different for players and NPCs.
	 * (Specifically, Players client index is (playerId + 1) | 0x8000. NPC's
	 * client index is just npcId + 1
	 * @return the index.
	 */
	public abstract int getClientIndex();
	
	/**
	 * Returns the next attack to be used by this mob in combat. The attack will
	 * be attempted, though may fail. If it fails, the mob may continue
	 * attacking in which case, another call will be made to nextAttack().
	 * @return the next attack for this mob to perform.
	 */
	public abstract Attack nextAttack();
	
	/**
	 * Returns the SkillSet for this Mob. Not all SkillTypes are available to
	 * NPC's, unlike players.
	 * @return the skills for this mob.
	 */
	public abstract SkillSet getSkills();
	
	/**
	 * The damage meter for this mob
	 * @return the damage meter not null
	 */
	public Combat getCombat() {
		return combat;
	}
	
	/**
	 * Returns the CombatStats which control how powerful this mob is in combat.
	 * @return the combat stats representing this mob's combat capability
	 */
	public CombatStats getCombatStats() {
		return this.combatStats;
	}
	
	/**
	 * Walks this mob to the given position via the given pathfinder. If there
	 * was no path found, any results from the pathfinder will be used thus it
	 * is the responsibility of the pathfinder as to whether a failure to find a
	 * path will result in a second-best path being used.
	 * @param to the position to move to
	 * @param finder the pathfinder to use
	 * @return true if a path was found (It may still be interrupted!) false if
	 *         one was not.
	 * @thread main
	 */
	public boolean move(Position to, PathFinder finder) {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		Path path = finder.findPath(getLocation(), to, to, 1, 1);
		
		Action walk = new WalkAction(this, path);
		getActions().clear();
		getActions().queue(walk);
		
		return !path.hasFailed();
	}
	
	/**
	 * Walk the steps in the given path, as many as possible.
	 * @param path the path to walk
	 * @return true if the path was completed, cancelled or unusable, false if
	 *         it can still be used next tick
	 */
	public abstract boolean move(Path path);
	
	/**
	 * The UpdateMask used for this mob. This may be extended if necessary.
	 * @return the update mask for this mob
	 */
	public UpdateMask getUpdateMask() {
		return mask;
	}
	
	/**
	 * Animates the mob with the given animation and priority. This is a
	 * shorthand method for accessing the update mask.
	 * @param emo the animation id
	 * @param priority the priority of the animation. Higher priority animations
	 *        will override lower ones.
	 */
	public void animate(int emo, int priority) {
		animate(new Animation(emo), priority);
	}
	
	public void animate(Animation anim, int priority){
		getUpdateMask().setAnimation(anim, priority);
	}
	
	/**
	 * Overlays graphics on the mob. This is a shorthand method for accessing
	 * the update mask.
	 * @param gfx the id of the graphics to use
	 */
	public void graphics(int gfx) {
		getUpdateMask().setGraphics(new Graphics(gfx));
	}
	
	/**
	 * The queue of actions for this Mob. For example, this queue may contain
	 * actions like walking, filling a vial with water, identifying herbs,
	 * attacking a mob.
	 * @return the action queue for this mob
	 */
	public ActionQueue getActions() {
		return actionQueue;
	}
	
	/**
	 * Fetches the unique ID for this mob. When sending the ID's to the client,
	 * +1 will be added to the result of this call because the client represents
	 * ID=0 with NULL mobs.
	 * @return the unique ID for this mob, 0-MAX_MOBS - 1
	 */
	public abstract short getSpawnIndex();
	
	/**
	 * The Model used for this mob. This is essentially an UpdateMask.
	 * @return the Model used for this mob
	 */
	public abstract MobModel getModel();
	
	/**
	 * Teleports this mob to the given location
	 * @param dest the location to teleport to
	 * @thread main
	 */
	public abstract void teleport(Location dest);
	
	/**
	 * The current health of this mob
	 * @return The current health of this mob
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Sets the health of this mob to the given amount. This method may exceed
	 * the maximum health of the mob (See Mob.getMaxHealth()), but if the given
	 * value is less than 0, health will be set to 0. Otherwise health will be
	 * set to the given value. This alters the return value of isDead() if
	 * health is at 0.
	 * @param hp the new health for the mob.
	 * @thread main
	 */
	public void setHealth(int hp) {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		if (hp < 0) hp = 0;
		
		if (this.isDead() == false && hp <= 0) {
			if (this.getFacing() != null) {
				this.setFacing(null);
			}
			//Mob has died
			this.getActions().clear(); //Remove all cancellable
			for (Action a : this.getActions().getList()) {
				this.getActions().cancel(a); //Force cancel anything left
			}
			
			this.getActions().queue(new DeathAction(this));
		}
		
		this.health = hp;
	}
	
	/**
	 * The maximum health of this mob. The current health does not necessarily
	 * have to be less than or equal to this. Health normalizes towards this
	 * value over time.
	 * @return the maximum health of this mob
	 */
	public abstract int getMaxHealth();
	
	/**
	 * The equipment that this mob is currently wielding, such as Rune Scimitar,
	 * Dharok's Platebody or Amulet of Glory
	 * @return the equipment this mob is wielding
	 */
	public Equipment getEquipment() {
		return this.equipment;
	}
	
	/**
	 * Returns true if this mob's health is set to 0.
	 * @return true if this mob's health is <= 0. False if it is > 0.
	 */
	public boolean isDead() {
		return health <= 0;
	}
	
	/**
	 * Determines if this mob is visible to the given mob. This method should be overridden
	 * if the mob can become invisible.
	 * @param to
	 * @return
	 */
	public boolean isVisible(Mob to){
		if(isDestroyed()){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Restores this mob to full health and removes any other imperfections,
	 * such as drained stats.
	 * @thread main
	 */
	public void restore() {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		setHealth(getMaxHealth());
	}
	
	/**
	 * Returns true if the mob is hidden from players view, false if it is
	 * visible.
	 * @return true if the mob is hidden from players view, false if it is
	 *         visible.
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * Hides this mob from players so that it isn't visible. The result of
	 * isHidden() will be true after this call. If the mob is currently hidden,
	 * this has no effect. The effect will occur on the next tick.
	 * @thread any
	 */
	public void hide() {
		this.hidden = true;
	}
	
	/**
	 * Shows this mob to players so that it becomes visible. The result of
	 * isHidden() will be false after this call. If the mob is currently
	 * visible, this has no effect. The effect will occur on the next tick.
	 * @thread any
	 */
	public void show() {
		this.hidden = false;
	}
	
	/**
	 * Respawns this mob. This resets the stats and health to the standard
	 * amount, and calls show() if the mob is currently hidden.
	 * @thread main
	 */
	public void respawn() {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		getCombat().reset();
		restore();
		if (isHidden()) show();
	}
	
	/**
	 * The number of ticks before this mob respawns after death.
	 * @return The number of ticks before this mob respawns after death.
	 * @thread any
	 */
	public abstract int getRespawnTicks();
	
	/**
	 * Destroys this mob by calling the super Entity.destroy(), and if this mob
	 * is not already hidden, calls hide() on this mob.
	 * @thread primary
	 */
	@Override
	public void destroy() {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Must be invoked in main thread");
		}
		
		if (isDestroyed()) {
			throw new IllegalStateException("Mob already destroyed");
		}
		if (this.isLoaded()) this.unload();
		super.destroy();
		if (isHidden() == false) hide();
	}
	
	/**
	 * Sets whether this mob will retaliate to any attacks made against it.
	 * Setting this to false will mean this mob will not retaliate. If a mob is
	 * busy performing some other action, it generally won't cancel the other
	 * action.
	 * @param retal true if this mob should retaliate when hit.
	 */
	public void setRetaliate(boolean retal) {
		this.retaliate = retal;
	}
	
	/**
	 * Returns whether this mob will currently retaliate to any incoming
	 * attacks.
	 * @return true if retaliating to attacks, false if it ignores the attacker.
	 */
	public boolean isRetaliate() {
		return this.retaliate;
	}
	
	/**
	 * Returns true if the given mob may attack this mob.
	 * @param src the mob who is attacking
	 * @return true if this mob may be attacked, false otherwise.
	 */
	public abstract boolean isAttackable(Mob src);
	
	/**
	 * Fetches the name of this mob. This should be the exact name when right
	 * clicking on the mob.
	 * @return
	 */
	public abstract String getName();
}