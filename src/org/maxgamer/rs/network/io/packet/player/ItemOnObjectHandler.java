package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class ItemOnObjectHandler implements PacketProcessor<Player> {
	public static final int OPCODE = 11;

	@Override
	public void process(Player player, RSIncomingPacket in) throws Exception {
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
		
		ItemStack item;
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
		
		for(GameObject g : l.getNearby(GameObject.class, 0)){
			if(g.getId() == objectId){
				if(g.getName() == null || g.getName().equals("null")){
					player.getCheats().log(10, "Player attempted to use an item on an object which has no name.");
					return;
				}
				
				player.use(item, g);
			}
		}
	}
}