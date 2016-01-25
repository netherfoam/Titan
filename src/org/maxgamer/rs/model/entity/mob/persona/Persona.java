package org.maxgamer.rs.model.entity.mob.persona;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.cache.EncryptedException;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.lib.Chat;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.Bonus;
import org.maxgamer.rs.model.entity.mob.Factions;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MobModel;
import org.maxgamer.rs.model.entity.mob.UpdateMask;
import org.maxgamer.rs.model.entity.mob.combat.Attack;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.entity.mob.combat.DamageType;
import org.maxgamer.rs.model.entity.mob.combat.MeleeAttack;
import org.maxgamer.rs.model.entity.mob.combat.RangeAttack;
import org.maxgamer.rs.model.entity.mob.combat.mage.CombatSpell;
import org.maxgamer.rs.model.entity.mob.combat.mage.MagicAttack;
import org.maxgamer.rs.model.entity.mob.combat.mage.Spellbook;
import org.maxgamer.rs.model.entity.mob.persona.player.FriendsList;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.events.mob.MobMoveEvent;
import org.maxgamer.rs.model.events.mob.MobPreTeleportEvent;
import org.maxgamer.rs.model.events.mob.persona.PersonaChatEvent;
import org.maxgamer.rs.model.events.mob.persona.PersonaDeathEvent;
import org.maxgamer.rs.model.events.mob.persona.PersonaStartEvent;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.ground.GroundItemStack;
import org.maxgamer.rs.model.item.inventory.BankContainer;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.GenericContainer;
import org.maxgamer.rs.model.item.inventory.Inventory;
import org.maxgamer.rs.model.item.inventory.PersonaEquipment;
import org.maxgamer.rs.model.item.inventory.StackType;
import org.maxgamer.rs.model.mail.LostAndFound;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.skill.SkillSet;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.model.skill.prayer.PrayerSet;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * Represents a dummy player which does not do anything. This dummy player has
 * an account, stored the same way player accounts are. It takes up a spot in
 * the player list, eg max is 2000 or so. It has a set of skills,
 * charactermodel, config, username, password, etc. It simply does not do
 * anything by itself, and can be extended to create players which are not
 * actually players, but bots or characters which are as highly customizable as
 * players themselves.
 * 
 * @author netherfoam
 */
public class Persona extends Mob implements YMLSerializable, InventoryHolder {
	/**
	 * The default location that players will spawn at. This is configurable in
	 * the world config.
	 */
	public static final Location DEFAULT_PLAYER_SPAWN = Location.deserialize(
			Core.getServer().getConfig().getSection("spawn"), new Location(
					3221, 3220, 0));

	/**
	 * The unique ID for this Persona. This varies from 0 to 2046. When sending
	 * the ID to clients, +1 should be added, as a 0 ID represents a null
	 * Persona on the client.
	 */
	protected short id;

	/**
	 * The name of this persona
	 */
	protected String name;

	/**
	 * true if this mob is running, false if it is not. A running entity can
	 * move 2 squares, a walking can move 1, per tick
	 */
	protected boolean run = false;

	/**
	 * The list of skills this persona has and their experiences/modifiers
	 */
	protected SkillSet skills;

	/**
	 * The persona's inventory state
	 */
	protected Inventory inventory;

	/**
	 * The persona's bank items
	 */
	protected BankContainer bank;

	/**
	 * The persona's items which have been lost and should be returned whenever
	 * possible
	 */
	protected LostAndFound lost;

	/**
	 * The model which is used by this persona, to store graphical model details
	 * such as hair colour, gender and clothing.
	 */
	protected MobModel model;

	/**
	 * The list of friends and ignores that this player has.
	 */
	protected FriendsList friends;

	/**
	 * The right-click options available on all surrounding players to this
	 * Persona.
	 */
	protected PersonaOptions personaOptions;

	/**
	 * A map of <Key, Serializable> which are attached to this persona. These
	 * are written on save() and are loaded in the load() call.
	 */
	protected HashMap<String, YMLSerializable> attachments = new HashMap<String, YMLSerializable>();
	/**
	 * The existing config file, possibly empty if the player is new.
	 */
	protected ConfigSection config = new ConfigSection();

	/**
	 * The attack style that this persona uses to attack.
	 */
	protected AttackStyle attackStyle;

	/**
	 * The spellbook this Persona is currently using, modern by default
	 */
	private Spellbook spellbook;

	/**
	 * Object containing active prayers and handles toggling them & drainrate
	 */
	private PrayerSet prayer;

	/**
	 * A currently queued attack, such as a Special Attack or a Magic Spell.
	 * This can be overwritten if another attack is queued, or cancelled
	 * entirely with no replacement.
	 */
	private Attack nextAttack;

	/**
	 * The spell the player is currently autocasting in combat. If this is null,
	 * then no spell is being autocast.
	 */
	private CombatSpell autocast;

	/**
	 * The run energy for this Persona
	 */
	private int runEnergy = 100;

	/**
	 * Constructs a new Persona from the given profile. This will modify the
	 * profile.
	 * 
	 * @param profile
	 *            the profile to use
	 * @throws WorldFullException
	 * @throws NullPointerException
	 *             if the given profile is null
	 */
	public Persona(String name) throws WorldFullException {
		super(1, 1);

		if (name == null) {
			throw new NullPointerException("Persona name may not be null.");
		}

		this.name = name;
		this.setFaction(Factions.PLAYER);
		this.personaOptions = new PersonaOptions(this);
		this.model = new PersonaModel(this);
		this.skills = new SkillSet(this);
		this.combatStats = new PersonaCombatStats(this);
		this.mask = new UpdateMask(this, new PersonaMovementUpdate());

		this.inventory = new Inventory();
		this.equipment = new PersonaEquipment(this);
		this.bank = new BankContainer();
		this.lost = new LostAndFound(this);
		this.prayer = new PrayerSet(this);
		this.attackStyle = AttackStyle.getStyle(-1, 1); // Punch

		this.equipment.addListener(new ContainerListener() {

			@Override
			public void onSet(Container c, int slot, ItemStack old) {
				if (c == equipment && slot == WieldType.WEAPON.getSlot()) {
					ItemStack item = c.get(slot);

					if (old != null && item != null) {
						if (old.getId() == item.getId()) {
							return; // We're still wielding the same item, the
									// attack is fine.
						}
					}

					// Our weapon has changed.
					AttackStyle style;
					if (item == null) {
						style = AttackStyle.getStyle(-1, getAttackStyle()
								.getSlot());
					} else {
						style = item.getDefinition().getAttackStyle(
								getAttackStyle().getSlot());
					}

					setAttackStyle(style);

					// Cancel any existing attack
					getActions().clear();
				}
			}
		});
		this.loadId();
	}

	@Override
	public void onDeath() {
		PersonaDeathEvent e = new PersonaDeathEvent(this);
		e.call();
		
		if (!e.isSafe()) {
			Container all = new GenericContainer(getInventory().getSize() + getEquipment().getSize(), StackType.ALWAYS);
			all.addAll(getInventory());
			all.addAll(getEquipment());
			all.sort(new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					return o2.getDefinition().getValue() - o1.getDefinition().getValue();
				}
			});
			all.shift();
			int keep = e.getKeepSize();
			Container keepContainer = new GenericContainer(keep, StackType.NEVER);
			int index = 0;
			while (keep > 0 && !all.isEmpty()) {
				ItemStack item = all.get(index);
				if (item == null)
					continue;
				all.remove(item.setAmount(1));
				if (all.get(index) == null || all.get(index).getAmount() == 0)
					index++;
				keep--;
				keepContainer.add(item.setAmount(1).setHealth(keep));
			}
			getInventory().clear();
			getEquipment().clear();
			getInventory().addAll(keepContainer);
			
			Persona owner = null;
			Mob[] killers = getDamage().getKillers(DamageType.values());
			for (int i = 0; i < killers.length; i++) {
				if (killers[i] instanceof Persona) {
					owner = (Persona) killers[i];
					break;
				}
			}
			if (owner == null) {
				owner = this; // NPC kill
			}
			
			for (ItemStack item : all) {
				while (item != null) { // item is set to NULL when amount = 0.
					long drop = Math.min(item.getAmount(), item.getStackSize());
					GroundItemStack g = new GroundItemStack(item.setAmount(drop), owner, 50, 250);
					g.setLocation(getCenter());
					item = item.setAmount(item.getAmount() - drop); // Will
																	// eventually
																	// set item
																	// to NULL
				}
			}
			
			GroundItemStack bones = new GroundItemStack(ItemStack.create(526), owner, 50, 250);
			bones.setLocation(getCenter());
		}
	}

	/**
	 * Sends the given message as if this player had said it in public. This is
	 * different to say(), as this records a message in the surrounding players
	 * chatbox and logs it to console. This calls a cancellable PersonaChatEvent
	 * which may modify the text. This method will also auto caps and auto
	 * grammar the given String. Note this invokes chat(s, 0).
	 * 
	 * @param s
	 *            the message to send
	 */
	public void chat(String s) {
		chat(s, 0);
	}

	/**
	 * Sends the given message as if this player had said it in public. This is
	 * different to say(), as this records a message in the surrounding players
	 * chatbox and logs it to console. This calls a cancellable PersonaChatEvent
	 * which may modify the text. This method will also auto caps and auto
	 * grammar the given String.
	 * 
	 * @param s
	 *            the message to send
	 * @param effects
	 *            the effects (Like Red or Flash)
	 */
	public void chat(String s, int effects) {
		// Throw the event call
		PersonaChatEvent e = new PersonaChatEvent(this, s);
		e.call();
		if (e.isCancelled()) {
			return;
		}
		s = e.getMessage();

		// Tidy our chat room up a bit.
		s = Chat.capsBlock(s);
		s = Chat.grammar(s);

		if (s.length() > 255) {
			// Could happen if we set the message badly in the event,
			// or if the grammar/capsBlock calls run foul.
			s = s.substring(0, 255);
		}

		for (Player target : this.getLocation().getNearby(Player.class, 20)) {
			target.getProtocol().sendLocalMessage(this, s, effects);
		}
		Log.info(this.getName() + " @" + this.getLocation() + ": " + s);
	}

	/**
	 * Sets the autocast spell for this Persona. This spell will be used when
	 * generating attacks.
	 * 
	 * @param spell
	 *            the spell, this may be null
	 */
	public void setAutocast(CombatSpell spell) {
		this.autocast = spell;
		if (spell != null) {
			this.setAttackStyle(new AttackStyle(-1, "Magic", Bonus.ATK_MAGIC,
					SkillType.MAGIC));
		} else {
			ItemStack wep = getEquipment().get(WieldType.WEAPON);
			if (wep == null || wep.getWeapon() == null) {
				this.setAttackStyle(AttackStyle.getStyle(-1, 1));
			} else {
				this.setAttackStyle(wep.getDefinition().getAttackStyle(1));
			}
		}
	}

	/**
	 * Sets the Model used by this Persona to the given Model. This also forces
	 * the given model to be updated (model.setChanged()).
	 * 
	 * @param model
	 *            the model
	 */
	public void setModel(MobModel model) {
		this.model = model;
		this.model.setChanged(true);
	}

	/**
	 * The spell this player is currently autocasting. This spell may be null,
	 * if they are not casting spells.
	 * 
	 * @return the spell to cast
	 */
	public CombatSpell getAutocast() {
		return this.autocast;
	}

	public void setRunEnergy(int energy) {
		if (energy > 100 || energy < 0)
			throw new IllegalArgumentException("Energy must be <= 100 and >= 0");
		if (energy <= 0) {
			this.setRunning(false);
		}
		this.runEnergy = energy;
	}

	public int getRunEnergy() {
		return this.runEnergy;
	}

	/**
	 * Sets the current spellbook for this persona. For a player, this will also
	 * update the user's magic interface.
	 * 
	 * @param book
	 *            the new spellbook
	 */
	public void setSpellbook(Spellbook book) {
		if (book == null) {
			throw new NullPointerException("Spellbook may not be null");
		}

		this.spellbook = book;
	}

	/**
	 * The spells that this persona may currently access.
	 * 
	 * @return The spells that this persona may currently access.
	 */
	public Spellbook getSpellbook() {
		return spellbook;
	}

	/**
	 * Object containing active prayers and handles toggling them & drainrate
	 * 
	 * @return the Persona's prayer information
	 */
	public PrayerSet getPrayer() {
		return prayer;
	}

	/**
	 * The LostAndFound items for this Persona. This is used for returning any
	 * items which the game has somehow not had space to give to the player, but
	 * should not be discarded. Lost items are automatically deposited into the
	 * player's bank
	 * 
	 * @return The LostAndFound items for this Persona
	 */
	public LostAndFound getLostAndFound() {
		return lost;
	}

	/**
	 * True if this mob is running, false if not. A mob that is running may move
	 * 2 tiles per tick, a mob that is walking may move 1 tile per tick.
	 * 
	 * @return true if the mob is running
	 */
	public boolean isRunning() {
		return run;
	}

	/**
	 * The style of attack that this Persona is currently using. This defaults
	 * to punch
	 * 
	 * @return the attack style, not null.
	 */
	public AttackStyle getAttackStyle() {
		return attackStyle;
	}

	/**
	 * Sets the attack style for this Persona to use when making attacks.
	 * 
	 * @param style
	 *            the new style, not null.
	 */
	public void setAttackStyle(AttackStyle style) {
		if (style == null) {
			throw new NullPointerException("AttackStyle may not be null");
		}
		this.attackStyle = style;
	}

	public boolean move(Path path) {
		PersonaMovementUpdate m = (PersonaMovementUpdate) getUpdateMask()
				.getMovement();
		if (m.hasTeleported()) {
			// Can't move while teleporting
			return false;
		}

		if (m.hasChanged()) {
			// This should not be triggered, but it is. This is the only section
			// of code
			// where we modify MovementUpdate direction. This means somehow,
			// this run()
			// is being called twice, possibly by two WalkAction?
			// It seems to occur when interacting with a gameobject that is
			// immediately
			// next to the player, and then interacting with one that is further
			// away.
			// Eg queuing an empty path, and then queuing a non-empty path.

			/*
			 * Wait no, it occurs when the player has to move 1 tile to target
			 * then move n tiles to second target in the same tick
			 */
			throw new IllegalStateException(
					"Movement update mask has already changed dir "
							+ m.getDirection() + ", tele " + m.hasTeleported()
							+ ", ActionQueue: " + getActions().toString());
		}

		if (path != null && path.isEmpty() == false) {
			Direction next = path.next();

			if (next.conflict(getLocation()) != 0) {
				return false; // Our path has become blocked or was invalid (Eg
								// door closes)
			}

			int dx = next.dx;
			int dy = next.dy;

			Location dest;
			MobMoveEvent e;
			// TODO: Check the next point in the path is still valid, as the
			// clip in the
			// game world may change (Eg door opens or, more importantly,
			// closes!)
			if (this.isRunning() && path.isEmpty() == false) { // TODO: If NPC's
																// ever run,
																// this check
																// will need to
																// be removed
				Direction dir2 = path.peek(); // Not remove()!

				dx += dir2.dx;
				dy += dir2.dy;

				if (Calc.isBetween(dx, -1, 1) && Calc.isBetween(dy, -1, 1)) {
					// They gave us two simple directions which are not the
					// same.
					// Eg, they gave us NORTH + EAST, which would otherwise
					// resolve
					// to NORTH_EAST.
					// This does not trigger if they gave us NORTH + NORTH,
					// or gave us NORTH_EAST + EAST
					// So we must walk it.
					dx -= dir2.dx;
					dy -= dir2.dy;

					m.setWalk(next);
				} else {
					if (dir2.conflict(getLocation().add(next.dx, next.dy)) != 0) {
						return true; // Our path has become blocked or was
										// invalid (Eg door closes)
					}
					path.next(); // We are using this step. (Remove dir2)
					m.setRun(next, dir2);
				}

				dest = this.getLocation().add(dx, dy);
				e = new MobMoveEvent(this, dest, true);
			} else {
				m.setWalk(next);
				dest = this.getLocation().add(dx, dy);
				e = new MobMoveEvent(this, dest, false);
			}

			e.call();
			if (e.isCancelled()) {
				m.reset();
				return true; // Our path has been cancelled by an event.
			} else {
				try {
					this.setLocation(dest);
				} catch (RuntimeException ex) {
					if (ex.getCause() instanceof EncryptedException) {
						// So if we failed to set the new location due to map
						// encryption, we shouldn't inform the player that they
						// moved.
						m.reset();
					}
				}
			}

			if (path.isEmpty())
				return true; // We are done walking
			else
				return false; // We have area left to traverse.
		}

		return true; // Finished
	}

	/**
	 * Sets a mob to running. A mob that is running may move 2 tiles per tick, a
	 * mob that is walking may move 1 tile per tick.
	 * 
	 * @param run
	 *            true if this Mob should start running
	 */
	public void setRunning(boolean run) {
		this.run = run;
	}

	/**
	 * The inventory for this Persona, not null. This is for managing the
	 * Persona's items in their backpack.
	 * 
	 * @return The inventory for this Persona
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * The container for this Persona that represents their bank. This is never
	 * null. It is for managing the items in the persona's bank
	 * 
	 * @return the Persona's bank
	 */
	public BankContainer getBank() {
		return bank;
	}

	/**
	 * This player's configuration file. This contains everything except name,
	 * location, password, last IP, and last seen
	 * 
	 * @return the player's config file
	 */
	public ConfigSection getConfig() {
		return this.config;
	}

	/**
	 * The PersonaOptions for this Persona, not null. This is for managing the
	 * Right-Click options available to the Persona when Right-Clicking another
	 * player.
	 * 
	 * @return the PersonaOptions available when right clicking a player.
	 */
	public PersonaOptions getPersonaOptions() {
		return personaOptions;
	}

	/**
	 * This registers the given serializable object with this Persona. If the
	 * config has already been loaded, then the deserialize() method is called
	 * on the given object, using the ConfigSection available in this Persona's
	 * config at the given key. If the config has not yet been loaded (This is
	 * done in the load(File f) call), then the serializable object is still
	 * registered under the given key, and then when the config is loaded, the
	 * deserialize() method is called with the appropriate ConfigSection as an
	 * argument.
	 * 
	 * Calling this method also means that when this Persona has serialize()
	 * called, it will call the serialize() method on the given YML object and
	 * set the ConfigSection at the given key to the result. The result of this
	 * serialization is then written to disk, allowing persistant data to be
	 * stored across Personas.
	 * 
	 * @param key
	 *            the key for the config section.
	 * @param yml
	 *            the object to deserialize/serialize.
	 * @return true if the object was deserialized, false if the server is still
	 *         waiting for the player to load.
	 */
	public boolean register(String key, YMLSerializable yml) {
		if (key == null) {
			throw new NullPointerException("Key may not be null.");
		}
		if (yml == null) {
			throw new NullPointerException(
					"YMLSerializable object may not be null.");
		}

		this.attachments.put(key, yml);

		if (this.config != null) {
			yml.deserialize(this.config.getSection(key));
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <Y extends YMLSerializable> Y getAttachment(String key, Class<Y> cast) {
		return (Y) attachments.get(key);
	}

	/**
	 * Called after the Persona has been sent initial packets.
	 */
	@Override
	protected void onLoad() {
		PersonaStartEvent e = new PersonaStartEvent(this);
		e.call();

		// Submit our health regen tick
		// Core.getServer().getTicker().submit(6 - Core.getServer().getTicks() %
		// 6, new Tickable() {
		new Tickable() {
			@Override
			public void tick() {
				if (isDestroyed()) {
					return; // Cancelled
				}

				int healthTick = (int) (Core.getServer().getTicks() % 6);
				if (!isDead() && healthTick == 0) {
					// Regeneration tick
					if (getHealth() > getMaxHealth()) {
						// Normalize health over time
						setHealth(getHealth() - 1);
					} else if (getHealth() < getMaxHealth()) {
						// Regenerate health over time
						setHealth(getHealth() + 1);
					}
				}
				// Core.getServer().getTicker().submit(6 - healthTick, this);
				this.queue(6 - healthTick);
			}
		}.queue(6 - Core.getServer().getTicks() % 6);

		this.setHealth(this.config.getInt("health", getMaxHealth()));
		this.setRetaliate(this.config.getBoolean("retaliate",
				this.isRetaliate()));
		this.setSpellbook(Spellbook.getBook(this.config
				.getInt("spellbook", 192)));
		this.setRunEnergy(this.config.getInt("energy.run", this.runEnergy));
		if (this.getRunEnergy() > 0) {
			this.setRunning(true);
		}

		// Default to lvl 10
		this.skills.setLevel(SkillType.CONSTITUTION, 10);

		register("skills", this.skills);
		register("model", this.model);
		register("inventory", this.inventory);
		register("equipment", this.equipment);
		register("bank", this.bank);
		register("lost", this.lost);

		show();
	}

	@Override
	public void setHealth(int health) {
		super.setHealth(health);
	}

	/**
	 * Fetches the Persona's SkillSet. This also contains any temporary
	 * modifications to skills, like prayer or potion modifications.
	 * 
	 * @return the Persona's SkillSet
	 */
	@Override
	public SkillSet getSkills() {
		return this.skills;
	}

	@Override
	public MobModel getModel() {
		return this.model;
	}

	@Override
	protected void setLocation(Location l) {
		if (l != null) {
			try {
				l.getMap().load(l.x, l.y);
			} catch (IOException e) {
				// Okay, so we couldn't load the map correctly.
				// There is no nice way of handling this.
				throw new RuntimeException("Failed to load map.", e);
			}

			if (l.getMap().isLoaded(l.getChunkX(), l.getChunkY(), l.z) == false) {
				throw new RuntimeException("Failed to load map at " + l);
			}
		}
		super.setLocation(l);
	}

	/**
	 * Serializes this Persona's data into a single ConfigSection.
	 * 
	 * @return the tag
	 */
	@Override
	public ConfigSection serialize() {
		// Location is not YMLSerializable by design
		Location loc = getLocation();
		if (loc != null) {
			this.config.set("location", loc.serialize());
		}

		// We must use our previous config in case values in our previous config
		// are not registered. Just because data has not been used now does not
		// mean we want it removed.
		for (Entry<String, YMLSerializable> e : this.attachments.entrySet()) {
			this.config.set(e.getKey(), e.getValue().serialize());
		}

		if (this.isLoaded()) {
			// Health is loaded when start() is called. Thus if we save it
			// before we load, we're saving the default health!
			this.config.set("health", getHealth());
			this.config.set("retaliate", isRetaliate());
			this.config.set("spellbook", this.spellbook.getChildId());
			this.config.set("energy.run", this.runEnergy);
		}

		return this.config;
	}

	@Override
	public void deserialize(ConfigSection map) {
		this.config = map;

		// Any attachments which have been registered before we loaded our
		// config will be loaded here.
		// Worth noting that, here, we cannot send packets to a player yet. That
		// may done in Persona.start().
		for (Entry<String, YMLSerializable> e : this.attachments.entrySet()) {
			e.getValue().deserialize(map.getSection(e.getKey()));
		}
	}

	/**
	 * The username for this Persona. Returns directly from the profile given in
	 * the constructor.
	 * 
	 * @return The username for this Persona.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The rights of this Persona. Zero representing a normal player, one
	 * representing Player Mod and two representing an Admin.
	 * 
	 * @return the rights level of the persona.
	 */
	public int getRights() {
		return Rights.USER;
	}

	/**
	 * Fetches the next available PlayerID from the server. If no ID is
	 * available (Server full), then this throws a {@link RuntimeException}. If
	 * the ID is available, this Persona takes the slot and ID for itself.
	 * 
	 * @throws WorldFullException
	 *             if the world is full of players
	 */
	private void loadId() throws WorldFullException {
		synchronized (Core.getServer().getPersonas()) {
			this.id = (short) Core.getServer().getPersonas().add(this);
		}
	}

	@Override
	public short getSpawnIndex() {
		return this.id;
	}

	/**
	 * Destroys this Persona. It calls Persona.save(), cancels ticking, removes
	 * the Persona from the Core's Persona list then calls super.destroy(). The
	 * super.destroy() call sets the location to null.
	 * 
	 * @throws RuntimeException
	 *             if this Persona is already destroyed.
	 */
	@Override
	public void destroy() {
		if (this.isDestroyed()) {
			throw new RuntimeException(
					"This player has already been destroyed.");
		}

		for (Persona p : Core.getServer().getPersonas()) {// TODO: optimise
															// this, add some
															// sort of check for
															// lobbyplayers?
			if (p instanceof Player) {
				Player other = (Player) p;

				if (other.getFriends().isFriend(getName())) {
					other.getFriends().setOnline(getName(), false, null);
				}
			}
		}

		Core.getServer().getPersonas().remove(this.id);
		super.destroy();
	}

	@Override
	public void teleport(Location to) {
		if (to == null) {
			throw new NullPointerException(
					"Cannot teleport a player to a NULL location.");
		}
		if (to.getMap() == null) {
			throw new NullPointerException("Destination map may not be NULL");
		}
		// Disabled, we need setLocation() for this stuff!
		// TODO: Make setLocation() not clear actions,
		// Call super.setLocation() whenever we move,
		// Make teleport() clear actions.
		// getActions().clear();
		MobPreTeleportEvent event = new MobPreTeleportEvent(this,
				this.getLocation() == null ? to : this.getLocation(), to);
		event.call();
		this.setLocation(event.getTo());
		Log.debug(this + " teleported to " + this.getLocation());
		this.getUpdateMask().getMovement().setTeleported(true);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int getClientIndex() {
		// May be incorrect, may just be getId() + 0x8000
		return (getSpawnIndex() + 1) | 0x8000;
	}

	@Override
	public void restore() {
		super.restore();
		this.setRunEnergy(100);
		for (SkillType t : SkillType.values()) {
			getSkills().setModifier(t, 0);
		}
	}

	@Override
	public Attack nextAttack() {
		if (nextAttack != null) {
			Attack a = nextAttack;
			nextAttack = null; // Clear the attack
			return a;
		}

		if (autocast != null) {
			// TODO: If the player hasn't got the runes, should autocast be
			// cancelled?
			return new MagicAttack(this, autocast);
		}

		// TODO: Decide whether to use a magic, range or melee attack.
		ItemStack item = getEquipment().get(WieldType.WEAPON);
		if (item != null) {
			// if (RangeAttack.isRange(item)) {
			if (getAttackStyle().isType(SkillType.RANGE)) {
				return new RangeAttack(this);
			}

		}
		return new MeleeAttack(this);
	}

	/**
	 * Queues the given attack. If it is null, this method will cancel any
	 * existing attack. Any existing attack is cancelled, though the attack
	 * currently being executed is not. This method changes the result of
	 * nextAttack(). Calling queueAttack(null) does not stop attacks being
	 * generated.
	 * 
	 * @param a
	 *            the new attack, may be null
	 */
	public void queueAttack(Attack a) {
		this.nextAttack = a;
	}

	@Override
	public int getMaxHealth() {
		// TODO: Add equipment bonuses
		return getSkills().getLevel(SkillType.CONSTITUTION) * 10;
	}

	@Override
	public int getRespawnTicks() {
		// Immediately respawn players.
		return 0;
	}

	@Override
	public boolean isAttackable(Mob src) {
		return true;
	}

	@Override
	public void onUnload() {
		Log.debug("unloaded " + this);
	}

	@Override
	public void onIdle() {
		// Nothing.
	}

	@Override
	public Location getSpawn() {
		return DEFAULT_PLAYER_SPAWN;
	}
}