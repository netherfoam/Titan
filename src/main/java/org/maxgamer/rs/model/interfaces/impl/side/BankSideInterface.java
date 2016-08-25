package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.interfaces.SettingsBuilder;
import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.interfaces.impl.primary.BankInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class BankSideInterface extends SideInterface {
	private static SettingsBuilder SETTINGS = new SettingsBuilder();
	
	static {
		SETTINGS.setSecondaryOption(0, true);
		SETTINGS.setSecondaryOption(1, true);
		SETTINGS.setSecondaryOption(2, true);
		SETTINGS.setSecondaryOption(3, true);
		SETTINGS.setSecondaryOption(4, true);
		SETTINGS.setSecondaryOption(5, true);
		SETTINGS.setSecondaryOption(9, true);
		SETTINGS.setUseOnSettings(false, false, false, false, false, true);
		SETTINGS.setInterfaceDepth(1);
	}
	
	public BankSideInterface(Player p) {
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 197 : 84));
		setChildId(763);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return false;
	}
	
	@Override
	public void onOpen() {
		//We send this in BankInterface java, so we can assume it is already done. This is here for documentation purposes.
		//getPlayer().getProtocol().sendConfig(WITHDRAW_X_CONFIG, getPlayer().getConfig().getInt("input.bank"));
		
		setAccessMask(SETTINGS.getValue(), 0, 27, 0);
		super.onOpen();
	}
	
	//private void sendOptions(){
	//3rd last arg (4) is the number of items per-line to display on the inventory. Eg, changing it to 5 squishes it so that 5 items fit per row in the inventory
	//Assumably, that makes 4th last arg (7) the number of rows in the inventory. Eg, squish vertically
	//getPlayer().getProtocol().invoke(150, "", "", "", "Deposit-All", "Deposit-X", "Deposit-" + getPlayer().getConfig().getInt("input.bank"), "Deposit-10", "Deposit-5", "Deposit-1", -1, 0, 7, 4, 93, getChildId() << 16);
	//}
	
	@Override
	public void onClose() {
		super.onClose();
	}
	
	@Override
	public boolean isMobile() {
		return false;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		if (buttonId == 0) {
			ItemStack item = getPlayer().getInventory().get(slotId);
			if (item == null) {
				getPlayer().getCheats().log(10, "Player attempted to deposit NULL item from inventory");
				return;
			}
			
			switch (option) {
				case 9: //Examine
					getPlayer().sendMessage(item.getExamine());
					return;
				case 0: //1x
					item = item.setAmount(1);
					break;
				case 1: //5x
					item = item.setAmount(5);
					break;
				case 2: //10x
					item = item.setAmount(10);
					break;
				case 3: //(last)x
					item = item.setAmount(getPlayer().getConfig().getInt("input.bank", 0));
					break;
				case 4: //'X' x
					final ItemStack stack = item;
					getPlayer().getWindow().open(new IntRequestInterface(player, "Deposit how many?") {
						@Override
						public void onInput(long value) {
							ItemStack item = stack.setAmount(Math.min(getPlayer().getInventory().getNumberOf(stack), value));
							if (item == null) return;
							
							ContainerState inv = getPlayer().getInventory().getState();
							ContainerState bank = getPlayer().getBank().getState();
							
							try {
								inv.remove(item);
								if (item.isNoted()) item = item.getUnnoted();
								bank.add(item);
							}
							catch (ContainerException e) {
								getPlayer().sendMessage("There's not enough space!");
								return;
							}
							
							inv.apply();
							bank.apply();
							getPlayer().getConfig().set("input.bank", (int) value);
							getPlayer().getProtocol().sendConfig(BankInterface.WITHDRAW_X_CONFIG, getPlayer().getConfig().getInt("input.bank"));
						}
					});
					return;
				case 5: //All
					item = item.setAmount(getPlayer().getInventory().getNumberOf(item));
					break;
			}
			
			if (item == null) return;
			
			long amount = Math.min(getPlayer().getInventory().getNumberOf(item), item.getAmount());
			if (amount <= 0) {
				return; //Must have a 0 amount item stack
			}
			
			item = item.setAmount(amount);
			
			ContainerState inv = getPlayer().getInventory().getState();
			ContainerState bank = getPlayer().getBank().getState();
			
			try {
				inv.remove(item);
				if (item.isNoted()) item = item.getUnnoted();
				bank.add(item);
			}
			catch (ContainerException e) {
				getPlayer().sendMessage("There's not enough space!");
				return;
			}
			
			inv.apply();
			bank.apply();
			return;
		}
	}
	
}