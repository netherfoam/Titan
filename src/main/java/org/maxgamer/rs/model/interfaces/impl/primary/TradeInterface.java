package org.maxgamer.rs.model.interfaces.impl.primary;

import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.interfaces.SettingsBuilder;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.interfaces.impl.side.TradeSideInterface;
import org.maxgamer.rs.util.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class TradeInterface extends PrimaryInterface implements ContainerListener {
	/**
	 * The clientside container ID for the trade interface
	 */
	public static final int TRADE_CONTAINER_ID = 90;
	
	/**
	 * The client settings to modify the options on 'my' side of the trade
	 * window (Eg remove options)
	 */
	private static final SettingsBuilder MINE_OPTS = new SettingsBuilder();
	
	/**
	 * The client settings to modify the options on 'your' side of the trade
	 * window (Eg examine, value)
	 */
	private static final SettingsBuilder YOURS_OPTS = new SettingsBuilder();
	
	static {
		MINE_OPTS.setSecondaryOption(0, true);
		MINE_OPTS.setSecondaryOption(1, true);
		MINE_OPTS.setSecondaryOption(2, true);
		MINE_OPTS.setSecondaryOption(3, true);
		MINE_OPTS.setSecondaryOption(4, true);
		MINE_OPTS.setSecondaryOption(5, true);
		MINE_OPTS.setSecondaryOption(9, true);
		
		YOURS_OPTS.setSecondaryOption(0, true);
		YOURS_OPTS.setSecondaryOption(9, true);
	}
	
	/**
	 * My side of the trade window
	 */
	private Container mine;
	
	/**
	 * Your side of the trade window
	 */
	private Container yours;
	
	/**
	 * The player we're trading with
	 */
	private Player partner;
	
	/**
	 * True if we've accepted, false otherwise
	 */
	private boolean accept;
	
	/**
	 * The inventory interface we use to offer items
	 */
	private TradeSideInterface side;
	
	public TradeInterface(Player p, Container mine, Container yours, Player partner) {
		super(p);
		setChildId(335);
		if (mine == null) throw new NullPointerException();
		if (yours == null) throw new NullPointerException();
		if (partner == null) throw new NullPointerException();
		
		this.mine = mine;
		this.yours = yours;
		this.partner = partner;
	}
	
	@Override
	public void onOpen() {
		mine.addListener(this);
		yours.addListener(this);
		
		for (int i = 0; i < mine.getSize(); i++) {
			ItemStack item = mine.get(i);
			getPlayer().getProtocol().setItem(TRADE_CONTAINER_ID, false, item, i);
		}
		
		for (int i = 0; i < yours.getSize(); i++) {
			ItemStack item = yours.get(i);
			getPlayer().getProtocol().setItem(TRADE_CONTAINER_ID, true, item, i);
		}
		
		super.onOpen();
		getPlayer().getProtocol().invoke(150, "", "", "", "Value<col=FF9040>", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, TRADE_CONTAINER_ID, 335 << 16 | 31);
		setAccessMask(MINE_OPTS.getValue(), 0, 27, 31);
		
		getPlayer().getProtocol().invoke(695, "", "", "", "", "", "", "", "", "Value<col=FF9040>", -1, 0, 7, 4, TRADE_CONTAINER_ID, 335 << 16 | 34);
		setAccessMask(YOURS_OPTS.getValue(), 0, 27, 34);
		
		setString(15, "Trading with " + partner.getName());
		setString(22, partner.getName());
		
		this.side = new TradeSideInterface(getPlayer(), mine, this);
		getPlayer().getWindow().open(side);
		
		this.setString(37, "");
	}
	
	@Override
	public void onClose() {
		super.onClose();
		
		if (mine != null) mine.removeListener(this);
		if (yours != null) yours.removeListener(this);
		getPlayer().getWindow().close(side);
		
		if (mine != null) {
			try {
				getPlayer().getInventory().add(mine.getContents());
			}
			catch (ContainerException e) {
				getPlayer().sendMessage("Failed to return your trade offer to your inventory. It will be put in your bank given enough space.");
				getPlayer().getLostAndFound().add(mine.getContents());
			}
		}
	}
	
	public void setAccepted(boolean accept) {
		TradeInterface other = getPartnerInterface();
		if (other == null) {
			Log.info("During trade, partner appears to have no trade interface. Player: " + getPlayer() + ", Partner: " + partner);
			return;
		}
		
		if (accept && other.accept) {
			//Generally this won't be displayed
			other.setString(37, "Waiting...");
			this.setString(37, "Waiting...");
		}
		else if (accept && !other.accept) {
			this.setString(37, "Waiting for other player...");
			other.setString(37, "The other player has accepted");
		}
		else if (!accept && other.accept) {
			this.setString(37, "The other player has accepted");
			other.setString(37, "Waiting for other player...");
		}
		else if (!accept && !other.accept) {
			other.setString(37, "");
			this.setString(37, "");
		}
		this.accept = accept;
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	public boolean isAccepted() {
		return accept;
	}
	
	public TradeInterface getPartnerInterface() {
		Interface otherInterf = partner.getWindow().getInterface(this.getChildId());
		if (otherInterf instanceof TradeInterface == false) {
			//Somehow the partner's trade interface was removed and didn't notify us.
			return null;
		}
		TradeInterface other = (TradeInterface) otherInterf;
		return other;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		//Button 16 = Accept
		//Button 31 = My Items
		//Button 34 = Your items
		//Button 32 = Close
		//Button 18 = Decline
		//Button 12 = Close
		final TradeInterface other = getPartnerInterface();
		if (other == null) {
			player.getWindow().close(this);
			return;
		}
		
		if (buttonId == 16) {
			//Accept
			if (isAccepted() == false) {
				if (getPlayer().getInventory().hasRoom(yours.getContents()) == false) {
					//Ensure we have enough room for the player to hold the traded items
					getPlayer().sendMessage("You'll need more space to accept that.");
					return;
				}
				setAccepted(true);
				
				if (other.isAccepted() == true) {
					//Next stage interface
					TradeConfirmInterface confirmMine = new TradeConfirmInterface(player, mine, yours, partner);
					TradeConfirmInterface confirmYours = new TradeConfirmInterface(other.player, other.mine, other.yours, other.partner);
					
					mine.removeListener(this);
					yours.removeListener(this);
					other.mine.removeListener(other);
					other.yours.removeListener(this);
					
					//When we close the interface, we do not want to return the items.
					mine = null;
					yours = null;
					other.mine = null;
					other.yours = null;
					
					//This calls close() on us
					partner.getWindow().close(other);
					getPlayer().getWindow().close(this);
					
					getPlayer().getWindow().open(confirmMine);
					other.getPlayer().getWindow().open(confirmYours);
				}
			}
			return;
		}
		
		if (buttonId == 31) {
			ItemStack item = mine.get(slotId);
			
			if (item == null) {
				getPlayer().getCheats().log(5, "Player attempted to interact with NULL item in trade interface");
				return;
			}
			
			switch (option) {
				case 5: //Value
					return; //TODO
				case 9: //Examine
					getPlayer().sendMessage(item.getExamine());
					return;
				case 0: //Remove 1
					item = item.setAmount(1);
					break;
				case 1: //Remove 5
					item = item.setAmount(5);
					break;
				case 2: //Remove 10
					item = item.setAmount(10);
					break;
				case 3: //Remove All
					item = item.setAmount(mine.getNumberOf(item));
					break;
				case 4: //Remove X
					//TODO
					final ItemStack stack = item;
					getPlayer().getWindow().open(new IntRequestInterface(getPlayer(), "Remove how many?") {
						@Override
						public void onInput(long value) {
							long amount = Math.min(mine.getNumberOf(stack), value);
							if (amount <= 0) {
								return; //Possible, but peculiar
							}
							
							ItemStack item = stack.setAmount(amount);
							
							ContainerState inv = getPlayer().getInventory().getState();
							ContainerState offer = mine.getState();
							
							try {
								offer.remove(item);
								inv.add(item);
							}
							catch (ContainerException e) {
								getPlayer().sendMessage("Not enough room.");
								return;
							}
							
							offer.apply();
							inv.apply();
							
							if (isAccepted()) {
								setAccepted(false);
							}
							
							if (other.isAccepted()) {
								other.setAccepted(false);
							}
						}
					});
					return;
			}
			
			long amount = Math.min(mine.getNumberOf(item), item.getAmount());
			if (amount <= 0) {
				return; //Possible, but peculiar
			}
			
			item = item.setAmount(amount);
			
			ContainerState inv = getPlayer().getInventory().getState();
			ContainerState offer = mine.getState();
			
			try {
				offer.remove(item);
				inv.add(item);
			}
			catch (ContainerException e) {
				getPlayer().sendMessage("Not enough room.");
				return;
			}
			
			offer.apply();
			inv.apply();
			
			if (isAccepted()) {
				setAccepted(false);
			}
			
			if (other.isAccepted()) {
				other.setAccepted(false);
			}
			
			return;
		}
		
		if (buttonId == 34) {
			ItemStack item = yours.get(slotId);
			if (item == null) {
				getPlayer().getCheats().log(5, "Player attempted to interact with NULL item in trade interface");
				return;
			}
			
			switch (option) {
				case 0: //Value
					return; //TODO
				case 9: //Examine
					getPlayer().sendMessage(item.getExamine());
					return;
			}
		}
		
		//Close via 'X' (32) or Decline (18)
		if (buttonId == 12 || buttonId == 18) {
			partner.getWindow().close(other);
			getPlayer().getWindow().close(this);
			return;
		}
	}
	
	@Override
	public void onSet(Container c, int slot, ItemStack old) {
		ItemStack item = c.get(slot);
		if (c == mine) {
			getPlayer().getProtocol().setItem(TRADE_CONTAINER_ID, false, item, slot);
		}
		else if (c == yours) {
			getPlayer().getProtocol().setItem(TRADE_CONTAINER_ID, true, item, slot);
		}
	}
}