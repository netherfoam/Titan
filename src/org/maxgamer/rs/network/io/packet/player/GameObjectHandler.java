package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.definition.GameObjectProto;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.GameObjectAction;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class GameObjectHandler implements PacketProcessor<Player> {
	public static final int FIRST_OPTION = 76;
	public static final int SECOND_OPTION = 55;
	public static final int THIRD_OPTION = 60;
	public static final int FOURTH_OPTION = 81;
	public static final int FIFTH_OPTION = 25;
	public static final int EXAMINE = 48;
	
	@Override
	public void process(Player p, RSIncomingPacket in) throws Exception {
		int id;
		int x;
		int y;
		@SuppressWarnings("unused")
		boolean run;
		int option = -1;
		
		switch (in.getOpcode()) {
			case FIRST_OPTION:
				id = in.readLEShort();
				run = in.readByte() != 0;
				x = in.readLEShortA();
				y = in.readShortA();
				option = 1;
				break;
			case SECOND_OPTION:
				y = in.readShort();
				id = in.readLEShort();
				x = in.readShort();
				run = in.readByteS() != 0;
				option = 2;
				break;
			case THIRD_OPTION:
				x = in.readShortA();
				id = in.readLEShortA();
				run = (in.readByte() + 128) != 0;
				y = in.readLEShortA();
				
				option = 3;
				break;
			case FOURTH_OPTION:
				x = in.readLEShort();
				y = in.readLEShort();
				id = in.readShortA();
				run = (in.readByte() + 128) != 0;
				option = 4;
				break;
			case FIFTH_OPTION:
				x = in.readShortA();
				id = in.readLEShortA();
				run = in.readByteA() != 0;
				y = in.readLEShortA();
				option = 5;
				break;
			case EXAMINE:
				id = in.readShort();
				id = id & 0xFFFF;
				if (p.getRights() >= Rights.MOD) {
					p.sendMessage("ID: " + id);
				}
				try {
					GameObjectProto def = GameObject.getDefinition(id);
					if (def == null) {
						p.getCheats().log(5, "Player attempted to examine a NULL gameobject.");
						return;
					}
					p.sendMessage(def.getExamine());
				}
				catch (Exception e) {
					p.getCheats().log(5, "Player attempted to examine a bad gameobject.");
					return;
				}
				
				return;
			default:
				return;
		}
		
		Log.debug("X: " + x + ", Y: " + y);
		id = id & 0xFFFF; //Signed
		
		Location l = new Location(p.getLocation().getMap(), x, y, p.getLocation().z);
		for (GameObject g : l.getNearby(GameObject.class, 0)) {
			if (g.getId() == id && g.isHidden() == false) {
				String s = g.getDefiniton().getOption(option - 1); //Becomes zero-based
				if (s == null) {
					p.getCheats().log(10, "Player attempted to use a NULL option on a gameobject. Gameobject: " + g + ", option: " + option + "/5");
					return;
				}
				
				AStar finder = new AStar(20);
				Path path = finder.findPath(p.getLocation(), g.getLocation(), g.getLocation().add(g.getSizeX() - 1, g.getSizeY() - 1), p.getSizeX(), p.getSizeY(), g);
				
				if (path.hasFailed()) {
					return;
				}
				
				if (path.isEmpty() == false) {
					//Given our pathfinding algorithm, it ignores the object. Thus the path leads into the corner of the object. So we delete the last step, if one is created.
					path.removeLast();
				}
				
				p.getActions().clear();
				
				GameObjectAction script = new GameObjectAction(p, s, g);
				if (path.isEmpty() == false) {
					WalkAction walk = new WalkAction(p, path);
					walk.pair(script);
					p.getActions().queue(walk);
					p.getActions().insertAfter(walk, script);
				}
				else {
					p.getActions().queue(script);
				}
				
				return;
			}
		}
		
		p.getCheats().log(10, "Player attempted to use a NULL gameobject. ID: " + id + ", option: " + option + "/5");
		return;
	}
}