package org.maxgamer.rs.interfaces.impl.frame;

import org.maxgamer.rs.interfaces.Pane;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam, alva
 */
public class WorldMapPane extends Pane {
	
	public WorldMapPane(Player p) {
		super(p, 755);
		
		//Where to center the map 
		player.getProtocol().sendBConfig(622, player.getLocation().x << 14 | player.getLocation().y | player.getLocation().z << 28);
		//player.getUpdateMask().setAnimation(new Animation(840));
		
		//"You are here"
		player.getProtocol().sendBConfig(674, player.getLocation().x << 14 | player.getLocation().y | player.getLocation().z << 28);
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 44:
				getPlayer().getPanes().remove(this);
				break;
		}
		
	}
}
