package org.maxgamer.rs.interfaces.impl.primary;

import org.maxgamer.rs.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author Albert Beaupre
 */
public class ItemsOnDeathInterface extends PrimaryInterface {

	public ItemsOnDeathInterface(Player p) {
		super(p);
		setChildId(102);
	}
	
	@Override
	public void onOpen() {
		Player p = this.getPlayer();
		p.getProtocol().sendBConfig(199, 442);
		p.getProtocol().sendAMask(211, 0, 2, 102, 18, 4);
		p.getProtocol().sendAMask(212, 0, 2, 102, 21, 42);
		//Object[] params = new Object[] { riskedWealth, carriedWealth, "", hasFamiliar ? 1 : 0, skulled ? 1 : 0, keptItems.getItemSlot(3), keptItems.getItemSlot(2), keptItems.getItemSlot(1), keptItems.getItemSlot(0), allowedItems, type };
		//ActionSender.sendClientScript(player, 118, params, "noooooobsll");
		//riskedWealth, carriedWealth, "", hasFamiliar ? 1 : 0, skulled ? 1 : 0, keptItems.getItemSlot(3), keptItems.getItemSlot(2), keptItems.getItemSlot(1), keptItems.getItemSlot(0), allowedItems, type
		
	}

	@Override
	public boolean isMobile() {
		return false;
	}

	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		
	}

}
