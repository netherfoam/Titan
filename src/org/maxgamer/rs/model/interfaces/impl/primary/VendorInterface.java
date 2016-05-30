package org.maxgamer.rs.model.interfaces.impl.primary;

import org.maxgamer.rs.model.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.interfaces.SettingsBuilder;
import org.maxgamer.rs.model.interfaces.impl.side.VendorSideInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.vendor.VendorContainer;

/**
 * @author netherfoam
 */
public class VendorInterface extends PrimaryInterface implements ContainerListener {
	private static final int SHOP_ITEM_PRICE_BCONFIG_OFFSE = 946;
	private static final int SHOP_INVENTORY_ID = 4;
	
	private static final SettingsBuilder SETTINGS = new SettingsBuilder();
	static {
		SETTINGS.setSecondaryOption(0, true);
		SETTINGS.setSecondaryOption(1, true);
		SETTINGS.setSecondaryOption(2, true);
		SETTINGS.setSecondaryOption(3, true);
		SETTINGS.setSecondaryOption(4, true);
		SETTINGS.setSecondaryOption(5, true);
		SETTINGS.setSecondaryOption(9, true);
		SETTINGS.setUseOnSettings(false, false, false, false, false, false);
		SETTINGS.setInterfaceDepth(0);
	}
	
	private VendorContainer vendor;
	private VendorSideInterface side;
	
	public VendorInterface(Player p, VendorContainer vendor) {
		super(p);
		setChildId(620);
		this.vendor = vendor;
	}
	
	@Override
	public void onOpen() {
		getPlayer().getProtocol().sendConfig(118, 4);
		getPlayer().getProtocol().sendConfig(1496, -1); //Number of free items, or something? Index of end of free items? (Eg splitter value?)
		getPlayer().getProtocol().sendConfig(532, vendor.getCurrency()); //Currency ID
		super.onOpen();
		
		setAccessMask(SETTINGS.getValue(), 0, 12, 26);
		setAccessMask(SETTINGS.getValue(), 0, 240, 25); //Slot ID's are 0, 6, 12, etc. 240/6 = 40 = max size of vendor.
		
		//A beasty for another day.
		//getPlayer().getProtocol().invoke(VendorSideInterface.RIGHT_CLICK_OPTIONS, "Sell 50", "Sell 10", "Sell 5", "Sell 1", "Value", 25, 1, 5, 9, SHOP_INVENTORY_ID, (getChildId() << 16) | 25); //maybe | 25
		
		this.vendor.addListener(this);
		for (int i = 0; i < vendor.getSize(); i++) { //Vendor max size is 40
			ItemStack item = vendor.get(i);
			if (item == null) continue;
			
			getPlayer().getProtocol().sendBConfig(SHOP_ITEM_PRICE_BCONFIG_OFFSE + i, item.getDefinition().getHighAlchemy());
			getPlayer().getProtocol().setItem(SHOP_INVENTORY_ID, false, item, i);
		}
		getPlayer().getProtocol().sendContainer(555, false, player.getInventory());
		side = new VendorSideInterface(getPlayer(), vendor);
		getPlayer().getWindow().open(side);
	}
	
	@Override
	public void onClose() {
		super.onClose();
		
		//We hide all items we sent them. TODO: This could be optimized by sending an array of slots and items at once
		for (int i = 0; i < this.vendor.getSize(); i++) {
			ItemStack item = this.vendor.get(i);
			if (item != null) continue;
			getPlayer().getProtocol().setItem(SHOP_INVENTORY_ID, false, null, i);
		}
		
		this.vendor.removeListener(this);
		getPlayer().getWindow().close(side);
	}
	
	public VendorContainer getVendor() {
		return vendor;
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		if (buttonId == 18 && option == 0) {
			getPlayer().getWindow().close(this); //close() is invoked
			return;
		}
		
		if (buttonId == 25) { //Clicking on an item inside the vendor
			//TODO: Why is this necessary? What information are we destroying?
			slotId /= 6;
			ItemStack item = vendor.get(slotId);
			if (item == null) {
				getPlayer().getCheats().log(10, "Player attempted to interact with null shop item");
				return;
			}
			
			switch (option) {
				case 9: //Examine
					getPlayer().sendMessage(item.getExamine());
					return;
				case 0: //Info
					//TODO
					return;
				case 1: //Buy 1
					item = item.setAmount(1);
					break;
				case 2: //Buy 5
					item = item.setAmount(5);
					break;
				case 3: //Buy 10
					item = item.setAmount(10);
					break;
				case 4: //Buy 50
					item = item.setAmount(50);
					break;
				case 5: //Buy 500
					item = item.setAmount(500);
					break;
			}
			//If we made it here, they're attempting to buy form the shop.
			if (!vendor.buy(player, item, slotId)) {
				player.sendMessage("Transaction failed!");
				return;
			}
		}
	}
	
	@Override
	public void onSet(Container c, int slot, ItemStack old) {
		ItemStack item = c.get(slot);
		getPlayer().getProtocol().setItem(SHOP_INVENTORY_ID, false, item, slot);
		if (item != null) getPlayer().getProtocol().sendBConfig(SHOP_ITEM_PRICE_BCONFIG_OFFSE + slot, item.getDefinition().getHighAlchemy());
	}
}