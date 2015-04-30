package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SettingsBuilder;
import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.ContainerState;
import org.maxgamer.rs.model.item.inventory.VendorContainer;

/**
 * @author netherfoam
 */
public class VendorSideInterface extends SideInterface {
	private static final SettingsBuilder INTERFACE_SETTINGS = new SettingsBuilder();
	static {
		INTERFACE_SETTINGS.setSecondaryOption(0, true);
		INTERFACE_SETTINGS.setSecondaryOption(1, true);
		INTERFACE_SETTINGS.setSecondaryOption(2, true);
		INTERFACE_SETTINGS.setSecondaryOption(3, true);
		INTERFACE_SETTINGS.setSecondaryOption(4, true);
		INTERFACE_SETTINGS.setSecondaryOption(9, true);
		INTERFACE_SETTINGS.setUseOnSettings(false, false, false, false, false, false);
		INTERFACE_SETTINGS.setInterfaceDepth(1);
	}
	
	private VendorContainer vendor;
	private ContainerListener listener;
	
	public VendorSideInterface(Player p, VendorContainer vendor) {
		super(p, (short) 621, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 197 : 84));
		this.vendor = vendor;
	}
	
	@Override
	public void onOpen() {
		super.onOpen();
		
		//TODO: There are a few unknowns, but
		// 621 = inventory item interface
		// 93 = location of inventory interface
		// 4 = 'shop inventory' container id
		// 7 = number of rows in inventory
		// 4 = number of columns in inventory
		// 1 = ??, -1 = ??
		getPlayer().getProtocol().invoke(149, "Sell 50", "Sell 10", "Sell 5", "Sell 1", "Value", -1, 1, 7, 4, 93, getChildId() << 16);
		setAccessMask(INTERFACE_SETTINGS.getValue(), 0, 27, 0);
		
		for (int i = 0; i < this.getPlayer().getInventory().getSize(); i++) {
			ItemStack item = getPlayer().getInventory().get(i);
			if (item == null) continue; //We only show items we have. TODO: This could be optimized by sending an array of slots and items at once
			getPlayer().getProtocol().setItem(93, false, item, i);
		}
		
		this.listener = new ContainerListener() {
			@Override
			public void onSet(Container c, int slot, ItemStack old) {
				assert (isVisible()); //Should always be true if called
				getPlayer().getProtocol().setItem(93, false, c.get(slot), slot);
			}
		};
		getPlayer().getInventory().addListener(this.listener);
	}
	
	@Override
	public void onClose() {
		super.onClose();
		
		//We hide all items we sent them. TOOD: This could be optimized by sending an array of slots and items at once
		for (int i = 0; i < this.getPlayer().getInventory().getSize(); i++) {
			ItemStack item = getPlayer().getInventory().get(i);
			if (item != null) continue;
			getPlayer().getProtocol().setItem(93, false, null, i);
		}
		getPlayer().getInventory().removeListener(this.listener);
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		Log.debug("SideShopInterface clicked, option " + option);
		if (buttonId == 0) {
			ItemStack item = getPlayer().getInventory().get(slotId);
			
			if (item == null) {
				getPlayer().getCheats().log(10, "Player attempted to interact with NULL item in vendor inventory interface");
				return;
			}
			
			switch (option) {
				case 0: //Info
					//TODO
					return;
				case 9: //Examine
					getPlayer().sendMessage(item.getExamine());
					return;
				case 1: //1x
					item = item.setAmount(1);
					break;
				case 2: //5x
					item = item.setAmount(5);
					break;
				case 3: //10x
					item = item.setAmount(10);
					break;
				case 4: //50x
					item = item.setAmount(50); //TODO: Make this 'Sell X'
					break;
			}
			
			long amount = Math.min(item.getAmount(), getPlayer().getInventory().getNumberOf(item));
			if (amount <= 0) {
				//This is bizare, they have tried to sell an item with 0 amount and not through a hack.
				getPlayer().sendMessage("You don't have enough of those.");
				return;
			}
			item = item.setAmount(amount);
			
			ItemStack price = ItemStack.create(vendor.getCurrency(), item.getAmount() * item.getDefinition().getLowAlchemy());
			if (price == null) {
				getPlayer().sendMessage("That's not worth anything.");
				return;
			}
			
			ContainerState inv = getPlayer().getInventory().getState();
			ContainerState vend = vendor.getState();
			
			try {
				inv.remove(slotId, item);
				inv.add(price);
				vend.add(item);
			}
			catch (ContainerException e) {
				getPlayer().sendMessage("Transaction failed.");
				return;
			}
			
			//We know the transaction succeeded here
			vend.apply();
			inv.apply();
		}
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
}