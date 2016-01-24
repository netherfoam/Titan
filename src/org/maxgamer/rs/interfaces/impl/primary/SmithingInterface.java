package org.maxgamer.rs.interfaces.impl.primary;

import org.maxgamer.rs.interfaces.PrimaryInterface;
import org.maxgamer.rs.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

public abstract class SmithingInterface extends PrimaryInterface {
	public static final int POS_DAGGER = 0;
	public static final int POS_HATCHET = 1;
	public static final int POS_MACE = 2;
	public static final int POS_MED_HELM = 3;
	public static final int POS_CROSSBOW_BOLTS = 4;
	public static final int POS_SWORD = 5;
	public static final int POS_NAILS = 7;
	public static final int POS_ARROW_TIPS = 11;
	public static final int POS_SCIMITAR = 12;
	public static final int POS_CROSSBOW_LIMBS = 13;
	public static final int POS_LONGSWORD = 14;
	public static final int POS_THROWING_KNIFE = 15;
	public static final int POS_FULL_HELM = 16;
	public static final int POS_SQUARE_SHIELD = 17;
	public static final int POS_WARHAMMER = 20;
	public static final int POS_BATTLEAXE = 21;
	public static final int POS_CHAINBODY = 22;
	public static final int POS_KITESHIELD = 23;
	public static final int POS_2H_SWORD = 25;
	public static final int POS_PLATESKIRT = 26;
	public static final int POS_PLATELEGS = 27;
	public static final int POS_PLATEBODY = 28;
	
	public static final int INTERFACE_ID = 300;
	public static final int MAX_ITEMS = 30;
	public static final int MAKE_ALL = Integer.MAX_VALUE;
	
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
	
	public void set(int pos, ItemStack item){
		items[pos] = item;
		
		if(isOpen()){
			getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[pos], item);
		}
	}
	
	public ItemStack get(int pos){
		return items[pos];
	}
	
	@Override
	public void onOpen(){
		for(int i = 0; i < items.length; i++){
			getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[i], items[i]);
			// Intesting note for the future:
			// You can change the names of items from "mace" "sword" etc using this:
			// setString(CHILD_IDS[i]+1, items[i].getName());
			// You can change the costs of items from "1 Bar" "5 Bars" etc using this:
			// setString(CHILD_IDS[i]+2, "9001 Bars");
		}
	}

	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		final int pos = (buttonId - 21) / 8;
		int remainder = (buttonId - 21) % 8;
		
		if(items[pos] == null){
			getPlayer().getCheats().log(5, "Attempted to make an item from a NULL position in smithing interface");
			return;
		}
		
		if(remainder == 0){
			//Make all
			select(items[pos].setAmount(MAKE_ALL)); 
		}
		else if(remainder == 1){
			//Make X
			getPlayer().getWindow().open(new IntRequestInterface(getPlayer(), "How many would you like to make?") {
				@Override
				public void onInput(long value) {
					select(items[pos].setAmount(value));
				}
			});
		}
		else if(remainder == 2){
			//Make 5
			select(items[pos].setAmount(5));
		}
		else if(remainder == 3){
			//Make 1
			select(items[pos].setAmount(1));
		}
		
	}
	
	public abstract void select(ItemStack item);
}
