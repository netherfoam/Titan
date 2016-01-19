package org.maxgamer.rs.interfaces.impl.primary;

import java.util.Comparator;

import org.maxgamer.rs.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.GenericContainer;
import org.maxgamer.rs.model.item.inventory.StackType;

/**
 * @author Albert Beaupre
 */
public class ItemsOnDeathInterface extends PrimaryInterface {

	private Container keepContainer;

	public ItemsOnDeathInterface(Player p) {
		super(p);
		setChildId(102);
	}

	@Override
	public void onOpen() {
		Container all = new GenericContainer(player.getInventory().getSize() + player.getEquipment().getSize(), StackType.ALWAYS);
		all.addAll(player.getInventory());
		all.addAll(player.getEquipment());
		all.sort(new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack o1, ItemStack o2) {
				return o2.getDefinition().getValue() - o1.getDefinition().getValue();
			}
		});
		all.shift();
		int keep = getKeepSize();
		keepContainer = new GenericContainer(keep, StackType.NEVER);
		int index = 0;
		while (keep > 0 && !all.isEmpty()) {
			ItemStack item = all.get(index);
			if (item == null)
				continue;
			all.remove(item.setAmount(1));
			if (all.get(index) == null || all.get(index).getAmount() == 0)
				index++;
			keep--;
			keepContainer.add(item.setAmount(1).setHealth(keep));
		}

		Object[] params = new Object[] { getRiskedWealth(), getCarriedWealth(), "", 0, 0, getKeepItem(3), getKeepItem(2), getKeepItem(1), getKeepItem(0), getKeepSize(), 0 };

		player.getProtocol().invokeScript(118, params);
		this.setUnlockOptions(0, 211, 18, 0);
		this.setUnlockOptions(0, 212, 21, 0);
		player.getProtocol().sendBConfig(199, 442);
	}

	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {

	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public boolean isServerSidedClose() {
		return false;
	}

	@Override
	public boolean isMobile() {
		return false;
	}

	private long getCarriedWealth() {
		return getWealth(player.getInventory()) + getWealth(player.getEquipment());

	}

	private long getWealth(Container c) {
		long wealth = 0;
		for (int i = 0; i < c.getSize(); i++) {
			ItemStack item = c.get(i);
			if (item != null) {
				wealth += item.getAmount() * item.getDefinition().getValue();
			}
		}
		return wealth;
	}

	private long getRiskedWealth() {
		return getCarriedWealth() - getWealth(keepContainer);
	}

	private int getKeepSize() {
		return 3;
	}

	private int getKeepItem(int slot) {
		if (getKeepSize() <= slot)
			return -1;
		return keepContainer.get(slot) == null ? -1 : keepContainer.get(slot).getId();
	}

}
