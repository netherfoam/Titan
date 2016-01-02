package org.maxgamer.rs.model.item;

import java.util.Objects;

import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.structure.ArrayUtility;

/**
 * This class should be used for degrading items to another item.
 * 
 * @author Albert Beaupre
 */
public abstract class DegradableItem {
	
	/**
	 * Degrades this {@code DegradableItem} from the starting specified
	 * {@code degradeFrom} item and removes the item from the specified
	 * container and replaces it with the next item in the degrade sequence.
	 * After the item is degraded, {@link #onDegrade(ItemStack)} is called.
	 * 
	 * @param degradeFrom the item to degrade from
	 * @param container the container to degrade the item
	 * @return true if the item was degraded, return false otherwise
	 * 
	 * @see #getDegradeSequence()
	 * @see #onDegrade(ItemStack)
	 */
	public final boolean degrade(ItemStack degradeFrom, Container container) {
		degradeFrom = Objects.requireNonNull(degradeFrom, "The item to degrade from cannot be NULL");
		container = Objects.requireNonNull(container, "The container to handle degrading cannot be NULL");
		
		int itemIndex = ArrayUtility.indexOf(degradeFrom, getDegradeSequence());
		ItemStack currentItem = getDegradeSequence()[itemIndex];
		currentItem = currentItem.setHealth(Math.max(0, currentItem.getHealth() - 1));
		
		if (currentItem.getHealth() <= 0) {
			if (itemIndex + 1 < getDegradeSequence().length) {
				container.remove(currentItem);
				onDegrade(currentItem, currentItem = getDegradeSequence()[itemIndex + 1]);
				if (currentItem != null) container.add(currentItem);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method is called when the specified {@code previousItem} has been
	 * degraded and changed to the specified {@code itemChangedTo}.
	 * 
	 * @param previousItem the previous item before the item was degraded
	 * @param itemChangedTo the item the previous item was degraded to
	 */
	protected abstract void onDegrade(ItemStack previousItem, ItemStack itemChangedTo);
	
	/**
	 * Returns the ascending sequence of the items that will be degraded to.
	 * 
	 * @return the ascending sequence of degrading items
	 */
	protected abstract ItemStack[] getDegradeSequence();
	
}
