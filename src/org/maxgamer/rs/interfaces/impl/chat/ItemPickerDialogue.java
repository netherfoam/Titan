package org.maxgamer.rs.interfaces.impl.chat;

import java.util.ArrayList;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.interfaces.impl.dialogue.Dialogue;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public abstract class ItemPickerDialogue extends Dialogue {
	private class AmountSelectorInterface extends Interface {
		private int amount = maxAmount;
		
		public AmountSelectorInterface() {
			super(ItemPickerDialogue.this.player, ItemPickerDialogue.this, (short) (ItemPickerDialogue.this.player.getSession().getScreenSettings().getDisplayMode() < 2 ? -1 : 4), true);
			setChildId(916);
		}
		
		@Override
		public boolean isServerSidedClose() {
			return ItemPickerDialogue.this.isServerSidedClose();
		}
		
		@Override
		public boolean isMobile() {
			return ItemPickerDialogue.this.isMobile();
		}
		
		@Override
		public void onClick(int option, int buttonId, int slotId, int itemId) {
			//btn=20 = amount--;
			if (buttonId == 20) amount--;
			//btn=19 = amount++;
			if (buttonId == 19) amount++;
			//btn=7 = amount = 10;
			if (buttonId == 7) amount = 10;
			//btn=6 = amount = 5;
			if (buttonId == 6) amount = 5;
			//btn=5 = amount = 1;
			if (buttonId == 1) amount = 1;
			
			if (amount > maxAmount) amount = maxAmount;
			else if (amount < 0) amount = 0;
		}
	}
	
	/**
	 * The global string ids which correspond to the name of the items. Eg, the
	 * icon for one item could be displayed but the name of another could be
	 * displayed.
	 */
	public static final int[] NAME_IDS = { 132, 133, 134, 135, 136, 137, 275, 316, 317, 318 };
	
	/**
	 * the global int "BConfig" ids that correspond to the item ID, used for
	 * icon of item.
	 */
	public static final int[] CONFIG_IDS = { 755, 756, 757, 758, 759, 760, 120, 185, 87, 90 };
	
	/**
	 * The maximum amount they can select
	 */
	private int maxAmount = 28;
	
	/**
	 * The items they can select from
	 */
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>(10);
	
	/**
	 * The sub interface used to control the amount they're selecting
	 */
	private AmountSelectorInterface amount;
	
	/**
	 * Constructs a new ItemPickerInterface. This interface allows the given
	 * player to choose an item from a list of items in the chatbox area up to a
	 * maximum of 10 items. They can modify the amount of items they'd like to
	 * choose, from 0 to the given maximum amount. The default amount is set to
	 * the maximum amount.
	 * @param p the player this interface is for
	 * @param maxAmount the maximum amount they can select.
	 * @param items the items they can choose from, maximum of 10 items.
	 */
	public ItemPickerDialogue(Player p, int maxAmount) {
		super(p);
		setChildId(905);
		if (maxAmount < 0) {
			throw new IllegalArgumentException("MaxAmount must be >= 0");
		}
		
		this.maxAmount = maxAmount;
		this.amount = new AmountSelectorInterface();
	}
	
	/**
	 * Adds the given item as an option for this item picker dialogue
	 * @param item the item to add as an option
	 */
	public void add(ItemStack item){
		if(items.size() >= NAME_IDS.length) throw new IllegalStateException("ItemPickerDialogue may only contain up to 10 items!");
		items.add(item);
		
		if(this.isVisible()){
			getPlayer().getProtocol().sendBConfig(CONFIG_IDS[items.size() - 1], item.getId());
			getPlayer().getProtocol().sendGlobalString(NAME_IDS[items.size() - 1], item.getName());
		}
	}
	
	/**
	 * Removes the given item as an option for this item picker dialogue
	 * @param item the item to remove as an option
	 */
	public void remove(ItemStack item){
		items.remove(item);
		
		if(this.isVisible()){
			getPlayer().getProtocol().sendBConfig(CONFIG_IDS[items.size() - 1], 0);
			getPlayer().getProtocol().sendGlobalString(NAME_IDS[items.size() - 1], "");
		}
	}

	
	@Override
	public void onOpen() {
		super.onOpen();
		this.amount.setString(1, "Pick an item");
		getPlayer().getWindow().open(this.amount);
		
		getPlayer().getProtocol().sendBConfig(754, 13); //Hmm?
		
		for (int i = 0; i < items.size(); i++) {
			ItemStack item = items.get(i);
			getPlayer().getProtocol().sendBConfig(CONFIG_IDS[i], item.getId());
			getPlayer().getProtocol().sendGlobalString(NAME_IDS[i], item.getName());
		}
		
		//Sets the maximum amount the user can select
		getPlayer().getProtocol().sendConfig(1363, maxAmount << 20 | amount.amount << 26);
		
		//I don't think this interface should be click-through in that you can click 'Walk Here' through
		//the interface. I think an access mask changing the interface depth would fix it but I don't know
		//the interface offset and length (0 to n?) or the component id of the background
		/*
		 * SettingsBuilder s = new SettingsBuilder(); s.setSecondaryOption(0,
		 * true); setAccessMask(s.getValue(), 0, 20, 0);
		 */
	}
	
	@Override
	public void onClose() {
		super.onClose();
		if (this.amount.isVisible()) getPlayer().getWindow().close(this.amount);
	}
	
	@Override
	public boolean isMobile() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		// TODO Auto-generated method stub
		//Option 0 buttonId = 14
		//Option 1 buttonId = 15
		//Option 2 buttonId = 16
		ItemStack item;
		try {
			item = items.get(buttonId - 14);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			getPlayer().getCheats().log(2, "Attempted to select an item which does not exist");
			return;
		}
		
		try {
			item = item.setAmount(amount.amount);
			getPlayer().getWindow().close(this); //Close before, in case they want to ask the player for the next item or something
			this.pick(item);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Called when the player makes a selection. Not guaranteed to be called eg
	 * if the player moves away or logs out before clicking on the interface.
	 * @param item the item they selected, may be null if they selected a zero
	 *        amount
	 */
	public abstract void pick(ItemStack item);
}