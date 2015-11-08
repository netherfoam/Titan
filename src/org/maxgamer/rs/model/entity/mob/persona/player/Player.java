package org.maxgamer.rs.model.entity.mob.persona.player;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.format.RSFont;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.events.mob.persona.player.PlayerDestroyEvent;
import org.maxgamer.rs.events.mob.persona.player.PlayerEnterWorldEvent;
import org.maxgamer.rs.events.mob.persona.player.PlayerLogOutEvent;
import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.interfaces.impl.PrayerOrbInterface;
import org.maxgamer.rs.interfaces.impl.RunInterface;
import org.maxgamer.rs.interfaces.impl.chat.ChatInterface;
import org.maxgamer.rs.interfaces.impl.frame.GamePane;
import org.maxgamer.rs.interfaces.impl.side.CombatStyles;
import org.maxgamer.rs.interfaces.impl.side.EmotesInterface;
import org.maxgamer.rs.interfaces.impl.side.EquipmentInterface;
import org.maxgamer.rs.interfaces.impl.side.ExitInterface;
import org.maxgamer.rs.interfaces.impl.side.FriendSideInterface;
import org.maxgamer.rs.interfaces.impl.side.IgnoresSideInterface;
import org.maxgamer.rs.interfaces.impl.side.InventoryInterface;
import org.maxgamer.rs.interfaces.impl.side.MagicInterface;
import org.maxgamer.rs.interfaces.impl.side.MusicInterface;
import org.maxgamer.rs.interfaces.impl.side.NotesInterface;
import org.maxgamer.rs.interfaces.impl.side.PrayerInterface;
import org.maxgamer.rs.interfaces.impl.side.QuestInterface;
import org.maxgamer.rs.interfaces.impl.side.SettingsInterface;
import org.maxgamer.rs.interfaces.impl.side.SkillsInterface;
import org.maxgamer.rs.interfaces.impl.side.TasksInterface;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.entity.mob.combat.mage.CombatSpell;
import org.maxgamer.rs.model.entity.mob.combat.mage.Spellbook;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.PersonaOptions;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.network.io.rawhandler.GamePacketHandler;
import org.maxgamer.rs.network.protocol.Game637Protocol;
import org.maxgamer.rs.network.protocol.ProtocolHandler;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * Represents a player with a network connection to the server who is actively
 * able to wander around the server and interact with it.
 * @author netherfoam
 */
public class Player extends Persona implements Client, CommandSender, YMLSerializable {
	/**
	 * The session belonging to this player, used for data transfer
	 */
	private Session session;
	
	/**
	 * The protocol interface we are to use to communicate with the player. This
	 * leaves room for using different protocols later, if necessary.
	 */
	private ProtocolHandler<Player> protocol;
	
	/**
	 * The Panes manager for this player. This controls visible windows and
	 * sends them to the player through the protocol.
	 */
	private PaneSet panes;
	
	/**
	 * A log of all cheats this player has attempted to perform. The violation
	 * level slowly decreases over time, however this allows for tracking of
	 * potential cheats or abuse.
	 */
	private CheatLog cheatLog;
	
	/**
	 * The View Distance of the player - Eg, how far are they able to walk
	 * before they need to reload the map.
	 */
	private ViewDistance viewDistance = ViewDistance.SMALL;
	
	/**
	 * True if the client has loaded the game screen, false otherwise. It may be
	 * dangerous to send some packets to the client if they are not loaded.
	 */
	private boolean isLoaded;
	
	private GamePane gamepane;
	
	/**
	 * The list of friends and ignores for this player
	 */
	private FriendsList friends;
	
	/**
	 * The list of notes for this player
	 */
	private Notes notes;
	
	/**
	 * This number is randomly generated by the client when it starts. It is not
	 * persistent after restarting the client, as it will change. TODO: This is
	 * only an int, not a long!
	 */
	private int uuid = 0;
	
	/**
	 * Constructs a new Player but does not add them to the world. This does not
	 * send any data to the session until the start() method is invoked on the
	 * player.
	 * @param profile the profile to use for this player
	 * @param session the session controlling the player.
	 * @throws WorldFullException
	 * @throws NullPointerException if the given profile is null
	 */
	public Player(String name, Session session, int uuid) throws NoSuchProtocolException, WorldFullException {
		super(name);
		
		if (session == null) {
			throw new NullPointerException("Session may not be null");
		}
		
		session.addCloseHandler(new Runnable() {
			@Override
			public void run() {
				Core.submit(new Runnable() {
					@Override
					public void run() {
						//This must be done in the primary thread.
						if (!Player.this.isDestroyed()) {
							PlayerLogOutEvent leave = new PlayerLogOutEvent(Player.this);
							leave.call();
							if (leave.isCancelled()) {
								//Keep attempting to leave until the event is not cancelled
								Core.submit(this, 1800, false);
								return;
							}
							
							Player.this.destroy();
						}
					}
				}, false);
			}
		});
		
		switch (session.getRevision()) {
			case 637:
				this.protocol = new Game637Protocol(this);
				break;
			default:
				throw new NoSuchProtocolException("No protocol known for " + protocol);
		}
		
		this.session = session;
		
		this.panes = new PaneSet(this);
		this.cheatLog = new CheatLog(this);
		this.personaOptions = new PlayerOptions(this);
		this.friends = new FriendsList(this);
		this.notes = new Notes(this);
		
		try {
			// Attempt to get the view distance from the config.
			this.viewDistance = ViewDistance.valueOf(Core.getWorldConfig().getString("players.view-distance", "SMALL").toUpperCase());
		}
		catch (IllegalArgumentException e) {
			// User has a bad view distance in config.
			this.viewDistance = ViewDistance.SMALL;
			Log.warning("The view distance set in the config is invalid. Allowed values are: " + Arrays.toString(ViewDistance.values()));
		}
	}
	
	@Override
	protected void onLoad() {
		//It should not be. We need to avoid setting the player's location.
		//Location is not YMLSerializable by design
		this.getProtocol().login();
		
		ConfigSection config = getConfig().getSection("location");
		try {
			this.setLocation(Location.deserialize(config, Core.getServer().getMap(), PLAYER_SPAWN));
		}
		catch (RuntimeException e) {
			//Map didn't seem to load.
			this.setLocation(PLAYER_SPAWN);
		}
		
		this.getProtocol().sendMap();
		
		this.gamepane = new GamePane(this);
		this.getPanes().add(gamepane);
		
		PlayerEnterWorldEvent ev = new PlayerEnterWorldEvent(this);
		ev.call();
		
		this.getProtocol().loginInterfaces();
		this.getProtocol().sendInitMasks();
		
		getSession().setHandler(new GamePacketHandler(session, this));
		
		super.onLoad();
		
		//We may now load objects which send packets when constructing them
		//to the player safely without interfering with the login procedure.
		register("friends", this.friends);
		register("notes", this.notes);
		
		getPersonaOptions().add(PersonaOptions.ATTACK, true);
		getPersonaOptions().add(PersonaOptions.FOLLOW, false);
		getPersonaOptions().add(PersonaOptions.TRADE, false);
		getPersonaOptions().add(PersonaOptions.INSPECT, false);
		
		getWindow().open(new PrayerOrbInterface(this));
		getWindow().open(new PrayerInterface(this));
		getWindow().open(new EquipmentInterface(this));
		getWindow().open(new InventoryInterface(this));
		getWindow().open(new ExitInterface(this));
		getWindow().open(new FriendSideInterface(this));
		getWindow().open(new IgnoresSideInterface(this));
		getWindow().open(new CombatStyles(this));
		getWindow().open(new SettingsInterface(this));
		getWindow().open(new EmotesInterface(this));
		getWindow().open(new ChatInterface(this));
		getWindow().open(new NotesInterface(this));
		getWindow().open(new RunInterface(this));
		getWindow().open(new QuestInterface(this));
		getWindow().open(new SkillsInterface(this));
		getWindow().open(new TasksInterface(this));
		getWindow().open(new MusicInterface(this));
		
		this.getProtocol().sendConfig(281, 1000); //Removes tutorial island limitations
	}
	
	/**
	 * Fetches the UUID for this client. The client generates this UUID on
	 * startup, and therefore it is not persistent through restarts of the
	 * client.
	 * @return the UUID for this client.
	 */
	@Override
	public int getUUID() {
		return this.uuid;
	}
	
	@Override
	public void setRunEnergy(int energy) {
		super.setRunEnergy(energy);
		getProtocol().sendRunEnergy(getRunEnergy());
	}
	
	@Override
	public void setRunning(boolean run) {
		super.setRunning(run);
		this.getProtocol().sendConfig(173, isRunning() ? 1 : 0);
	}
	
	/**
	 * The currently running Window Panes for this player.
	 * @return The currently running Window Panes for this player.
	 */
	public PaneSet getPanes() {
		return panes;
	}
	
	@Override
	public void setSpellbook(Spellbook book) {
		if (book == null) {
			throw new NullPointerException("Spellbook may not be null");
		}
		
		if (book != this.getSpellbook()) {
			//Update the player by adding the new interface
			MagicInterface magic = new MagicInterface(this, (short) book.getChildId());
			this.getWindow().open(magic);
			setAutocast(null);
			//TODO: Autocasting should be disabled
			//TODO: If the current attack (Combat.java) is a spell, it should be cancelled
		}
		
		super.setSpellbook(book);
	}
	
	@Override
	public void setAutocast(CombatSpell spell) {
		super.setAutocast(spell);
		
		if (spell == null) {
			getProtocol().sendConfig(108, -1);
		}
		else {
			getProtocol().sendConfig(108, spell.getAutocastId());
		}
	}
	
	/**
	 * A logger against this particular player for anything that is suspicious,
	 * such as picking up items they can't see, sending bad packets, using
	 * interfaces they don't have open, and so on.
	 * 
	 * @return the cheat log
	 */
	public CheatLog getCheats() {
		return this.cheatLog;
	}
	
	/**
	 * True if the player has loaded the map, false if they haven't told us they
	 * have yet.
	 * 
	 * @return True if the player has loaded the map, false if they haven't told
	 *         us they have yet.
	 */
	public boolean isMapLoaded() {
		return this.isLoaded;
	}
	
	/**
	 * Sets the return of isLoaded(). The player sends us a packet when they've
	 * finished loading a portion of the map.
	 * 
	 * @param loaded whether the player has finished or not.
	 */
	public void setLoaded(boolean loaded) {
		this.isLoaded = loaded;
	}
	
	@Override
	public void setLocation(Location l) {
		//Persona class forces the map around it to be loaded.
		super.setLocation(l);
		
		if (this.gamepane != null) {
			for (Interface interf : getWindow().getInterfaces()) {
				if (interf.isMobile() == false) {
					getWindow().close(interf);
				}
			}
		}
	}
	
	/**
	 * A systematically calculated weighting for the given persona. Since
	 * clients can't display all 2000 players from a world happily, this simple
	 * method will calculate a value that determines whether or not to show the
	 * player.
	 * @param p the player who is being prioritized
	 * @return the priority value, this may be negative.
	 */
	public int getPriority(Persona p) {
		int priority = 0;
		priority -= p.getLocation().distanceSq(getLocation()); //Further away, less prioritized
		
		if (this.getTarget() == p) priority += 50; //Our target, heavy weighted.
		if (p.getTarget() == this) priority += 25; //Targetting us, heavy weighted
		if (this.getFriends().isFriend(p.getName())) priority += 50; //You're my friend
		if (p.getRights() > Rights.USER) priority += 25; //You're an admin
		
		return priority;
	}
	
	/**
	 * Currently active interfaces the player is able to view.
	 * 
	 * @return Currently active interfaces the player is able to view.
	 */
	public GamePane getWindow() {
		if (this.gamepane == null) {
			throw new RuntimeException("The player's GamePane has not been initialized yet. Interfaces may not be used until then.");
		}
		return this.gamepane;
	}
	
	/**
	 * Returns the protocol that this player is currently using. This shouldn't
	 * be null unless accessed during login.
	 * @return the protocol for this player
	 */
	public Game637Protocol getProtocol() {
		return (Game637Protocol) this.protocol;
	}
	
	static RSFont font;
	static {
		try {
			font = new RSFont(Core.getCache().getFile(IDX.FONTS, 495).getData());
		}
		catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	@Override
	public void sendMessage(String string) {
		if (string == null) {
			throw new NullPointerException("Message may not be null!");
		}
		
		int pos = 0;
		while (pos < string.length()) {
			int width = 0;
			int start = pos;
			while (pos < string.length() && width < 480 && string.charAt(pos) != '\n') {
				width += font.getCharWidth(string.charAt(pos));
				pos++;
			}
			
			getProtocol().sendMessage(string.substring(start, pos));
		}
	}
	
	@Override
	public Session getSession() {
		return this.session;
	}
	
	@Override
	public int getVersion() {
		return this.protocol.getRevision();
	}
	
	@Override
	public void setRights(int rights) {
		super.setRights(rights);
		//TODO: Notify the player 
	}
	
	/**
	 * Whispers the given player a message
	 * @param to the player to whisper to, not null
	 * @param msg the message to send, not null
	 */
	public void whisper(Player to, String msg) {
		if (to == null) throw new NullPointerException("Player may not be null");
		if (msg == null) throw new NullPointerException("Message may not be null");
		
		getProtocol().sendPrivateMessage(to.getName(), msg);
		to.getProtocol().receivePrivateMessage(getName(), getRights(), msg);
	}
	
	/**
	 * The player's view distance setting. See the actual enum for more details.
	 * 
	 * @return The player's view distance setting.
	 */
	public ViewDistance getViewDistance() {
		return this.viewDistance;
	}
	
	/**
	 * Sets the view distance for this player, and refreshes the view distance
	 * if required.
	 * @param d the new view distance
	 * @throws NullPointerException if the distance is null
	 */
	public void setViewDistance(ViewDistance d) {
		if (d == null) {
			throw new NullPointerException("ViewDistance may not be null");
		}
		
		if (this.viewDistance != d) {
			this.viewDistance = d;
			getProtocol().sendMap();
		}
	}
	
	/**
	 * Sends the given packet to this player, if the player is connected to the
	 * server. If they are not connected, this will call
	 * Core.getServer().getPlayers().set(id, null) to dispose of the player.
	 * 
	 * @param out the packet to send
	 * @return true if the packet was sent, false if it wasn't and we tried to
	 *         disconnect the player.
	 */
	public boolean write(RSOutgoingPacket out) {
		if (this.getProtocol().getViewport() == null) {
			throw new RuntimeException("Player hasn't started yet. You should not send packets to them before they enter the world fully. If you ABSOLUTELY must, use Player.getSession().write();");
		}
		
		try {
			if (out.getLength() > 5000) {
				throw new IllegalArgumentException("The given packet's length is " + out.getLength() + " bytes long, but the client will disconnect if a packet is > 5000 bytes.");
			}
			
			if (this.getSession().isConnected()) {
				this.getSession().write(out);
				return true;
			}
			else {
				return false;
			}
		}
		catch (IOException e) {
			if (this.isDestroyed() == false) {
				Core.submit(new Runnable() {
					@Override
					public void run() {
						//Destroy the player session, the check is necessary.
						if (isDestroyed() == false) destroy();
						getSession().close(true);
					}
				}, false);
			}
			return false;
		}
	}
	
	/**
	 * Destroys this player. It calls the PlayerDisconnectEvent, then calls
	 * Player.save(), cancels the ticking of the player, removes the player from
	 * the Core's player list and then calls super.destroy(). The
	 * super.destroy() call sets the player's location to null.
	 * 
	 * This method does close the player's session.
	 * 
	 * @throws RuntimeException if this player is already destroyed.
	 */
	@Override
	public void destroy() {
		if (this.isDestroyed()) {
			throw new RuntimeException("This player has already been destroyed.");
		}
		
		if (this.gamepane != null) {
			for (Interface iface : this.gamepane.getInterfaces()) {
				try{
					if (iface.isVisible()) this.gamepane.close(iface); //May close other interfaces as well, which is why the check is necessary.
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		LinkedList<Client> list = new LinkedList<Client>();
		list.add(this);
		Core.getServer().getLogon().getAPI().save(list);
		
		// We call this before we perform the save.
		PlayerDestroyEvent e = new PlayerDestroyEvent(this);
		e.call();
		
		//Tidies up the protocol so references are removed
		getProtocol().close();
		
		Core.getServer().getLogon().getAPI().leave(this);
		
		if (getSession().isConnected()) {
			//This may cause duplicates if we send logout() prior to calling destroy.
			//It currently has no effect on the client though.
			getProtocol().logout(false);
			getSession().close(true);
		}
		
		super.destroy();
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public void setHealth(int hp) {
		super.setHealth(hp);
		getProtocol().sendConfig(1240, Math.min(getHealth() << 1, Short.MAX_VALUE));
	}
	
	/**
	 * The list of friends and ignores that this player has, not null.
	 * @return the list of friends and ignores
	 */
	public FriendsList getFriends() {
		return friends;
	}
	
	@Override
	public void setRetaliate(boolean retaliate) {
		super.setRetaliate(retaliate);
		//Overrides so that we may inform the client of the change
		getProtocol().sendConfig(172, retaliate ? 0 : 1); //1 for disable retaliate
	}
	
	@Override
	public void setAttackStyle(AttackStyle style) {
		super.setAttackStyle(style);
		
		getProtocol().sendConfig(43, style.getSlot() > 0 ? style.getSlot() - 1 : -1);
	}
	
	public Notes getNotes() {
		return this.notes;
	}
}
