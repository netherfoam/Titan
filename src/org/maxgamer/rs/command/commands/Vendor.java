package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.impl.primary.VendorInterface;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;
import org.maxgamer.rs.model.item.vendor.VendorContainer;

/**
 * @author netherfoam
 */
@CmdName(names = { "shop" })
public class Vendor implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		ItemStack[] items = new ItemStack[40];
		for (int i = 0; i < items.length; i++) {
			try {
				items[i] = ItemStack.create(Erratic.nextInt(0, 10000), 100);
			}
			catch (Exception e) {
				items[i] = ItemStack.create(995);
			}
		}
		VendorContainer shop = new VendorContainer("Admin-Shop", 0, items, 995) {
			
			@Override
			public boolean buy(Mob buyer, ItemStack toBuy, int slot) {
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
						ItemStack cost = ItemStack.create(getCurrency(), toBuy.getAmount() * toBuy.getDefinition().getHighAlchemy());
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
			
			@Override
			public boolean sell(Mob seller, ItemStack toSell, int slot) {
				if (seller instanceof InventoryHolder) {
					Container inventory = ((InventoryHolder) seller).getInventory();
					long amount = Math.min(toSell.getAmount(), inventory.getNumberOf(toSell));
					if (amount <= 0) {
						//This is bizare, they have tried to sell an item with 0 amount and not through a hack.
						seller.sendMessage("You don't have enough of those.");
						return false;
					}
					toSell = toSell.setAmount(amount);
					
					ItemStack price = ItemStack.create(getCurrency(), toSell.getAmount() * toSell.getDefinition().getLowAlchemy());
					if (price == null) {
						seller.sendMessage("That's not worth anything.");
						return false;
					}
					
					ContainerState inv = inventory.getState();
					ContainerState vend = getState();
					
					try {
						System.out.println(toSell);
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
		};
		player.getWindow().open(new VendorInterface(player, shop));
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}