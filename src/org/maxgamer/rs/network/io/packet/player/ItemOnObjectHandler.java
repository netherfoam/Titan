package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.object.GameObject;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class ItemOnObjectHandler implements PacketProcessor<Player> {
	public static final int OPCODE = 11;

	@Override
	public void process(final Player player, RSIncomingPacket in) throws Exception {
		//Opcode 30, 4 bytes
		
		//in.readLEInt(); //not sure
		in.readLEInt();
		in.readLEShortA();
		int itemUsed = in.readShort();
		int objX = in.readShort();
		@SuppressWarnings("unused")
		boolean run = in.readByteS() != 0 ? true : false;
		int objectId = in.readShortA();
		int objY = in.readLEShortA();
		
		Location l = new Location(player.getMap(), objX, objY, player.getLocation().z);
		
		if(player.getLocation().near(l, 25) == false){
			player.getCheats().log(20, "Player attempted to use an item on an object that was too far away!");
			return;
		}
		
		final ItemStack item;
		try{
			item = ItemStack.create(itemUsed);
		}
		catch(RuntimeException e){
			player.getCheats().log(1, "Player attempted to use an item on an object. The item ID does not exist.");
			return;
		}
		
		if(player.getInventory().contains(item) == false){
			player.getCheats().log(20, "Player attempted to use an item they do not have on an object");
			return;
		}
		
		for(final GameObject g : l.getNearby(GameObject.class, 0)){
			if(g.getId() == objectId){
				if(g.getName() == null || g.getName().equals("null")){
					player.getCheats().log(10, "Player attempted to use an item on an object which has no name.");
					return;
				}
				
				AStar finder = new AStar(20);
				Path path = finder.findPath(player.getLocation(), g.getLocation(), g.getLocation().add(g.getSizeX() - 1, g.getSizeY() - 1), player.getSizeX(), player.getSizeY(), g);
				
				if (path.hasFailed()) {
					return;
				}
				
				if (!path.isEmpty()) {
					// Given our pathfinding algorithm, it ignores the object.
					// Thus the path leads into the corner of the object. So we
					// delete the last step, if one is created.
					path.removeLast();
				}
				
				player.getActions().clear();
				if (!path.isEmpty()) {
					WalkAction walk = new WalkAction(player, path) {
						@Override
						public void run() throws SuspendExecution {
							super.run();
							
							// Then use the object
							player.use(item, g);
						}
					};
					player.getActions().queue(walk);
				}
				else {
					player.use(item, g);
				}
				
				player.use(item, g);
			}
		}
	}
}