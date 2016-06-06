package org.maxgamer.rs.model.item.vendor;

import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;
import org.maxgamer.rs.model.item.inventory.StackType;

/**
 * Represents a vendor's stock, which has a few unique properties:<br>
 * - The maximum number of items is 40,<br>
 * - Items the container starts with are normalized to their original amounts
 * over time,<br>
 * - Items the container starts with still show up if their amount is 0,<br>
 * - Items not in the default stock are slowly removed from the container
 * @author netherfoam
 */
public class VendorContainer extends Container {
	/**
	 * The owning vendor
	 */
	private Vendor vendor;

	/**
	 * The items currently available in this vendor
	 */
	private ItemStack[] items = new ItemStack[40];
	
	/**
	 * The default quantity of items in this shop. The stock of these items will
	 * normalize towards these numbers.
	 */
	private long[] defaults;

	/**
	 * Constructs a new VendorContainer, with the given item array as the
	 * default stock for the shop. Over time, the items in this shop will be
	 * normalized towards the amount specified in the array. If the amount for
	 * items in the array drops to 0, they will not be removed from stock but
	 * will instead display 0 amount.
	 * @param vendor The vendor that owns this container
	 * @throws IllegalArgumentException if there are more than 40 default stock
	 *         items
	 * @throws NullPointerException if there are any null values in the given
	 *         stock
	 */
	public VendorContainer(Vendor vendor) {
		super(StackType.ALWAYS);
		this.vendor = vendor;

		defaults = new long[this.vendor.getItems().size()];
		if (defaults.length > items.length) {
			throw new IllegalArgumentException("A shop may only have 40 items at most for sale at any point. Requested a stock size of " + defaults.length + " items");
		}
		
		for (int i = 0; i < this.vendor.getItems().size(); i++) {
			if (this.vendor.getItems().get(i) == null) {
				throw new NullPointerException("Stock given at index " + i + " is null.");
			}
			items[i] = this.vendor.getItems().get(i).toItem();
			defaults[i] = this.vendor.getItems().get(i).getAmount();
		}
	}
	
	/**
	 * The specified {@code buyer} buys the specified {@code toBuy} from the
	 * {@code slot} of this {@code VendorContainer}.
	 * 
	 * @param buyer the mob buying the item
	 * @param toBuy the item being bought
	 * @param slot the slot being bought from
	 * @return true if the item was bought; return false otherwise
	 */
	public boolean buy(Mob buyer, ItemStack toBuy, int slot){
		long amount = Math.min(toBuy.getAmount(), getNumberOf(toBuy));
		if (amount <= 0) {
			buyer.sendMessage("The vendor is out of stock.");
			return false;
		}
		
		toBuy = toBuy.setAmount(amount);
		if (buyer instanceof InventoryHolder) {
			ContainerState inv = ((InventoryHolder) buyer).getInventory().getState();
			ContainerState ven = getState();
			
			try {
				ItemStack cost = ItemStack.create(vendor.getCurrency().getId(), toBuy.getAmount() * toBuy.getDefinition().getHighAlchemy());
				if (cost != null) inv.remove(cost);
				
				ven.remove(slot, toBuy);
				inv.add(toBuy);
			}
			catch (ContainerException e) {
				return false;
			}
			
			//We know we were successful here.
			ven.apply();
			inv.apply();
			return true;
		}
		return false;
	}
	
	public boolean sell(Mob seller, ItemStack toSell, int slot) {
		if (seller instanceof InventoryHolder) {
			Container inventory = ((InventoryHolder) seller).getInventory();
			long amount = Math.min(toSell.getAmount(), inventory.getNumberOf(toSell));
			if (amount <= 0) {
				//This is bizarre, they have tried to sell an item with 0 amount and not through a hack.
				seller.sendMessage("You don't have enough of those.");
				return false;
			}
			toSell = toSell.setAmount(amount);
			
			ItemStack price = ItemStack.create(vendor.getCurrency().getId(), toSell.getAmount() * toSell.getDefinition().getLowAlchemy());
			if (price == null) {
				seller.sendMessage("That's not worth anything.");
				return false;
			}
			
			ContainerState inv = inventory.getState();
			ContainerState vend = getState();
			
			try {
				vend.add(toSell);
				inv.remove(slot, toSell);
				inv.add(price);
			}
			catch (ContainerException e) {
				return false;
			}
			
			//We know the transaction succeeded here
			vend.apply();
			inv.apply();
			return true;
		}
		return false;
	}
	
	@Override
	protected void setItem(int slot, ItemStack item) {
		if (item == null && slot < defaults.length) {
			ItemStack old = this.items[slot];
			//This item will be regenerated later.
			
			item = ItemStack.createEmpty(old.getId(), old.getHealth());
		}
		
		items[slot] = item;
	}
	
	@Override
	public ItemStack get(int slot) {
		return items[slot];
	}
	
	@Override
	public int getSize() {
		return items.length;
	}
	
	/**
	 * Normalizes this shop by restocking/destocking appropriately towards the
	 * correct number of items that should be in stock.
	 */
	public void restock() {
		for (int i = 0; i < defaults.length; i++) {
			ItemStack item = items[i];
			if (item.getAmount() == defaults[i]) continue;
			if (item.getAmount() < defaults[i]) {
				//Restock
				item = item.setAmount(item.getAmount() + 1);
				this.set(i, item);
			}
			else if (item.getAmount() > defaults[i]) {
				//Destock
				//Use ItemStack.create() in-case our default amount is 0 for some reason.
				item = ItemStack.create(item.getId(), item.getAmount() - 1, item.getHealth());
				this.set(i, item);
			}
		}
		
		for (int i = defaults.length; i < items.length; i++) {
			ItemStack item = items[i];
			if (item.getAmount() > 1) {
				item = item.setAmount(item.getAmount() - 1);
				this.set(i, item);
			}
			else {
				this.set(i, null);
			}
		}
	}
}