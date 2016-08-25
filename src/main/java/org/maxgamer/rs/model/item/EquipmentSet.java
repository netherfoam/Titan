package org.maxgamer.rs.model.item;

import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.model.item.inventory.Container;

/**
 * @author Albert Beaupre
 */
public class EquipmentSet {

	private final HashMap<WieldType, ItemStack[]> equipmentMap;
	private final String name;

	/**
	 * Constructs a new {@code EquipmentSet} and associates the specified
	 * {@code name} to this set.
	 * 
	 * @param name
	 *            the name of this set
	 */
	public EquipmentSet(String name) {
		this.name = name;
		this.equipmentMap = new HashMap<WieldType, ItemStack[]>();
	}

	/**
	 * Returns true if the specified {@code holder} is wearing this
	 * {@code EquipmentSet}.
	 * 
	 * @param holder
	 *            the holder to check
	 * @return true if the holder is wearing this set; return false otherwise
	 */
	public boolean isWearingSet(Container container) {
		loop: for (Entry<WieldType, ItemStack[]> entry : equipmentMap.entrySet()) {
			for (ItemStack i : entry.getValue()) {
				ItemStack equipment = container.get(entry.getKey().getSlot());
				if (equipment != null && equipment.getId() == i.getId())
					continue loop;
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param type
	 * @param items
	 */
	public void setStack(WieldType type, ItemStack... items) {
		this.equipmentMap.put(type, items);
	}

	/**
	 * Returns the name of this {@code EquipmentSet}.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
