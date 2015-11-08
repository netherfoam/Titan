package org.maxgamer.rs.interfaces.impl.primary;

import org.maxgamer.rs.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

public class SmithingInterface extends PrimaryInterface {
	public static final int INTERFACE_ID = 300;
	public static final int MAX_ITEMS = 30;
	
	private static int[] CHILD_IDS = new int[MAX_ITEMS];
	static{
		//Credits: Dementhium
        int counter = 18;
        for (int i = 0; i < CHILD_IDS.length; i++) {
            if (counter == 250) {
                counter = 267;
            }
            CHILD_IDS[i] = counter;
            counter += 8;
        }
	}
	
	private ItemStack[] items = new ItemStack[MAX_ITEMS];
	
	public SmithingInterface(Player p) {
		super(p);
		setChildId(INTERFACE_ID);
	}
	
	public void setItem(int pos, ItemStack item){
		items[pos] = item;
		
		if(isVisible()){
			getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[pos], item);
		}
	}
	
	@Override
	public void onOpen(){
		for(int i = 0; i < items.length; i++){
			getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[i], items[i]);
		}
	}

	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		
	}
}
