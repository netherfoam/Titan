package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SettingsBuilder;
import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.interfaces.impl.primary.TradeInterface;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class TradeSideInterface extends SideInterface {
	private static final SettingsBuilder INV_OPTS = new SettingsBuilder();
	
	static {
		INV_OPTS.setSecondaryOption(0, true);
		INV_OPTS.setSecondaryOption(1, true);
		INV_OPTS.setSecondaryOption(2, true);
		INV_OPTS.setSecondaryOption(3, true);
		INV_OPTS.setSecondaryOption(4, true);
		INV_OPTS.setSecondaryOption(5, true);
		INV_OPTS.setSecondaryOption(6, true);
		INV_OPTS.setSecondaryOption(9, true);
	}
	
	private Container mine;
	private TradeInterface trade;
	private ContainerListener listener;
	
	public TradeSideInterface(Player p, Container mine, TradeInterface trade) {
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 197 : 84));
		setChildId(336);
		if (mine == null) throw new NullPointerException("TradeSideInterface trade container may not be null");
		this.mine = mine;
		this.trade = trade;
	}
	
	@Override
	public void onOpen() {
		super.onOpen();
		getPlayer().getProtocol().invoke(150, "", "", "Lend", "Value<col=FF9040>", "Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0, 7, 4, 93, 336 << 16);
		setAccessMask(INV_OPTS.getValue(), 0, 27, 0);
		
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
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		Log.debug("Option: " + option);
		
		if (buttonId == 0) {
			ItemStack item = getPlayer().getInventory().get(slotId);
			if (item == null) {
				getPlayer().getCheats().log(5, "Player attempted to interact with a NULL item in their inventory.");
				return;
			}
			
			switch (option) {
				case 5: //Value TODO
					return;
				case 9: //Examine
					getPlayer().sendMessage(item.getExamine());
				case 6: //Lend
					return; //TODO
				case 0: // Offer 1
					item = item.setAmount(1);
					break;
				case 1: // Offer 5
					item = item.setAmount(1);
					break;
				case 2: // Offer 10
					item = item.setAmount(10);
					break;
				case 3: // Offer All
					item = item.setAmount(getPlayer().getInventory().getNumberOf(item));
					break;
				case 4: //Offer X
					final ItemStack stack = item;
					//getPlayer().getProtocol().request("Offer how many?", new IntRequest() {
					getPlayer().getWindow().open(new IntRequestInterface(player, "Offer how many?") {
						@Override
						public void onInput(long value) {
							ItemStack item = stack.setAmount(Math.min(getPlayer().getInventory().getNumberOf(stack), value));
							if (item == null) { //Will become null if amount <= 0
								return;
							}
							
							ContainerState inv = getPlayer().getInventory().getState();
							ContainerState min = mine.getState();
							
							try {
								inv.remove(item);
								min.add(item);
							}
							catch (ContainerException e) {
								getPlayer().sendMessage("Not enough space!");
								return;
							}
							
							if (trade.isAccepted()) {
								trade.setAccepted(false);
							}
							
							TradeInterface other = trade.getPartnerInterface();
							if (other != null && other.isAccepted()) {
								other.setAccepted(false);
							}
							
							inv.apply();
							min.apply();
						}
					});
					return;
			}
			
			long amount = Math.min(item.getAmount(), getPlayer().getInventory().getNumberOf(item));
			if (amount <= 0) {
				return; //Odd, could occur if the player has a 0 item stack somehow
			}
			
			item = item.setAmount(amount);
			
			ContainerState inv = getPlayer().getInventory().getState();
			ContainerState min = mine.getState();
			
			try {
				inv.remove(item);
				min.add(item);
			}
			catch (ContainerException e) {
				getPlayer().sendMessage("Not enough space!");
				return;
			}
			
			if (trade.isAccepted()) {
				trade.setAccepted(false);
			}
			
			TradeInterface other = trade.getPartnerInterface();
			if (other != null && other.isAccepted()) {
				other.setAccepted(false);
			}
			
			inv.apply();
			min.apply();
			return;
		}
	}
}