package org.maxgamer.rs.interfaces.impl.chat;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public abstract class GESearchInterface extends Interface {
	public static final short INTERFACE_ID = (short) 389;
	
	public GESearchInterface(Player p) {
		//super(p, INTERFACE_ID);
		//This is probably because we're invoking the wrong script, it seems like we're invoking the script
		//for the fixed-screen size. It appears to work with both fixed and resizable game panes though. Normal
		//dialogue interfaces use 13 or 7 for resizable or fixed screens, but this only uses 7. It is an abnormality
		//because we open this interface with a script. This is why we extend Interface and not DialogueInterface.
		super(p, p.getWindow().getInterface(ChatInterface.CHILD_ID), (short) 7, true);
		setChildId(INTERFACE_ID);
	}
	
	@Override
	public void onOpen() {
		getPlayer().getProtocol().sendBConfig(1113, 0);
		
		getPlayer().getProtocol().sendBConfig(1111, 1); //Price?
		getPlayer().getProtocol().sendBConfig(1112, 0);
		getPlayer().getProtocol().sendBConfig(1113, 0);
		super.onOpen();
		
		//GE Search script
		getPlayer().getProtocol().invoke(570, "Grand Exchange Item Search");
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		if (buttonId == 10 && option == 0) {
			getPlayer().getWindow().close(this);
			return;
		}
	}
	
	public abstract void onSelect(ItemStack item);
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
}