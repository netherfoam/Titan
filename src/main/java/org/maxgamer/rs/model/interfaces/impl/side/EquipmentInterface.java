package org.maxgamer.rs.model.interfaces.impl.side;

import java.util.HashMap;

import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.model.interfaces.impl.primary.ItemsOnDeathInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.MobEquipEvent;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * @author netherfoam
 */
public class EquipmentInterface extends SideInterface {
	public static final int EQUIPMENT_CONTAINER_ID = 94;
	private static HashMap<Integer, WieldType> BUTTON_TO_SLOT = new HashMap<Integer, WieldType>();
	static {
		BUTTON_TO_SLOT.put(8, WieldType.HAT);
		BUTTON_TO_SLOT.put(11, WieldType.CAPE);
		BUTTON_TO_SLOT.put(14, WieldType.AMULET);
		BUTTON_TO_SLOT.put(17, WieldType.WEAPON);
		BUTTON_TO_SLOT.put(20, WieldType.BODY);
		BUTTON_TO_SLOT.put(23, WieldType.SHIELD);
		BUTTON_TO_SLOT.put(26, WieldType.LEGS);
		BUTTON_TO_SLOT.put(29, WieldType.GLOVES);
		BUTTON_TO_SLOT.put(32, WieldType.BOOTS);
		BUTTON_TO_SLOT.put(35, WieldType.RING);
		BUTTON_TO_SLOT.put(38, WieldType.ARROWS);
	}
	
	private ContainerListener listener;
	private ItemsOnDeathInterface iodInterface;
	
	public EquipmentInterface(Player p) {
		//207 or 92
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 207 : 92)); //Container ID is 94, but Interface ID is 387
		this.iodInterface = new ItemsOnDeathInterface(player);
		setChildId(387);
	}
	
	@Override
	public void onOpen() {
		super.onOpen();
		
		for (int i = 0; i < this.getPlayer().getEquipment().getSize(); i++) {
			ItemStack item = getPlayer().getEquipment().get(i);
			if (item == null) continue; //We only show items we have. TODO: This could be optimized by sending an array of slots and items at once
			getPlayer().getProtocol().setItem(EQUIPMENT_CONTAINER_ID, false, item, i);
		}
		
		this.listener = new ContainerListener() {
			@Override
			public void onSet(Container c, int slot, ItemStack old) {
				assert (isOpen()); //Should always be true if called
				getPlayer().getProtocol().setItem(EQUIPMENT_CONTAINER_ID, false, c.get(slot), slot);
			}
		};
		
		getPlayer().getEquipment().addListener(this.listener);
	}
	
	@Override
	public void onClose() {
		super.onClose();
		
		//We hide all items we sent them. TODO: This could be optimized by sending an array of slots and items at once
		for (int i = 0; i < this.getPlayer().getEquipment().getSize(); i++) {
			ItemStack item = getPlayer().getEquipment().get(i);
			if (item != null) continue;
			getPlayer().getProtocol().setItem(EQUIPMENT_CONTAINER_ID, false, null, i);
		}
		getPlayer().getInventory().removeListener(this.listener);
	}
	
	@Override
	public void onClick(int option, int buttonId, int slot, int itemId) {
		//Slot is invalid for the equipment interface, it is sent by the client as 65535 (-1)
		//Client instead sends button ID based on the slot they clicked.
		if (buttonId == 45) { //Items on death
			player.getWindow().open(iodInterface);
			return;
		}
		
		if (BUTTON_TO_SLOT.containsKey(buttonId)) {
			WieldType type = BUTTON_TO_SLOT.get(buttonId);
			
			ItemStack item = getPlayer().getEquipment().get(type);
			if (item == null || item.getId() != itemId) {
				getPlayer().getCheats().log(5, "Item click ID mismatch");
				return;
			}
			
			if (option == 0) { //TODO: Should we just check if it's "Wear"? It becomes "Unwear" in the client.
				if (item.getWeapon() == null) {
					getPlayer().getCheats().log(10, "Attempted to unequip an item which has no equipment slot. Item: " + item);
					return;
				}
				
				ContainerState equip = getPlayer().getEquipment().getState();
				ContainerState inv = getPlayer().getInventory().getState();
				
				try {

					MobEquipEvent event = new MobEquipEvent(player, item, null);
					event.call();
					if (event.isCancelled())
						return;
					equip.remove(type.getSlot(), item);
					inv.add(item);
				}
				catch (ContainerException e) {
					getPlayer().sendMessage("Not enough space!");
					return;
				}
				
				ItemStack weapon = equip.get(WieldType.WEAPON.getSlot());
				if (weapon != null) {
					player.getModel().setRenderAnimationId(weapon.getDefinition().getRenderAnimation());
				}
				else {
					player.getModel().setRenderAnimationId(1426);
				}
				equip.apply();
				inv.apply();
			}
		}
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
}
