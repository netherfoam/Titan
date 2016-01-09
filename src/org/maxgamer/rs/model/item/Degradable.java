package org.maxgamer.rs.model.item;

import java.util.Objects;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.inventory.Container;

/**
 * This class should be used for degrading {@code ItemStakc} to another state.
 * 
 * @author Albert Beaupre
 */
public abstract class Degradable {
	
	/**
	 * Degrades this {@code DegradableItem} from the starting specified
	 * {@code degradeFrom} item and removes the item from the specified
	 * {@code from} container and adds the next item in the degrade sequence to
	 * the specified {@code to} container. After the item is degraded,
	 * {@link #onDegrade(ItemStack)} is called with the previously degraded and
	 * newly replaced item as parameters.
	 * 
	 * @param degradeFrom the item to degrade from
	 * @param from the container to degrade the item from
	 * @param to the container to place the next degradable item in
	 * @return true if the item was degraded, return false otherwise
	 * 
	 * @see #getDegradeSequence()
	 * @see #onDegrade(ItemStack)
	 */
	public final boolean degrade(Mob mob, ItemStack degradeFrom, Container from, Container to) {
		degradeFrom = Objects.requireNonNull(degradeFrom, "The item to degrade from cannot be NULL");
		from = Objects.requireNonNull(from, "The container to remove the degrading item cannot be NULL");
		to = Objects.requireNonNull(to, "The container to add the new item cannot be NULL");
		
		from.remove(degradeFrom);
		from.add(degradeFrom = degradeFrom.setHealth(Math.max(0, degradeFrom.getHealth() - 1)));
		if (degradeFrom.getHealth() <= 0) {
			int itemIndex = -1;
			ItemStack[] sequence = getDegradeSequence();
			for (int i = 0; i < sequence.length; i++) {
				if (sequence[i] != null && sequence[i].getId() == degradeFrom.getId()) {
					itemIndex = i;
					break;
				}
			}
			if (itemIndex == -1) return false;
			ItemStack currentItem = sequence[itemIndex];
			currentItem = currentItem.setHealth(Math.max(0, currentItem.getHealth() - 1));
			if (itemIndex + 1 < sequence.length) {
				from.remove(degradeFrom);
				onDegrade(mob, currentItem, currentItem = sequence[itemIndex + 1]);
				if (currentItem != null) to.add(currentItem);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Degrades this {@code DegradableItem} from the starting specified
	 * {@code degradeFrom} item and removes the item from the specified
	 * container and replaces it with the next item in the degrade sequence.
	 * After the item is degraded, {@link #onDegrade(ItemStack)} is called with
	 * the previously degraded and newly replaced item as parameters.
	 * 
	 * @param degradeFrom the item to degrade from
	 * @param container the container to degrade the item
	 * @return true if the item was degraded, return false otherwise
	 * 
	 * @see #getDegradeSequence()
	 * @see #onDegrade(ItemStack)
	 */
	public final boolean degrade(Mob mob, ItemStack degradeFrom, Container container) {
		return degrade(mob, degradeFrom, container, container);
	}
	
	/**
	 * This method is called when the specified {@code previousItem} has been
	 * degraded and changed to the specified {@code itemChangedTo}.
	 * 
	 * @param previousItem the previous item before the item was degraded
	 * @param itemChangedTo the item the previous item was degraded to
	 */
	protected void onDegrade(Mob mob, ItemStack previousItem, ItemStack itemChangedTo) {
	}
	
	/**
	 * Returns the ascending sequence of the items that will be degraded to.
	 * 
	 * @return the ascending sequence of degrading items
	 */
	protected abstract ItemStack[] getDegradeSequence();
	
}
