package org.maxgamer.rs.model.interfaces.impl.primary;

import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class TradeConfirmInterface extends PrimaryInterface implements ContainerListener {
	private Container mine;
	private Container yours;
	private Player partner;
	private boolean accept = false;
	
	public TradeConfirmInterface(Player p, Container mine, Container yours, Player partner) {
		super(p);
		setChildId(334);
		this.mine = mine;
		this.yours = yours;
		this.partner = partner;
	}
	
	@Override
	public void onOpen() {
		this.setString(54, "<col=00FFFF>Trading with:<br><col=00FFFF>?");
		this.setString(34, "Are you sure you want to make this trade?");
		super.onOpen();
		
		mine.addListener(this);
		yours.addListener(this);
	}
	
	@Override
	public void onClose() {
		super.onClose();
		
		if (mine != null) {
			mine.removeListener(this);
			try {
				getPlayer().getInventory().add(mine.getContents());
			}
			catch (ContainerException e) {
				getPlayer().sendMessage("Failed to add items to inventory. Items will be added to your bank when it has space.");
				getPlayer().getLostAndFound().add(mine.getContents());
			}
		}
		if (yours != null) yours.removeListener(this);
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	public boolean isAccepted() {
		return accept;
	}
	
	public TradeConfirmInterface getPartnerInterface() {
		Interface otherInterf = partner.getWindow().getInterface(this.getChildId());
		if (otherInterf instanceof TradeConfirmInterface == false) {
			//Somehow the partner's trade interface was removed and didn't notify us.
			return null;
		}
		TradeConfirmInterface other = (TradeConfirmInterface) otherInterf;
		return other;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		// Button 21 = Accept
		// Button 22 = Decline
		// Button  6 = 'X'
		
		TradeConfirmInterface other = getPartnerInterface();
		if (other == null) {
			player.getWindow().close(this);
			return;
		}
		
		if (buttonId == 21) {
			this.accept = true;
			
			if (other.isAccepted() == false) {
				this.setString(34, "Waiting for other player...");
				other.setString(34, "The other player has accepted");
				return;
			}
			else {
				//Trade completed
				ContainerState otherInv = other.getPlayer().getInventory().getState();
				ContainerState myInv = this.getPlayer().getInventory().getState();
				
				try {
					otherInv.add(mine.getContents());
					myInv.add(yours.getContents());
				}
				catch (ContainerException e) {
					other.getPlayer().sendMessage("Trade failed: Not enough room");
					this.getPlayer().sendMessage("Trade failed: Not enough room");
					return;
				}
				
				otherInv.apply();
				myInv.apply();
				
				other.mine = null;
				other.yours = null;
				
				mine = null;
				yours = null;
				
				other.getPlayer().getWindow().close(other);
				this.getPlayer().getWindow().close(this);
				
				return;
			}
		}
		
		//Decline (22) or 'X' (6)
		if (buttonId == 22 || buttonId == 6) {
			other.getPlayer().getWindow().close(other);
			this.getPlayer().getWindow().close(this);
			return;
		}
	}
	
	@Override
	public void onSet(Container c, int slot, ItemStack old) {
		//Although technically we can't change
		ItemStack item = c.get(slot);
		if (c == mine) {
			getPlayer().getProtocol().setItem(TradeInterface.TRADE_CONTAINER_ID, false, item, slot);
		}
		else if (c == yours) {
			getPlayer().getProtocol().setItem(TradeInterface.TRADE_CONTAINER_ID, true, item, slot);
		}
	}
}