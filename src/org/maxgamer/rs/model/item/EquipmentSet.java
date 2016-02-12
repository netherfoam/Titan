package org.maxgamer.rs.model.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.model.entity.mob.EquipmentHolder;

public class EquipmentSet {

	private final HashMap<WieldType, ItemStack[]> equipmentMap;
	private final String name;

	public EquipmentSet(String name) {
		this.name = name;
		this.equipmentMap = new HashMap<WieldType, ItemStack[]>();
	}

	public boolean isWearingSet(EquipmentHolder holder) {
		loop: for (Entry<WieldType, ItemStack[]> entry : equipmentMap.entrySet()) {
			for (ItemStack i : entry.getValue()) {
				ItemStack equipment = holder.getEquipment().get(entry.getKey().getSlot());
				if (equipment != null && equipment.getId() == i.getId())
					continue loop;
			}
			return false;
		}
		return true;
	}

	public void setStack(WieldType type, ItemStack... items) {
		this.equipmentMap.put(type, items);
	}
	
	public Collection<ItemStack[]> getItems() {
		return equipmentMap.values();
	}

	public String getName() {
		return name;
	}
}
