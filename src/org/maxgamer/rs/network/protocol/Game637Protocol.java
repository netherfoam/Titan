package org.maxgamer.rs.network.protocol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.XTEAKey;
import org.maxgamer.rs.cache.format.CS2;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.Calc;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Graphics;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MobModel;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.entity.mob.UpdateMask;
import org.maxgamer.rs.model.entity.mob.combat.Damage;
import org.maxgamer.rs.model.entity.mob.combat.DamageType;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.facing.MobFacing;
import org.maxgamer.rs.model.entity.mob.facing.PositionFacing;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Viewport;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerMapUpdateEvent;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.map.Chunk;
import org.maxgamer.rs.model.map.DynamicMap;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.StandardMap;
import org.maxgamer.rs.model.map.SubMap;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.model.map.object.DynamicGameObject;
import org.maxgamer.rs.model.map.object.GameObject;
import org.maxgamer.rs.model.map.object.StaticGameObject;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.rs.network.io.Huffman;
import org.maxgamer.rs.network.io.packet.PacketManager;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;
import org.maxgamer.rs.network.io.packet.player.CameraHandler;
import org.maxgamer.rs.network.io.packet.player.ChatHandler;
import org.maxgamer.rs.network.io.packet.player.ClickHandler;
import org.maxgamer.rs.network.io.packet.player.ClientFocusHandler;
import org.maxgamer.rs.network.io.packet.player.DialogueHandler;
import org.maxgamer.rs.network.io.packet.player.GameObjectHandler;
import org.maxgamer.rs.network.io.packet.player.GrandExchangeHandler;
import org.maxgamer.rs.network.io.packet.player.GroundItemOptionsHandler;
import org.maxgamer.rs.network.io.packet.player.HeartbeatHandler;
import org.maxgamer.rs.network.io.packet.player.InputHandler;
import org.maxgamer.rs.network.io.packet.player.InterfaceComponentOnInterfaceComponentHandler;
import org.maxgamer.rs.network.io.packet.player.InterfaceHandler;
import org.maxgamer.rs.network.io.packet.player.ItemMoveHandler;
import org.maxgamer.rs.network.io.packet.player.ItemOnObjectHandler;
import org.maxgamer.rs.network.io.packet.player.KeyHandler;
import org.maxgamer.rs.network.io.packet.player.LoadHandler;
import org.maxgamer.rs.network.io.packet.player.LocaleHandler;
import org.maxgamer.rs.network.io.packet.player.MouseMoveHandler;
import org.maxgamer.rs.network.io.packet.player.MoveHandler;
import org.maxgamer.rs.network.io.packet.player.MusicRequestHandler;
import org.maxgamer.rs.network.io.packet.player.NPCOptionsHandler;
import org.maxgamer.rs.network.io.packet.player.PaneSwitchHandler;
import org.maxgamer.rs.network.io.packet.player.PlayerOptionsHandler;
import org.maxgamer.rs.network.io.packet.player.SocialRelationsHandler;
import org.maxgamer.rs.network.io.packet.player.SpellOnMobHandler;
import org.maxgamer.rs.network.io.packet.player.UnknownHandler;
import org.maxgamer.rs.network.io.packet.player.WorldMapHandler;
import org.maxgamer.rs.network.io.stream.RSOutputStream;
import org.maxgamer.rs.structure.areagrid.Cube;
import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.areagrid.MBRUtil;

/**
 * @author netherfoam
 */
public class Game637Protocol extends GameProtocol {
	/**
	 * The maximum number of players we show to a player at once. After this
	 * number is reached, we should begin trimming out players who are not key
	 * or are further away.
	 */
	public static final int MAX_LOCAL_PLAYERS = 70;
	public static final int PLAYER_UPDATE_RADIUS = 30;
	public static final int MAX_LOCAL_NPCS = 255; // Protocol-limited
	public static final PacketManager<Player> PACKET_MANAGER = new PacketManager<Player>();

	private static HashMap<Integer, CS2> scripts = new HashMap<Integer, CS2>();

	static {
		// Unknown packets.
		PACKET_MANAGER.setHandler(23, new UnknownHandler());

		InterfaceHandler interf = new InterfaceHandler();
		PACKET_MANAGER.setHandler(0, interf);
		PACKET_MANAGER.setHandler(6, interf);
		PACKET_MANAGER.setHandler(13, interf);
		PACKET_MANAGER.setHandler(15, interf);
		PACKET_MANAGER.setHandler(39, interf);
		PACKET_MANAGER.setHandler(46, interf);
		PACKET_MANAGER.setHandler(58, interf);
		PACKET_MANAGER.setHandler(67, interf);
		PACKET_MANAGER.setHandler(82, interf);
		PACKET_MANAGER.setHandler(73, interf);

		PACKET_MANAGER.setHandler(59, new LocaleHandler());
		PACKET_MANAGER.setHandler(16, new ChatHandler());
		PACKET_MANAGER.setHandler(62, new ClientFocusHandler());
		PACKET_MANAGER.setHandler(42, new MusicRequestHandler());
		PACKET_MANAGER.setHandler(7, new PaneSwitchHandler());
		PACKET_MANAGER.setHandler(12, new HeartbeatHandler());
		PACKET_MANAGER.setHandler(35, new MoveHandler());
		PACKET_MANAGER.setHandler(26, new KeyHandler());
		PACKET_MANAGER.setHandler(30, new CameraHandler());
		PACKET_MANAGER.setHandler(29, new ClickHandler());
		PACKET_MANAGER.setHandler(75, new LoadHandler());
		PACKET_MANAGER.setHandler(MouseMoveHandler.OPCODE, new MouseMoveHandler());

		PlayerOptionsHandler popt = new PlayerOptionsHandler();
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.FIRST_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.SECOND_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.THIRD_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.FOURTH_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.FIFTH_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.SIXTH_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.SEVENTH_OPTION, popt);
		PACKET_MANAGER.setHandler(PlayerOptionsHandler.EIGHTH_OPTION, popt);

		NPCOptionsHandler nopt = new NPCOptionsHandler();
		PACKET_MANAGER.setHandler(NPCOptionsHandler.FIRST_OPTION, nopt);
		PACKET_MANAGER.setHandler(NPCOptionsHandler.SECOND_OPTION, nopt);
		PACKET_MANAGER.setHandler(NPCOptionsHandler.THIRD_OPTION, nopt);
		PACKET_MANAGER.setHandler(NPCOptionsHandler.FOURTH_OPTION, nopt);
		PACKET_MANAGER.setHandler(NPCOptionsHandler.FIFTH_OPTION, nopt);
		PACKET_MANAGER.setHandler(NPCOptionsHandler.EXAMINE, nopt);

		GameObjectHandler goh = new GameObjectHandler();
		PACKET_MANAGER.setHandler(GameObjectHandler.FIRST_OPTION, goh);
		PACKET_MANAGER.setHandler(GameObjectHandler.SECOND_OPTION, goh);
		PACKET_MANAGER.setHandler(GameObjectHandler.THIRD_OPTION, goh);
		PACKET_MANAGER.setHandler(GameObjectHandler.FOURTH_OPTION, goh);
		PACKET_MANAGER.setHandler(GameObjectHandler.FIFTH_OPTION, goh);
		PACKET_MANAGER.setHandler(GameObjectHandler.EXAMINE, goh);

		GroundItemOptionsHandler gopt = new GroundItemOptionsHandler();
		PACKET_MANAGER.setHandler(GroundItemOptionsHandler.FIRST_OPTION, gopt);
		PACKET_MANAGER.setHandler(GroundItemOptionsHandler.SECOND_OPTION, gopt);
		PACKET_MANAGER.setHandler(GroundItemOptionsHandler.THIRD_OPTION, gopt);
		PACKET_MANAGER.setHandler(GroundItemOptionsHandler.FOURTH_OPTION, gopt);
		PACKET_MANAGER.setHandler(GroundItemOptionsHandler.FIFTH_OPTION, gopt);
		PACKET_MANAGER.setHandler(GroundItemOptionsHandler.EXAMINE, gopt);

		InputHandler inh = new InputHandler();
		PACKET_MANAGER.setHandler(InputHandler.STRING_OPTION_1, inh);
		PACKET_MANAGER.setHandler(InputHandler.STRING_OPTION_2, inh);
		PACKET_MANAGER.setHandler(InputHandler.INT_OPTION, inh);
		// PACKET_MANAGER.setHandler(InputHandler.ADDING_FRIEND, inh);

		PACKET_MANAGER.setHandler(ItemMoveHandler.PACKET_ID, new ItemMoveHandler());

		SocialRelationsHandler srh = new SocialRelationsHandler();
		PACKET_MANAGER.setHandler(SocialRelationsHandler.ADDING_FRIEND, srh);
		PACKET_MANAGER.setHandler(SocialRelationsHandler.ADDING_IGNORE, srh);
		PACKET_MANAGER.setHandler(SocialRelationsHandler.REMOVING_FRIEND, srh);
		PACKET_MANAGER.setHandler(SocialRelationsHandler.REMOVING_IGNORE, srh);

		ChatHandler chh = new ChatHandler();
		PACKET_MANAGER.setHandler(ChatHandler.PRIVATE_CHAT, chh);
		PACKET_MANAGER.setHandler(ChatHandler.PUBLIC_CHAT, chh);

		SpellOnMobHandler somh = new SpellOnMobHandler();
		PACKET_MANAGER.setHandler(SpellOnMobHandler.ON_NPC, somh);
		PACKET_MANAGER.setHandler(SpellOnMobHandler.ON_PERSONA, somh);

		// What a name.
		InterfaceComponentOnInterfaceComponentHandler icoich = new InterfaceComponentOnInterfaceComponentHandler();
		PACKET_MANAGER.setHandler(InterfaceComponentOnInterfaceComponentHandler.ON_USE, icoich);

		PACKET_MANAGER.setHandler(DialogueHandler.OPCODE, new DialogueHandler());
		PACKET_MANAGER.setHandler(WorldMapHandler.OPCODE, new WorldMapHandler());

		PACKET_MANAGER.setHandler(ItemOnObjectHandler.OPCODE, new ItemOnObjectHandler());
		PACKET_MANAGER.setHandler(GrandExchangeHandler.OPCODE, new GrandExchangeHandler());
	}

	/** An array of players who are within view distance of this player */
	private ArrayList<Persona> localPlayers = new ArrayList<Persona>(); // TODO:
																		// This
																		// can
																		// be
																		// made
																		// a
																		// HashSet

	/** An array of NPCs who are within view distance of this player */
	private ArrayList<NPC> localNpcs = new ArrayList<NPC>(); // TODO: Limit this

	/**
	 * The last viewport we sent the player. Not null if the player has been
	 * sent the initial map login data
	 */
	private Viewport viewport;

	public Game637Protocol(Player p) {
		super(p);
	}

	@Override
	public int getRevision() {
		return 637;
	}

	public PacketManager<Player> getPacketManager() {
		return PACKET_MANAGER;
	}

	/**
	 * Returns true if the given Persona has been made visible to the user and
	 * is currently still accessible. Use this check when they client states
	 * they are interacting with a Persona.
	 * 
	 * @param p
	 *            the player who may or may not be visible
	 * @return true if they are visible, false if they are not.
	 */
	public boolean isVisible(Persona p) {
		return localPlayers.contains(p);
	}

	/**
	 * Returns true if the given NPC has been made visible to the user and is
	 * currently still accessible. Use this check when they client states they
	 * are interacting with an NPC.
	 * 
	 * @param n
	 *            the NPC who may or may not be visible
	 * @return true if they are visible, false if they are not.
	 */
	public boolean isVisible(NPC n) {
		return localNpcs.contains(n);
	}

	/**
	 * Requests the client invoke the given client script.
	 * 
	 * @param script
	 *            the script ID to invoke. This will be decoded the first time
	 *            this scriptId is used and cached.
	 * @param params
	 *            the parameters the client is to invoke the script with. These
	 *            must be strings or numbers. May be null if no args.
	 * @throws NullPointerException
	 *             if the given script is null
	 * @throws IllegalArgumentException
	 *             If the script rejects the arguments (See
	 *             {@link CS2#isValidInvokation(Object...)})
	 * @throws RuntimeException
	 *             if there is an IOException while getting the script data for
	 *             the first time.
	 */
	public void invoke(int script, Object... params) {
		CS2 cs2 = scripts.get(script);
		if (cs2 == null) {
			try {
				cs2 = CS2.decode(script, Core.getCache().getFile(IDX.INTERFACE_SCRIPTS, script).getData());
			} catch (IOException e) {
				// There's no elegant way of handling this. We really shouldn't
				// have to catch an IOException
				// each time.
				throw new RuntimeException(e);
			}
			scripts.put(script, cs2);
		}

		this.invoke(cs2, params);
	}

	/**
	 * Requests the client invoke the given client script.
	 * 
	 * @param script
	 *            the script to invoke. Decode this from the cache in IDX 12
	 *            (IDX.INTERFACE_SCRIPTS). Each cache file is its own CS2
	 *            script. (CS2.decode(cache.getFile(IDX.INTERFACE_SCRIPTS,
	 *            scriptId)))
	 * @param params
	 *            the parameters the client is to invoke the script with. These
	 *            must be strings or numbers. May be null if no args.
	 * @throws NullPointerException
	 *             if the given script is null
	 * @throws IllegalArgumentException
	 *             If the script rejects the arguments (See
	 *             {@link CS2#isValidInvokation(Object...)})
	 */
	private void invoke(CS2 script, Object... params) {
		if (params == null)
			params = new Object[0]; // No-args

		if (script == null) {
			throw new NullPointerException("Script may not be null");
		}

		if (script.isValidInvokation(params) == false) {
			StringBuilder given = new StringBuilder();
			for (Object o : params) {
				if (o instanceof Number) {
					given.append("i");
				} else if (o instanceof String || o instanceof Character) {
					given.append("s");
				} else {
					given.append("?");
				}
			}

			given.append(", required ");
			given.append(script.getIntArgCount() + " ints and " + script.getStringArgCount() + " strings");

			throw new IllegalArgumentException("Bad parameters. " + given.toString());
		}

		RSOutgoingPacket out = new RSOutgoingPacket(16);

		// Write the types backwards
		for (int i = 0; i < params.length; i++) {
			if (params[params.length - 1 - i] instanceof String) {
				out.write((byte) 's');
			} else {
				out.write((byte) 'i');
			}
		}
		out.write((byte) 0); // Null terminator

		// Write the parameters forwards.
		for (int i = 0; i < params.length; i++) {
			Object arg = params[i];
			if (arg instanceof String) {
				out.writePJStr1((String) arg);
			} else {
				out.writeInt(((Number) arg).intValue());
			}
		}

		out.writeInt(script.getId());

		getPlayer().write(out);
	}

	public void invokeBlankScript(int id) {
		RSOutgoingPacket out = new RSOutgoingPacket(98);
		out.writeShort(0);
		out.writePJStr1("");
		out.writeInt(id);
		getPlayer().write(out);
	}

	public void invokeScript(int id, Object... params) {
		if (params == null)
			params = new Object[0]; // No-args

		RSOutgoingPacket out = new RSOutgoingPacket(16);

		// Write the types backwards
		for (int i = 0; i < params.length; i++) {
			if (params[params.length - 1 - i] instanceof String) {
				out.write((byte) 's');
			} else {
				out.write((byte) 'i');
			}
		}
		out.write((byte) 0); // Null terminator

		// Write the parameters forwards.
		for (int i = 0; i < params.length; i++) {
			Object arg = params[i];
			if (arg instanceof String) {
				out.writePJStr1((String) arg);
			} else {
				out.writeInt(((Number) arg).intValue());
			}
		}

		out.writeInt(id);

		getPlayer().write(out);
	}

	private long lastPlayerUpdate = System.currentTimeMillis();

	/**
	 * Sends the player the required updates after a tick. If necessary, this
	 * will also update the player's map. This method sends all nearby players
	 * and their mask changes to the player, allowing them to see movement,
	 * animations, teleports and more. This method does not call the reset
	 * method on any masks.
	 */
	public void sendUpdates() {
		boolean firstUpdate = false;
		if(viewport == null) {
			firstUpdate = true;
		}
		
		if (isMapUpdateRequired()) {
			Log.debug("Map upadte sent!");
			sendMap();
		}

		boolean change = false;
		final Location playerLoc = getPlayer().getLocation();
		
		// A MBR that overlaps with all entities that the player can see with their view distance.
		MBR visibleArea = viewport; 

		PriorityQueue<Persona> sorted = new PriorityQueue<Persona>(100, new Comparator<Persona>() {
			@Override
			public int compare(Persona p1, Persona p2) {
				return getPlayer().getPriority(p2) - getPlayer().getPriority(p1);
			}
		});

		sorted.addAll(playerLoc.getMap().getEntities(MBRUtil.getOverlap(visibleArea, new Cube(new int[] {
				playerLoc.x - PLAYER_UPDATE_RADIUS, playerLoc.y - PLAYER_UPDATE_RADIUS, playerLoc.z }, new int[] {
				playerLoc.x + PLAYER_UPDATE_RADIUS, playerLoc.y + PLAYER_UPDATE_RADIUS, 1 })), 100, Persona.class));
		
		ArrayList<Persona> nearby = new ArrayList<Persona>(sorted);
		nearby.remove(getPlayer()); // Don't send updates about ourself here.

		// Skip any hidden players
		Iterator<Persona> pit = nearby.iterator();
		while (pit.hasNext()) {
			Persona p = pit.next();
			if (p.isHidden()) {
				pit.remove();
			}
		}

		RSOutgoingPacket out = new RSOutgoingPacket(70);
		
		// This is a dummy packet. We append it later.
		RSOutgoingPacket update = new RSOutgoingPacket(-1); 
		out.startBitAccess();

		if (getPlayer().getUpdateMask().hasChanged() || getPlayer().getModel().hasChanged() || firstUpdate) {
			// We have updates for the client about themselves.
			out.writeBits(1, 1);
			applyMovementUpdate(out, getPlayer());
			change = true;

			// Updateblock Start
			appendPlayerUpdateBlock(update, getPlayer(), firstUpdate);
		} else {
			// We have no updates for the client about themselves.
			out.writeBits(1, 0);
		}

		/*
		 * Now we loop through all players which must be sent to the player, or
		 * updated, or removed.
		 */
		pit = localPlayers.iterator();
		while (pit.hasNext()) {
			Persona p = pit.next();

			int rating = nearby.indexOf(p);
			if (rating == -1 || rating >= MAX_LOCAL_PLAYERS || p.isHidden()) {
				// This player should no longer be on screen.
				pit.remove();
				out.writeBits(1, 1);
				out.writeBits(11, p.getSpawnIndex() + 1);

				// Remove the player from the client.
				sendLocalPlayerStatus(out, 0, false);
				out.writeBits(1, 0);
				change = true;
			} else {
				// Apply a regular update
				if (p.getUpdateMask().hasChanged() || p.getModel().hasChanged()) {
					change = true;
					appendPlayerUpdateBlock(update, p, false);
					// This player has updates to send
					out.writeBits(1, 1);
					out.writeBits(11, p.getSpawnIndex() + 1);
					// Teleports and walking.
					applyMovementUpdate(out, p);
				} else {
					// How do we know which player we're specifying 'has no
					// updates?'
					// In the case where there are 0 updates, the client will
					// need to receive
					// a number of '0' bits which is equal to the number of
					// players they
					// have been told about.
					out.writeBits(1, 0);
				}
			}
		}

		// Add new local players
		int added = 0;
		for (int i = 0; i < nearby.size() && localPlayers.size() < MAX_LOCAL_PLAYERS; i++) {
			Persona p = nearby.get(i);

			if (localPlayers.contains(p) || p.isHidden()) {
				// No need to check if p == getPlayer() because nearby has had
				// nearby.remove(getPlayer())
				continue;
			}

			change = true;
			out.writeBits(1, 1);
			out.writeBits(11, p.getSpawnIndex() + 1);

			// Add the nearby player to the client.
			out.writeBits(2, 0);
			// boolean updateHash = false;
			out.writeBits(1, 1); // !updateHash
			// if updateHash == false
			out.writeBits(2, 3);
			Location l = p.getLocation();
			out.writeBits(2, l.z);
			out.writeBits(8, l.x >> 6);
			out.writeBits(8, l.y >> 6);
			// endif

			// The number of tiles they're in from their current region.
			out.writeBits(6, l.x); // Last 6 bits only
			out.writeBits(6, l.y); // Last 6 bits only
			out.writeBits(1, 1);
			localPlayers.add(p);

			// We must force the player's model to show
			appendPlayerUpdateBlock(update, p, true);

			added++;
			if (added > 5)
				break;
		}

		// We're done.
		out.writeBits(1, 0);
		out.finishBitAccess();
		// must send at least 1 update every 3 ticks just to keep client alive
		if (change || System.currentTimeMillis() - lastPlayerUpdate > 1800) {
			lastPlayerUpdate = System.currentTimeMillis();
			// Other players were updated in the update block. They go after the
			// client's player.
			out.write(update.getPayload());

			getPlayer().write(out);
		}

		// NPC UPDATING PROCESS
		change = false;
		out = new RSOutgoingPacket(6);
		update = new RSOutgoingPacket(-1); // Dummy

		// Something about this sorting seems off?
		PriorityQueue<NPC> sortedNpcs = new PriorityQueue<NPC>(100, new Comparator<NPC>() {
			@Override
			public int compare(NPC n1, NPC n2) {
				return getPlayer().getLocation().distanceSq(n1.getLocation()) - getPlayer().getLocation().distanceSq(n2.getLocation());
			}
		});

		sortedNpcs.addAll(playerLoc.getMap().getEntities(visibleArea, 100, NPC.class));
		Iterator<NPC> nit = sortedNpcs.iterator();
		while (nit.hasNext()) {
			NPC n = nit.next();
			if (n.getLocation().z != p.getLocation().z || n.isHidden()) {
				nit.remove();
			}
		}

		ArrayList<NPC> sortedNPCList = new ArrayList<NPC>(sortedNpcs);

		out.writeByte(localNpcs.size()); // Will never be > 255

		nit = localNpcs.iterator();
		out.startBitAccess();

		while (nit.hasNext()) {
			NPC n = nit.next();
			if (n == null || n.isDestroyed()) {
				change = true;
				nit.remove();
				continue;
			}
			if (n.getLocation() == null || n.isHidden() || n.getLocation().z != getPlayer().getLocation().z || MBRUtil.isOverlap(visibleArea, n.getLocation()) == false || n.getUpdateMask().isTeleporting() || sortedNPCList.indexOf(n) >= MAX_LOCAL_NPCS) {
				change = true;
				// The NPC is not visible to the player anymore.
				out.writeBits(1, 1);
				out.writeBits(2, 3);
				nit.remove();
				continue;
			}

			if (n.getUpdateMask().hasChanged()) {
				change = true;
				out.writeBits(1, 1);
			} else {
				out.writeBits(1, 0);
				continue; // No updates for NPC
			}

			if (n.getUpdateMask().getMovement().hasChanged()) {
				change = true;
				out.writeBits(2, 1);// Is one of these 'run' flag? TODO: NPC Run
									// Protocol
				out.writeBits(3, n.getUpdateMask().getMovement().getDirection());
				if (n.getUpdateMask().hasChanged()) {
					out.writeBits(1, 1);
				} else {
					out.writeBits(1, 0);
					continue; // Done with update.
				}
			} else {
				// No movement
				out.writeBits(2, 0);
			}

			// We know n.getUpdateMask().hasChanged() == true here.
			appendUpdateBlock(update, n);
			change = true;
		}

		// A NPC has to be within this cube to be added to the player. This is a
		// limitation of the protocol.
		visibleArea = new Cube(new int[] { playerLoc.x - 31, playerLoc.y - 31, playerLoc.z }, new int[] {
				63, 63, 0 });

		// Adding new NPCs
		// for (NPC n : viewport.getCenter().getMap().getEntities(visibleArea,
		// 40, NPC.class)) {
		// for(NPC n : sortedNPCList){
		for (int i = 0; i < sortedNPCList.size() && i < MAX_LOCAL_NPCS; i++) {
			NPC n = sortedNPCList.get(i);
			if (localNpcs.contains(n)) { // TODO: Private NPC check
				continue;
			}

			// NPC's are added relative to the player's location
			int x = n.getLocation().x - getPlayer().getLocation().x;
			int y = n.getLocation().y - getPlayer().getLocation().y;

			if (x > 15 || x < -15 || y > 15 || y < -15) {
				// Too far
				continue;
			}

			out.writeBits(15, n.getClientIndex());
			out.writeBits(14, n.getId());
			out.writeBits(1, 1);

			// The client assumes the value is <= 15 and >= -15.
			if (x < 0) {
				x += 32;
			}

			if (y < 0) {
				y += 32;
			}
			change = true;
			out.writeBits(5, x);
			out.writeBits(5, y);
			out.writeBits(2, n.getLocation().z);
			if (n.getUpdateMask().hasChanged()) {
				out.writeBits(1, 1); // Has mask updates
				appendUpdateBlock(update, n);
			} else {
				out.writeBits(1, 0);
			}
			out.writeBits(3, 0); // Face-Dir.. TODO: Is there an option for this
									// in players?
			localNpcs.add(n);
		}

		out.writeBits(15, 0x7FFF); // Some kind of EOF marker?
		out.finishBitAccess();
		if (change) {
			out.write(update.getPayload());
			getPlayer().write(out);
		}
	}

	private void appendUpdateBlock(RSOutgoingPacket out, NPC npc) {
		RSOutgoingPacket block = new RSOutgoingPacket(-1);
		int mask = 0x0000;
		UpdateMask um = npc.getUpdateMask();

		/*
		 * Order is: ForceMovement Hits Graphics MobFacing SwitchID Animation
		 * FacePosition ForceText
		 */

		// "ForceMovement" is 0x2000 and refers to..? Moving the NPC?
		if (um.getHits() != null) {
			mask |= 0x04;

			HashMap<Mob, ArrayList<Damage>> hits = um.getHits();

			int size = 0;
			for (Entry<Mob, ArrayList<Damage>> e : hits.entrySet()) {
				size += e.getValue().size();
				if (size > 255) {
					size = 255;
					break;
				}
			}
			block.writeByteC(size);

			size = 0;
			iterator: for (Entry<Mob, ArrayList<Damage>> e : hits.entrySet()) {
				Mob dealer = e.getKey();

				for (Damage d : e.getValue()) {
					if (++size > 255) {
						break iterator;
					}

					if (dealer == p || d.getTarget() == p) {
						block.writeSmart(d.getType().toByte());
					} else {
						block.writeSmart(d.getType().toByte() + 14);
					}

					block.writeSmart(d.getHit());
					block.writeSmart(0); // TODO: Delay
					Mob target = d.getTarget();
					int scale;
					if (target.getMaxHealth() > 0) {
						scale = (target.getHealth() * 255) / target.getMaxHealth();
					} else {
						scale = 0; // No health left.
					}
					block.writeByte(Calc.betweeni(0, 255, scale)); // Scale of
																	// 0-255,
																	// how
																	// healthy
																	// are you?
				}
			}
		}

		if (um.getGraphics() != null) {
			mask |= 0x02;
			Graphics g = um.getGraphics();
			block.writeShortA(g.getId());
			block.writeLEInt(g.getDelay());
			block.writeByteC(g.getHeight());
		}

		if (um.hasFacingChanged()) {
			Facing fm = npc.getFacing();
			if (fm == null) {
				mask |= 0x08;
				block.writeShort(-1);
			} else if (fm instanceof MobFacing) {
				MobFacing mf = (MobFacing) fm;
				mask |= 0x08;
				block.writeShort(mf.getTarget().getClientIndex());
			}
		}
		/*
		 * if (fm.getFaceMob() != 0) { mask |= 0x08;
		 * 
		 * block.writeShort(fm.getFaceMob()); }
		 */
		// "SwitchID" is 0x80 and probably refers to when a NPC
		// changes its display ID
		if (um.getAnimation() != null) {

			mask |= 0x10;

			Animation a = um.getAnimation();
			for (int i = 0; i < 4; i++) {
				block.writeShortA(a.getId());
			}
			block.writeByteS(a.getDelay());
		}

		/*
		 * if (fm.hasFacePositionChanged() && fm.getFaceMob() == 0) { mask |=
		 * 0x40; Position face = fm.getTarget(); if (face != null) {
		 * block.writeLEShortA(face.x * 2); block.writeLEShortA(face.y * 2); }
		 * else { block.writeLEShortA(0); block.writeLEShortA(0); } }
		 */

		if (um.hasFacingChanged()) {
			Facing fm = npc.getFacing();
			if (fm == null) {
				mask |= 0x40;
				block.writeLEShortA(0);
				block.writeLEShortA(0);
			} else if (fm instanceof PositionFacing) {
				mask |= 0x40;
				PositionFacing pf = (PositionFacing) fm;
				Position face = pf.getTarget();
				block.writeLEShortA(face.x * 2);
				block.writeLEShortA(face.y * 2);
			}
		}
		// "ForceText" is 0x01 and probably refers to making the
		// NPC have text hover its head
		if (um.getSay() != null) {
			mask |= 0x01;
			block.writePJStr1(um.getSay());
		}

		if (mask > 0x80) {
			mask = mask |= 0x20;
		}

		out.writeByte(mask);
		if (mask > 0x80)
			out.write(mask >> 8);
		out.write(block.getPayload());
	}

	private void applyMovementUpdate(RSOutgoingPacket out, Persona p) {
		if (p.getUpdateMask().getMovement().hasTeleported()) {
			sendLocalPlayerStatus(out, 3, true);
			out.writeBits(1, 1);
			Location l = p.getLocation();
			out.writeBits(2, l.z);
			out.writeBits(14, l.x);
			out.writeBits(14, l.y);
			return;
		}

		// This is a generic movement update.
		MovementUpdate m = p.getUpdateMask().getMovement();

		// TODO: This is probably incorrect, if it is 0, it will probably just
		// be a 0 indicating no movement!
		out.writeBits(1, 1); // status boolean, if false, this removes the
								// player.. Or does it?

		if (m.hasChanged()) {
			int dir = m.getDirection();
			if (m.isRun()) {
				out.writeBits(2, 2);
				out.writeBits(4, dir); // Run is 4 bits
			} else {
				out.writeBits(2, 1);
				out.writeBits(3, dir); // Walk is 3 bits
			}
		} else {
			out.writeBits(2, 0);
		}
	}

	private void sendLocalPlayerStatus(RSOutgoingPacket out, int type, boolean status) {
		out.writeBits(1, status ? 1 : 0);
		out.writeBits(2, type);
	}

	// Unknown is 0x400 Format: (Short, ByteA, ByteS)
	// Unknown is 0x40000 Format: (ByteS[length], Short1[length])

	private static int MASK_GFX = 0x4000;
	// private static int MASK_MOVE_FORCE = 0x200;
	// private static int MASK_TEXT_FORCE = 0x8000;

	private static int MASK_TELEPORTED = 0x2000;
	private static int MASK_FACEPOS = 0x04;
	private static int MASK_FACEMOB = 0x02;
	private static int MASK_HITS = 0x08;
	private static int MASK_ANIM = 0x10;
	private static int MASK_MODEL = 0x40;
	private static int MASK_MOVE = 0x01;
	private static int MASK_2BYTE = 0x20;
	private static int MASK_3BYTE = 0x800;

	private void appendPlayerUpdateBlock(RSOutgoingPacket out, Persona p, boolean isNew) {
		UpdateMask mu = p.getUpdateMask();
		int mask = 0;

		RSOutgoingPacket buffer = new RSOutgoingPacket(-1);
		if (mu.getGraphics() != null) {
			mask |= MASK_GFX;
			Graphics g = mu.getGraphics();
			buffer.writeLEShortA(g.getId());
			buffer.writeInt2(g.getDelay());
			buffer.writeByte(g.getHeight());
		}

		if (mu.getMovement().hasTeleported()) {
			mask |= MASK_TELEPORTED;

			// The value '1' here causes the player to teleport without
			// movement.
			// Other values cause the player to walk, unless the distance is
			// further than
			// a single step away, in which case, they are teleported. This is
			// probably a "resync" of player coordinates. Values other than '1'
			// will case the player to face their previous location.

			buffer.writeByteC(1);
		}

		if (mu.hasFacingChanged() || isNew) {
			Facing fm = p.getFacing();
			if (fm == null) {
				mask |= MASK_FACEPOS;
				buffer.writeLEShort(0);

				mask |= MASK_FACEMOB;
				buffer.writeLEShort(-1);
			} else if (fm instanceof PositionFacing) {
				PositionFacing pf = (PositionFacing) fm;
				mask |= MASK_FACEPOS;

				Position face = pf.getTarget();
				if (face != null) {
					int dX = p.getLocation().x - face.x;
					int dY = p.getLocation().y - face.y;
					buffer.writeLEShort(((int) (Math.atan2(dX, dY) * 2607.5945876176133)) & 0xFFFF);
				} else {
					buffer.writeLEShort(0);
				}
			} else if (fm instanceof MobFacing) {
				MobFacing mf = (MobFacing) fm;

				mask |= MASK_FACEMOB;
				buffer.writeLEShort(mf.getTarget().getClientIndex());
			}
		}

		if (mu.getHits() != null) {
			mask |= MASK_HITS;

			HashMap<Mob, ArrayList<Damage>> hits = mu.getHits();

			int size = 0;
			for (Entry<Mob, ArrayList<Damage>> e : hits.entrySet()) {
				for (Damage d : e.getValue()) {
					if (d.getType() == DamageType.MISS && this.p != e.getKey() && this.p != d.getTarget()) {
						// Don't send hits which are 0's and not involved with
						// this player (waste of bandwidth)
						continue;
					}
					size++;
					if (size >= 255) {
						size = 255;
						break;
					}
				}
			}
			buffer.writeByte(size);

			size = 0;
			iterator: for (Entry<Mob, ArrayList<Damage>> e : hits.entrySet()) {
				Mob dealer = e.getKey();

				for (Damage d : e.getValue()) {
					if (++size > 255) {
						break iterator;
					}

					if (d.getType() == DamageType.MISS && this.p != dealer && this.p != d.getTarget()) {
						// Don't send hits which are 0's and not involved with
						// this player (waste of bandwidth)
						continue;
					}
					// Something about damage soaking?
					// if(m != null){ out.writeSmart(0x7FFF); }
					buffer.writeSmart(DamageType.getCode(d.getType(), this.p == dealer || this.p == d.getTarget(), d.isMax()));
					buffer.writeSmart(d.getHit());

					// Something about damage soaking?
					// if(m != null){ int type2 = }

					buffer.writeSmart(0); // hit delay
					// How much green is left on the hitbar, scale 0-255
					// If a player's health is greater than their max health,
					// the result of
					// this will be >255. This is why we use Math.min()
					buffer.writeByte(Calc.betweeni(0, 255, p.getHealth() * 255 / p.getMaxHealth()));

				}
			}
		}

		if (mu.getAnimation() != null) {
			mask |= MASK_ANIM;

			// We write this four times, the best guess seems to be that Jagex
			// was planning on using mixed animations
			// http://www.rune-server.org/runescape-development/rs-503-client-server/267639-621-new-animation-mask.html
			Animation a = mu.getAnimation();
			for (int i = 0; i < 4; i++) {
				buffer.writeLEShortA(a.getId());
			}
			buffer.writeByteC(a.getDelay());
		}

		if (p.getModel().hasChanged() || isNew) {
			mask |= MASK_MODEL;

			MobModel model = p.getModel();
			byte[] data = model.getUpdateData();
			buffer.writeByteA(data.length);
			buffer.write(data);
		}

		if (mu.getSay() != null) {
			mask |= 0x8000;
			buffer.writePJStr1(mu.getSay());
		}

		if (mu.getMovement().hasChanged() && !mu.getMovement().hasTeleported()) {
			mask |= MASK_MOVE;

			MovementUpdate m = p.getUpdateMask().getMovement();
			if (m.isRun()) {
				buffer.writeByteA(2); // Run
			} else {
				buffer.writeByteA(1); // Walk
			}
		}

		// This works very similar to how you'd imagine a 3-byte smart value
		// would work.
		if (mask > 0x80) {
			mask |= MASK_2BYTE; // if the biggest bit is set, then it means
								// 'there is another byte coming with data'

			if (mask > 0x8000) {
				mask |= MASK_3BYTE; // As above
			}
		}

		out.writeByte((byte) mask);
		if (mask > 0x80) {
			// mask |= MASK_2BYTE; //if the biggest bit is set, then it means
			// 'there is another byte coming with data'
			out.writeByte((byte) (mask >> 8));

			if (mask > 0x8000) {
				// mask |= MASK_3BYTE; //As above
				out.writeByte((byte) (mask >> 16));
			}
		}

		out.write(buffer.getPayload());
	}

	public void playMusic(int volume, int fadeSpeed, int trackId) {
		RSOutgoingPacket out = new RSOutgoingPacket(73);
		int v = volume > 100 ? volume : (int) (255 * (volume / 100D));
		out.writeByteC(v); // volume from 0 to 255
		out.writeByteC(fadeSpeed); // Fade in speed
		out.writeLEShort(trackId); // Track ID of song in cache
		p.write(out);
	}

	public void sendSound(int soundId, int volume, int speed) {
		RSOutgoingPacket out = new RSOutgoingPacket(69);
		out.writeShort(soundId); // -1 for no sound
		out.writeByte(1); // Times played
		out.writeShort(0); // Delay
		out.writeByte(volume);
		out.writeShort(speed); // 255 seems normal - dementhium
		getPlayer().write(out);
	}

	public void sendVoice(int voiceId, int volume, int speed) {
		RSOutgoingPacket out = new RSOutgoingPacket(7);
		out.writeShort(voiceId);
		out.writeByte(1); // Times played
		out.writeShort(0); // Delay
		out.writeByte(volume);
		out.writeShort(speed); // 255 seems normal - dementhium
		getPlayer().write(out);
	}

	public void sendGroundItem(Location tile, ItemStack item) {
		RSOutgoingPacket out = new RSOutgoingPacket(29);
		out.writeLEShortA(item.getId());
		out.writeByteS(((tile.x & 0x7) << 4) | (tile.y & 0x7));
		out.writeLEShortA((short) Math.min(item.getAmount(), Short.MAX_VALUE));
		chunkUpdate(tile);
		getPlayer().write(out);
	}

	public void removeGroundItem(Location tile, ItemStack item) {
		RSOutgoingPacket out = new RSOutgoingPacket(59);
		out.writeByteS(((tile.x & 0x7) << 4) | (tile.y & 0x7));
		out.writeShortA(item.getId());
		chunkUpdate(tile);;
		getPlayer().write(out);
	}

	/**
	 * This packet must be sent before sending things like ground item or
	 * gameobject updates. It presumably prepares the client to update a chunk
	 * of their map. A chunk is an 8x8 area. The Z plane (Height) is used by
	 * this method.
	 * 
	 * @param tile
	 *            the tile which is inside the chunk we want to update.
	 * @throws IllegalArgumentException
	 *             if the given tile is not contained by the player's last
	 *             {@link Viewport} update.
	 */
	public void chunkUpdate(Location tile) {
		if (MBRUtil.isOverlap(viewport, tile) == false) {
			throw new IllegalArgumentException("Cannot update the tile " + tile + " for " + p + " because it is not contained in their last viewport.");
		}

		int chunkRadius = getPlayer().getViewDistance().getChunkRadius(); // 6;
		int x = (tile.x >> 3) - ((viewport.getCenter().x >> 3) - chunkRadius);
		int y = (tile.y >> 3) - ((viewport.getCenter().y >> 3) - chunkRadius);

		if (x > 127 || y > 127 || x < -128 || y < -128) {
			// We'll have an overflow, we cannot update this chunk without
			// teleporting the player closer.
			// Protocol does not allow this
			throw new IllegalArgumentException("Protocol only allows Chunk Updates to occur at a maximum distance of 2039 tiles (-128 to 127 chunks) away");
		}

		RSOutgoingPacket out = new RSOutgoingPacket(114);
		out.writeByteA(x);
		out.writeByteC(tile.z);
		out.writeByteA(y);
		getPlayer().write(out);
	}

	/**
	 * Sends a message to a player
	 * 
	 * @param type
	 *            the message type
	 * @param userFrom
	 *            the user it was from, eg 'netherfoam sent trade request' or
	 *            null for none
	 * @param text
	 *            the message 'sent trade request'
	 */
	public void sendMessage(int type, String userFrom, String text) {
		RSOutgoingPacket out = new RSOutgoingPacket(53);
		out.writeByte(type);
		out.writeInt(0);

		if (userFrom != null && !userFrom.isEmpty()) {
			out.writeByte(1);
			out.writePJStr1(userFrom);
		} else
			out.writeByte(0);

		out.writePJStr1(text);
		getPlayer().write(out);
	}

	public void sendMessage(String text) {
		if (text.length() > 255) {
			text = text.substring(0, 255);
		}
		sendMessage(0, null, text);
	}

	public void sendTradeRequest(String userFrom, String message) {
		sendMessage(100, userFrom, message);
	}

	public void sendDuelRequest(String userFrom, String message) {
		sendMessage(101, userFrom, message);
	}

	public void sendAMask(int offset, int length, int interfaceId1, int childId1, int flagsMajor, int flagsMinor) {
		RSOutgoingPacket out = new RSOutgoingPacket(119);

		out.writeLEShort(flagsMajor);
		out.writeLEShort(flagsMinor);
		out.writeShortA(length);
		out.writeShortA(offset);
		out.writeLEInt(interfaceId1 << 16 | childId1);

		getPlayer().write(out);
	}

	public void sendInitMasks() {
		switch (getPlayer().getSession().getScreenSettings().getDisplayMode()) {
		case 0:
		case 1:
			sendFixedAMasks();
			break;
		case 2:
		case 3:
			sendFullScreenAMasks();
			break;
		}
	}

	public void sendFixedAMasks() {
		this.sendAMask(-1, -1, 548, 123, 0, 2);
		this.sendAMask(-1, -1, 884, 11, 0, 2);
		this.sendAMask(-1, -1, 884, 12, 0, 2);
		this.sendAMask(-1, -1, 884, 13, 0, 2);
		this.sendAMask(-1, -1, 884, 14, 0, 2);
		this.sendAMask(-1, -1, 548, 124, 0, 2);
		this.sendAMask(-1, -1, 548, 125, 0, 2);
		this.sendAMask(0, 300, 190, 18, 0, 14);
		this.sendAMask(0, 11, 190, 15, 0, 2);
		this.sendAMask(-1, -1, 548, 126, 0, 2);
		this.sendAMask(-1, -1, 548, 127, 0, 2);
		this.sendAMask(0, 27, 149, 0, 69, 32142);
		this.sendAMask(28, 55, 149, 0, 32, 0);
		this.sendAMask(-1, -1, 548, 128, 0, 2);
		this.sendAMask(-1, -1, 548, 129, 0, 2);
		this.sendAMask(0, 30, 271, 8, 0, 2);
		this.sendAMask(-1, -1, 548, 130, 0, 2);
		this.sendAMask(-1, -1, 548, 93, 0, 2);
		this.sendAMask(-1, -1, 548, 94, 0, 2);
		this.sendAMask(-1, -1, 548, 95, 0, 2);
		this.sendAMask(-1, -1, 548, 96, 0, 2);
		this.sendAMask(-1, -1, 548, 97, 0, 2);
		this.sendAMask(-1, -1, 548, 98, 0, 2);
		this.sendAMask(-1, -1, 548, 99, 0, 2);
		this.sendAMask(0, 1727, 187, 1, 0, 26);
		this.sendAMask(0, 11, 187, 9, 36, 6);
		this.sendAMask(12, 23, 187, 9, 0, 4);
		this.sendAMask(24, 24, 187, 9, 32, 0);
		this.sendAMask(-1, -1, 548, 100, 0, 2);
		this.sendAMask(0, 29, 34, 9, 40, 30);
		this.sendAMask(-1, -1, 548, 123, 0, 2);
		this.sendAMask(-1, -1, 884, 11, 0, 2);
		this.sendAMask(-1, -1, 884, 12, 0, 2);
		this.sendAMask(-1, -1, 884, 13, 0, 2);
		this.sendAMask(-1, -1, 884, 14, 0, 2);
		this.sendAMask(-1, -1, 548, 123, 0, 2);
		this.sendAMask(-1, -1, 884, 11, 0, 2);
		this.sendAMask(-1, -1, 884, 12, 0, 2);
		this.sendAMask(-1, -1, 884, 13, 0, 2);
		this.sendAMask(-1, -1, 884, 14, 0, 2);
	}

	public void sendFullScreenAMasks() {
		this.sendAMask(-1, -1, 746, 36, 0, 2);
		this.sendAMask(-1, -1, 884, 11, 0, 2);
		this.sendAMask(-1, -1, 884, 12, 0, 2);
		this.sendAMask(-1, -1, 884, 13, 0, 2);
		this.sendAMask(-1, -1, 884, 14, 0, 2);
		this.sendAMask(-1, -1, 746, 37, 0, 2);
		this.sendAMask(-1, -1, 746, 38, 0, 2);
		this.sendAMask(0, 300, 190, 18, 0, 14);
		this.sendAMask(0, 11, 190, 15, 0, 2);
		this.sendAMask(-1, -1, 746, 39, 0, 2);
		this.sendAMask(-1, -1, 746, 40, 0, 2);
		this.sendAMask(0, 27, 149, 0, 69, 32142);
		this.sendAMask(28, 55, 149, 0, 32, 0);
		this.sendAMask(-1, -1, 746, 41, 0, 2);
		this.sendAMask(-1, -1, 746, 42, 0, 2);
		this.sendAMask(0, 29, 271, 8, 0, 2);
		this.sendAMask(-1, -1, 746, 43, 0, 2);
		this.sendAMask(-1, -1, 746, 44, 0, 2);
		this.sendAMask(-1, -1, 746, 45, 0, 2);
		this.sendAMask(-1, -1, 746, 46, 0, 2);
		this.sendAMask(-1, -1, 746, 47, 0, 2);
		this.sendAMask(-1, -1, 746, 48, 0, 2);
		this.sendAMask(-1, -1, 746, 49, 0, 2);
		this.sendAMask(-1, -1, 746, 50, 0, 2);
		this.sendAMask(0, 1727, 187, 1, 0, 26);
		this.sendAMask(0, 11, 187, 9, 36, 6);
		this.sendAMask(12, 23, 187, 9, 0, 4);
		this.sendAMask(24, 24, 187, 9, 32, 0);
		this.sendAMask(-1, -1, 746, 51, 0, 2);
		this.sendAMask(0, 29, 34, 9, 40, 30);
		this.sendAMask(-1, -1, 746, 36, 0, 2);
		this.sendAMask(-1, -1, 884, 11, 0, 2);
		this.sendAMask(-1, -1, 884, 12, 0, 2);
		this.sendAMask(-1, -1, 884, 13, 0, 2);
		this.sendAMask(-1, -1, 884, 14, 0, 2);
		this.sendAMask(-1, -1, 746, 36, 0, 2);
		this.sendAMask(-1, -1, 884, 11, 0, 2);
		this.sendAMask(-1, -1, 884, 12, 0, 2);
		this.sendAMask(-1, -1, 884, 13, 0, 2);
		this.sendAMask(-1, -1, 884, 14, 0, 2);
	}

	public void loginInterfaces() {
		switch (getPlayer().getSession().getScreenSettings().getDisplayMode()) {
		case 0:
		case 1:
			this.sendInterface(true, 548, 67, 751);
			// this.sendInterface(true, 548, 192, 752);
			this.sendInterface(true, 548, 16, 754);
			this.sendInterface(true, 548, 182, 748); // Exit interface HP Orb
			// this.sendInterface(true, 548, 184, 749); //Prayer orb
			// this.sendInterface(true, 548, 185, 750); //Run orb
			this.sendInterface(true, 548, 187, 747);
			this.sendInterface(true, 548, 14, 745);
			this.sendInterface(true, 752, 9, 137);
			// this.sendInterface(true, 548, 202, 884); //Combat styles
			// this.sendInterface(true, 548, 203, 1056); //Tasks Interface
			// this.sendInterface(true, 548, 204, 320); //Skills interface
			// this.sendInterface(true, 548, 205, 190); //Quest interface
			// this.sendInterface(true, 548, 206, 149); //Inventory interface
			// this.sendInterface(true, 548, 207, 387); //Equipment interface
			// this.sendInterface(true, 548, 208, 271); //Prayer interface
			// this.sendInterface(true, 548, 209, 192); //magic interface
			// this.sendInterface(true, 548, 211, 550); //Friends
			// this.sendInterface(true, 548, 212, 551); //Ignores
			// this.sendInterface(true, 548, 213, 589); //Clan chat interface
			// this.sendInterface(true, 548, 214, 261); //Settings
			// this.sendInterface(true, 548, 215, 464); //Emotes
			// this.sendInterface(true, 548, 216, 187); //Music interface
			// this.sendInterface(true, 548, 217, 34); //Notes interface
			this.sendInterface(true, 548, 220, 182);
			// ActionSender.sendInterface(player, 1, 548, 18, 206);
			// ActionSender.sendInterface(player, 1, 548, 197, 207);
			// ActionSender.sendInterface(player, 1, 548, 18, 762);
			// ActionSender.sendInterface(player, 1, 548, 197, 763);
			// ActionSender.sendInterface(player, 1, 548, 7, 667);
			break;
		case 2:
		case 3:
			// this.sendWindowsPane(player, 746, 0);
			this.sendInterface(true, 746, 16, 751);
			// this.sendInterface(true, 746, 69, 752);
			this.sendInterface(true, 746, 70, 754);
			this.sendInterface(true, 746, 174, 748); // Exit interface HP Orb
			// this.sendInterface(true, 746, 175, 749); //Prayer orb
			// this.sendInterface(true, 746, 176, 750); //Run orb
			this.sendInterface(true, 746, 177, 747);
			this.sendInterface(true, 746, 12, 745);
			this.sendInterface(true, 752, 9, 137);
			// this.sendInterface(true, 746, 87, 884); //Combat styles
			// this.sendInterface(true, 746, 88, 1056); //Tasks interface
			// this.sendInterface(true, 746, 89, 320); //Skills interface
			// this.sendInterface(true, 746, 90, 190); //Quest interface
			// this.sendInterface(true, 746, 91, 149); //Inventory interface
			// this.sendInterface(true, 746, 92, 387); //Equipment interface
			// this.sendInterface(true, 746, 93, 271); //Prayer interface
			// this.sendInterface(true, 746, 94, 192); //magic interface
			// this.sendInterface(true, 746, 96, 550); //Friends
			// this.sendInterface(true, 746, 97, 551); //Ignores
			// this.sendInterface(true, 746, 98, 589); //Clan chat interface
			// this.sendInterface(true, 746, 99, 261); //Settings
			// this.sendInterface(true, 746, 100, 464); //Emotes
			// this.sendInterface(true, 746, 101, 187); //Music interface
			// this.sendInterface(true, 746, 102, 34); //Note interface
			// this.sendInterface(true, 746, 105, 182); //Exit interface
			// ActionSender.sendInterface(player, 1, 746, 9, 206);
			// ActionSender.sendInterface(player, 1, 746, 84, 207);
			// ActionSender.sendInterface(player, 1, 746, 9, 762);
			// ActionSender.sendInterface(player, 1, 746, 84, 763);
			// ActionSender.sendInterface(player, 1, 746, 6, 667);
			break;
		}
	}

	public void sendWindowsPane(int pane, boolean redraw) {
		RSOutgoingPacket bldr = new RSOutgoingPacket(36);
		bldr.writeByteA(redraw ? 2 : 0);
		bldr.writeShortA(pane);
		getPlayer().write(bldr);
	}

	public void sendInterface(boolean clickable, int parentId, int interfacePosition, int interfaceUUID) {
		RSOutgoingPacket out = new RSOutgoingPacket(50);

		out.writeLEShort(parentId);
		out.writeLEShort(interfacePosition);
		out.writeLEShortA(interfacePosition >> 16 | interfaceUUID);
		// out.writeByteC(clickable ? 0 : 1);
		out.writeByteC(clickable ? 1 : 0);

		getPlayer().write(out);
	}

	public void sendCloseInterface(int window, int interfaceId) {
		RSOutgoingPacket out = new RSOutgoingPacket(61);
		out.writeLEInt(window << 16 | interfaceId);

		getPlayer().write(out);
	}

	public void login() {
		RSOutputStream bldr = new RSOutputStream(14);

		bldr.writeByte((byte) 13); // length
		bldr.writeByte((byte) 0); // This is the player rights. Disabled to
									// allow for normal command execution
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 0);
		bldr.writeByte((byte) 1);
		bldr.writeByte((byte) 0);
		bldr.writeShort((short) (getPlayer().getSpawnIndex() + 1)); // Player
																	// id's are
																	// 1-2047
		bldr.writeByte((byte) 1);
		bldr.writeMediumInt(0);
		bldr.writeByte((byte) 1); // 0 = f2p, 1 = p2p, 2 = subscription

		p.getSession().write(bldr.getPayload());
	}

	public boolean isMapUpdateRequired() {
		if (viewport.getCenter() == null) {
			return true; // No updates have been sent
		}
		if (viewport.getCenter().getMap() != getPlayer().getLocation().getMap()) {
			return true; // Changed map instance
		}

		Location l = getPlayer().getLocation();
		int dcx = (l.x >> WorldMap.CHUNK_BITS) - (viewport.getCenter().x >> WorldMap.CHUNK_BITS);
		int dcy = (l.y >> WorldMap.CHUNK_BITS) - (viewport.getCenter().y >> WorldMap.CHUNK_BITS);

		dcx = Math.abs(dcx);
		dcy = Math.abs(dcy);

		int chunkRadius = getPlayer().getViewDistance().getChunkRadius();

		if (dcx >= chunkRadius - 1) {
			return true;
		}
		if (dcy >= chunkRadius - 1) {
			return true;
		}

		// Map sent previously, same map, player is within view distance. All fine!
		return false; 
	}

	/**
	 * Forcefully sends an update to the player. Use isMapUpdateRequired() to
	 * check whether the player actually needs the map. This worlds for standard
	 * and dynamic maps.
	 */
	public void sendMap() {
		PlayerMapUpdateEvent e = new PlayerMapUpdateEvent(getPlayer(), viewport == null);
		e.call();
		if (e.isCancelled()) {
			return;
		}

		Log.debug("Sending map to " + getPlayer());
		WorldMap m = getPlayer().getLocation().getMap();
		RSOutgoingPacket out;

		if (m instanceof StandardMap || m instanceof SubMap) {
			out = new RSOutgoingPacket(80);
		} else if (m instanceof DynamicMap) {
			out = new RSOutgoingPacket(31);
		} else {
			throw new RuntimeException("Not implemented");
		}

		if (viewport == null) {
			loginData(out);
		}

		Location l = getPlayer().getLocation();
		if (this.viewport != null) {
			// Remove the old viewport
			this.viewport.getCenter().getMap().remove(this.viewport);
		}

		// TODO: If this Viewport is constructed while the player is near the
		// edge of the map,
		// then it will overlap the edge of the map and thus not fit in the
		// WorldMap. This will
		// cause errors.
		// Construct the new Viewport
		this.viewport = new Viewport(getPlayer());
		// Add the new Viewport to the map
		this.viewport.getCenter().getMap().put(this.viewport);

		WorldMap map = l.getMap();

		if (m instanceof StandardMap || m instanceof SubMap) {
			out.writeLEShortA(l.y >> 3);
			out.writeShortA(l.x >> 3);

			// Viewport depth
			out.writeByte(getPlayer().getViewDistance().getId()); 

			out.writeByteA(1); // Unknown, possibly force load.

			int chunkX = l.x >> 3;
			int chunkY = l.y >> 3;

			// Bitshift right by 3, then dividing by two equals bitshift right by 4
			int mapHash = getPlayer().getViewDistance().getTileSize() >> 4;

			for (int regionX = (chunkX - mapHash) >> 3; regionX <= (chunkX + mapHash) >> 3; regionX++) {
				for (int regionY = (chunkY - mapHash) >> 3; regionY <= (chunkY + mapHash) >> 3; regionY++) {
					int fileId;
					try {
						fileId = Core.getCache().getFileId(IDX.LANDSCAPES, "l" + regionX + "_" + regionY);
					} catch (FileNotFoundException e1) {
						// Instead of raising an exception, the client loads the
						// region anyway. However,
						// they still need blank XTEA keys to load the region.
						for (int i = 0; i < 4; i++) {
							out.writeInt(0);
						}
						continue; // We've written our dummy keys.
					}
					XTEAKey xtea = Core.getCache().getXTEA().getKey(IDX.LANDSCAPES, fileId);
					int[] keys;
					if (xtea == null) {
						keys = new int[] { 0, 0, 0, 0 };
					} else {
						keys = xtea.getKeys();
					}

					for (int i = 0; i < 4; i++) {
						out.writeInt(keys[i]);
					}
				}
			}
		} else if (m instanceof DynamicMap) {
			this.localNpcs.clear();
			out.writeByteA(1); // Loading type
			out.writeByteA(getPlayer().getViewDistance().getId());

			int cx = l.x >> 3;
			int cy = l.y >> 3;
			out.writeShortA(cy); // Region
			out.writeLEShort(cx); // Region
			out.writeByteA(1); // Force reload

			out.startBitAccess();
			/*
			 * Bitshift right by 3, then dividing by two equals bitshift right
			 * by 4
			 */
			int mapHash = getPlayer().getViewDistance().getTileSize() >> 4;

			ArrayList<Integer> regionids = new ArrayList<Integer>();

			for (int z = 0; z < 4; z++) {
				for (int chunkX = (cx - mapHash); chunkX <= (cx + mapHash); chunkX++) {
					for (int chunkY = (cy - mapHash); chunkY <= (cy + mapHash); chunkY++) {
						Chunk c = map.getChunk(chunkX, chunkY, z);
						if (c == null) {
							out.writeBits(1, 0);
						} else {
							int rotation = 0;
							out.writeBits(1, 1);
							out.writeBits(2, c.getCacheZ());
							/*
							 * It appears X can only go up to 8191 inclusive for
							 * DynamicMaps
							 */
							out.writeBits(10, c.getCacheX());
							out.writeBits(11, c.getCacheY());
							out.writeBits(2, rotation);
							out.writeBits(1, 0);

							int regionId = (((c.getCacheX() & ~0x7) << 5) + (c.getCacheY() >> 3));
							if (regionids.contains(regionId) == false) {
								regionids.add(regionId);
							}
						}
					}
				}
			}

			out.finishBitAccess();
			for (int regionId : regionids) {
				int[] keys = null;
				if (regionId != 0) {
					int fileId;
					try {
						fileId = Core.getCache().getFileId(IDX.LANDSCAPES, "l" + (regionId >> 8) + "_" + (regionId & 0xFF));
						XTEAKey xtea = Core.getCache().getXTEA().getKey(IDX.LANDSCAPES, fileId);
						if (xtea != null) {
							keys = xtea.getKeys();
						}
					} catch (FileNotFoundException ex) {
						/* There are no objects here. We send an empty XTEA key */
					}
				}

				if (keys == null) {
					keys = new int[] { 0, 0, 0, 0 };
				}

				for (int i = 0; i < 4; i++) {
					out.writeInt(keys[i]);
				}
			}
		} else {
			throw new RuntimeException("Not implemented");
		}

		getPlayer().setLoaded(false); // Begin loading!
		try {
			getPlayer().getSession().write(out);
		} catch (IOException e1) {
			Log.debug("There was an error sending the map to " + getPlayer().getName() + ".");
			e1.printStackTrace();
			return;
		}

		// Dynamic objects are objects which aren't in the cache and must be
		// added if visible
		for (DynamicGameObject g : m.getEntities(viewport, 40, DynamicGameObject.class)) {
			if (g.isHidden())
				continue; // Object is not visible, do not send it to them.
			showObject(g);
		}

		// Static objects are objects which are in the cache and must be removed
		// if hidden
		for (StaticGameObject g : m.getEntities(viewport, 40, StaticGameObject.class)) {
			if (g.isHidden() == false)
				continue; // Object is visible, do not send it to them.
			hideObject(g);
		}
	}

	/**
	 * Appends the login data to a map packet. This should only be sent once per
	 * login.
	 * 
	 * @param out
	 *            the map packet we're building to
	 */
	private void loginData(RSOutgoingPacket out) {
		out.startBitAccess();
		Location l = getPlayer().getLocation();

		out.writeBits(30, l.z << 28 | l.x << 14 | l.y & 0x3FFF);

		short playerId = (short) (getPlayer().getSpawnIndex() + 1); // Network
																	// ID is +1.

		for (int index = 0; index < 2047; index++) {
			if (index == playerId) {
				continue;
			}

			Persona p = Core.getServer().getPersonas().get(index);
			if (p == null || l.near(p.getLocation(), getPlayer().getViewDistance().getTileSize()) == false) {
				out.writeBits(18, 0);
			} else {
				Location o = p.getLocation();
				// 18 bit version of location.
				int hash = (o.z << 16) | ((o.x >> 6) << 8) | (o.y >> 6);
				out.writeBits(18, hash);
			}
		}
		out.finishBitAccess();
	}

	public void sendConfig(int id, int value) {
		RSOutgoingPacket out;
		if (value >= 0 && value < 128) {
			out = new RSOutgoingPacket(21);
			out.writeShortA(id);
			out.writeByteS(value);
		} else {
			out = new RSOutgoingPacket(27);
			out.writeInt1(value);
			out.writeShort(id);
		}

		getPlayer().write(out);
	}

	public void sendBConfig(int id, int value) {
		RSOutgoingPacket out;
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			out = new RSOutgoingPacket(5);
			out.writeLEShortA(id);
			out.writeLEInt(value);
		} else {
			out = new RSOutgoingPacket(51);
			out.writeLEShort(id);
			out.writeByte(value);
		}
		getPlayer().write(out);
	}

	public void sendLocalMessage(Persona sender, String s, int effects) {
		RSOutgoingPacket out = new RSOutgoingPacket(62);
		out.writeShort(sender.getSpawnIndex() + 1);
		out.writeShort(effects);
		out.writeByte(sender.getRights());

		byte[] data = new byte[256]; // Has to be 256, in case huffman actually
										// causes it to get bigger.
		data[0] = (byte) s.length();
		int size = 1 + Huffman.huffmanCompress(s, data, 1);
		out.write(data, 0, size);

		getPlayer().write(out);
	}

	public void unlockInterfaceComponent(int interfaceId, int childId, boolean unlocked) {
		RSOutgoingPacket out = new RSOutgoingPacket(3);
		out.writeInt2((interfaceId << 16) | childId);
		out.writeByteC(unlocked ? 0 : 1);
		getPlayer().write(out);
	}

	public void sendContainer(int type, boolean split, Container c) {
		RSOutgoingPacket bldr = new RSOutgoingPacket(113);
		bldr.writeShort(type);
		bldr.writeByte((split ? 1 : 0));
		bldr.writeShort(c.getSize());
		for (int i = 0; i < c.getSize(); i++) {
			ItemStack item = c.get(i);
			int id, amt;
			if (item == null) {
				id = -1;
				amt = 0;
			} else {
				id = item.getId();
				amt = (int) item.getAmount();
			}
			bldr.writeShortA(id + 1);
			bldr.writeByteC(amt > 254 ? 0xff : amt);
			if (amt > 0xfe)
				bldr.writeInt1(amt);
		}
	}

	public void setItem(int iface, boolean split, Container c, int... slots) {
		RSOutgoingPacket out = new RSOutgoingPacket(57);

		out.writeShort(iface);
		out.writeByte(split ? 1 : 0);

		for (int slot : slots) {
			out.writeSmart(slot);
			ItemStack item = c.get(slot);
			if (item == null) {
				out.writeShort(0); // Client represents NULL as 0
			} else {
				out.writeShort(item.getId() + 1); // Client IDs are offset by +1
				int n = (int) Math.min(item.getAmount(), Integer.MAX_VALUE);
				if (n >= 0xFF) {
					out.writeByte(0xFF);
					out.writeInt(n);
				} else {
					out.writeByte(n);
				}
			}
		}

		getPlayer().write(out);
	}

	public void setItem(int iface, boolean split, Container c) {
		RSOutgoingPacket out = new RSOutgoingPacket(57);

		out.writeShort(iface);
		out.writeByte(split ? 1 : 0);

		for (int i = 0; i < c.getSize(); i++) {
			out.writeSmart(i);
			ItemStack item = c.get(i);
			if (item == null) {
				out.writeShort(0); // Client represents NULL as 0
			} else {
				out.writeShort(item.getId() + 1); // Client IDs are offset by +1
				int n = (int) Math.min(item.getAmount(), Integer.MAX_VALUE);
				if (n >= 0xFF) {
					out.writeByte(0xFF);
					out.writeInt(n);
				} else {
					out.writeByte(n);
				}
			}
		}

		getPlayer().write(out);
	}

	public void setItem(int iface, boolean split, ItemStack item, int slot) {
		RSOutgoingPacket out = new RSOutgoingPacket(57);

		out.writeShort(iface);
		out.writeByte(split ? 1 : 0);

		out.writeSmart(slot);
		if (item == null) {
			out.writeShort(0); // Client represents NULL as 0
		} else {
			out.writeShort(item.getId() + 1); // Client IDs are offset by +1
			int n = (int) Math.min(item.getAmount(), Integer.MAX_VALUE);
			if (n >= 0xFF) {
				out.writeByte(0xFF);
				out.writeInt(n);
			} else {
				out.writeByte(n);
			}
		}
		getPlayer().write(out);
	}

	public void close() {
		localPlayers.clear();
		localNpcs.clear();
		// Most importantly, remove the viewport from the map. It has a
		// reference to the player.
		if (viewport != null)
			viewport.getCenter().getMap().remove(viewport);
	}

	public void logout(boolean lobby) {
		RSOutgoingPacket out = new RSOutgoingPacket(lobby ? 45 : 23);
		this.getPlayer().write(out);
	}

	public void hideObject(GameObject g) {
		if (g == null)
			throw new NullPointerException("Gameobject may not be null");
		Location l = g.getLocation();
		if (l == null)
			throw new NullPointerException("Location may not be null");
		Direction f = g.getFacing();
		if (f == null)
			throw new NullPointerException("Facing may not be null");

		int rotation;
		if (f == Directions.NORTH)
			rotation = 0;
		else if (f == Directions.EAST)
			rotation = 1;
		else if (f == Directions.SOUTH)
			rotation = 2;
		else if (f == Directions.WEST)
			rotation = 3;
		else
			throw new IllegalStateException();

		RSOutgoingPacket out = new RSOutgoingPacket(19);
		out.writeByteC((g.getType() << 2) | rotation);
		out.writeByteS(((l.x & 0x7) << 4) | (l.y & 0x7));

		// Notify of chunk update
		chunkUpdate(l);
		getPlayer().write(out);
	}

	public void showObject(GameObject g) {
		if (g == null)
			throw new NullPointerException("Gameobject may not be null");
		Location l = g.getLocation();
		if (l == null)
			throw new NullPointerException("Location may not be null");
		Direction f = g.getFacing();
		if (f == null)
			throw new NullPointerException("Facing may not be null");

		RSOutgoingPacket out = new RSOutgoingPacket(78);
		out.writeByteC(((l.x & 0x7) << 4) | (l.y & 0x7));
		out.writeLEShort(g.getId());

		int rotation;
		if (f == Directions.NORTH)
			rotation = 0;
		else if (f == Directions.EAST)
			rotation = 1;
		else if (f == Directions.SOUTH)
			rotation = 2;
		else if (f == Directions.WEST)
			rotation = 3;
		else
			throw new IllegalStateException();

		out.writeByte((g.getType() << 2) | rotation);

		// Notify of chunk update
		chunkUpdate(l);
		getPlayer().write(out);
	}

	public void sendFriend(String name, boolean victimOnline, String world) {
		RSOutgoingPacket out = new RSOutgoingPacket(10);
		out.writeByte(0); // boolean 1 = true, else false
		out.writePJStr1(name);// Username
		out.writePJStr1(""); // Display name maybe? Uhm, I can't see anywhere
								// where the client uses this but it reads it
								// and saves it?

		out.writeShort(victimOnline ? 1 : 0);// Checks whether online or not.//
												// World ID?
		out.writeByte(0); // byte
		if (victimOnline) {// <col=00FF00> is not necessary here
			// out.writePJStr1(Core.getServer().getDefinition().getWorldId() +
			// ": " + Core.getServer().getDefinition().getText()); //World name
			out.writePJStr1(world);
			out.writeByte(0); // Some boolean, 1=true, else false
		}
		getPlayer().write(out);
	}

	public void sendUnlockFriendsList() {
		RSOutgoingPacket out = new RSOutgoingPacket(10);
		getPlayer().write(out);
	}

	public void sendIgnores(String ignore) {
		RSOutgoingPacket out = new RSOutgoingPacket(85);

		// Some bool, if last bit is not set, the name is added to the end of
		// the list.
		// Otherwise it replaces the old name which .equals it?
		// The second last bit, is also some boolean value, which is only used
		// if the
		// last bit is not set.
		out.writeByte(0);

		out.writePJStr1(ignore);
		out.writePJStr1(""); // Display name, or empty for same as ignore
		out.writePJStr1(ignore); // Last known as name
		out.writePJStr1(""); // Last known display name or empty for same as
								// last known name
		getPlayer().write(out);
	}

	/**
	 * Unlocks/resets the ignores list.
	 */
	public void sendUnlockIgnores() {
		getPlayer().write(new RSOutgoingPacket(12));
	}

	public void sendPrivateMessage(String username, String message) {
		byte[] bytes = new byte[256];
		int length = Huffman.huffmanCompress(message, bytes, 0);
		RSOutgoingPacket out = new RSOutgoingPacket(76);
		out.writePJStr1(username);
		out.writeByte(message.length());
		out.write(bytes, 0, length);
		getPlayer().write(out);
	}

	public void receivePrivateMessage(String username, int rights, String message) {
		long id = (long) (1 + ((Math.random() * Long.MAX_VALUE) + (Math.random() * Long.MIN_VALUE)));// What
																										// is
																										// this
																										// for?
		byte[] bytes = new byte[256];
		bytes[0] = (byte) message.length();
		int length = 1 + Huffman.huffmanCompress(message, bytes, 1);
		RSOutgoingPacket out = new RSOutgoingPacket(30);
		out.writeByte(0); // has a previous name?
		out.writePJStr1(username);
		out.writeShort((int) (id >> 32));
		out.writeMediumInt((int) (id - ((id >> 32) << 32)));
		out.writeByte(rights);
		out.write(bytes, 0, length);
		getPlayer().write(out);
	}

	/**
	 * Runs the client script to remove the given friend
	 * 
	 * @param name
	 *            the name of the friend case insensitive
	 */
	public void removeFriend(String name) {
		invoke(126, -1, 5, "", name);
	}

	/**
	 * Runs the client script to remove the given ignore
	 * 
	 * @param name
	 *            the name of the ignore case insensitive
	 */
	public void removeIgnore(String name) {
		invoke(130, name);
	}

	public Viewport getViewport() {
		return viewport;
	}

	/**
	 * Called "SpecialString" in dementhium
	 * 
	 * @param id
	 *            string id
	 * @param name
	 *            string text
	 */
	public void sendGlobalString(int id, String name) {
		RSOutgoingPacket out = new RSOutgoingPacket(88);
		out.writePJStr1(name);
		out.writeLEShortA(id);
		getPlayer().write(out);
	}

	@Override
	public void sendRunEnergy(int val) {
		RSOutgoingPacket out = new RSOutgoingPacket(13);
		out.writeByte(val);
		getPlayer().write(out);
	}

	/**
	 * Renders the player's head on an interface. Eg, for dialogues where the
	 * player speaks, this method can be used to display the players head. This
	 * doesn't set the animation used.
	 * 
	 * @param ifaceId
	 *            the interface id
	 * @param componentId
	 *            the child position id
	 */
	public void sendPlayerOnInterface(int ifaceId, int componentId) {
		RSOutgoingPacket out = new RSOutgoingPacket(54);
		out.writeInt1(ifaceId << 16 | componentId);
		getPlayer().write(out);
	}

	/**
	 * Renders the NPC's head on an interface. Eg, for dialogues where the NPC
	 * speaks, this method can be used to display the NPC's head. This doesn't
	 * set the animation used.
	 * 
	 * @param ifaceId
	 *            the interface id
	 * @param componentId
	 *            the child position id
	 * @param npcId
	 *            the ID of the NPC definition
	 */
	public void sendNPCOnInterface(int ifaceId, int componentId, int npcId) {
		RSOutgoingPacket out = new RSOutgoingPacket(71);
		out.writeInt2(ifaceId << 16 | componentId);
		out.writeLEShort(npcId);
		getPlayer().write(out);
	}

	/**
	 * See also {@link #sendNPCOnInterface(int, int, int)}
	 * {@link #sendPlayerOnInterface(int, int)} Animates the face sent
	 * 
	 * @param emoteId
	 * @param ifaceId
	 * @param componentId
	 */
	public void sendInterAnimation(int emoteId, int ifaceId, int componentId) {
		RSOutgoingPacket out = new RSOutgoingPacket(86);
		out.writeInt1(ifaceId << 16 | componentId);
		out.writeLEShortA(emoteId);
		getPlayer().write(out);
	}

	public void sendItemOnInterface(int interfaceId, int childId, ItemStack item) {
		RSOutgoingPacket out = new RSOutgoingPacket(91);
		if (item != null) {
			out.writeLEShortA(item.getId());
			out.writeInt((int) Math.min(item.getAmount(), Integer.MAX_VALUE));
		} else {
			out.writeLEShortA(0);
			out.writeInt(0);
		}
		out.writeInt2(interfaceId << 16 | childId);
		getPlayer().write(out);
	}
}