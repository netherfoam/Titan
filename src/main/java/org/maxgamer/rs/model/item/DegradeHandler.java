package org.maxgamer.rs.model.item;

import org.maxgamer.rs.model.item.inventory.Container;

import java.util.Objects;

/**
 * This class should be used for degrading {@code ItemStakc} to another state.
 *
 * @author Albert Beaupre
 */
public final class DegradeHandler {

    /**
     * Degrades this {@code DegradableItem} from the starting specified
     * {@code degradeFrom} item and removes the item from the specified
     * {@code from} container and adds the next item in the degrade sequence to
     * the specified {@code to} container. After the item is degraded,
     * {@link #onDegrade(ItemStack)} is called with the previously degraded and
     * newly replaced item as parameters.
     *
     * @param degradeFrom the item to degrade from
     * @param from        the container to degrade the item from
     * @param to          the container to place the next degradable item in
     * @return true if the item was degraded, return false otherwise
     * @see #getDegradeSequence()
     * @see #onDegrade(ItemStack)
     */
    public static boolean degrade(Degradable degradable, ItemStack degradeFrom, Container from, Container to) {
        degradeFrom = Objects.requireNonNull(degradeFrom, "The item to degrade from cannot be NULL");
        from = Objects.requireNonNull(from, "The container to remove the degrading item cannot be NULL");
        to = Objects.requireNonNull(to, "The container to add the new item cannot be NULL");

        from.remove(degradeFrom);
        from.add(degradeFrom = degradeFrom.setHealth(Math.max(0, degradeFrom.getHealth() - 1)));
        if (degradeFrom.getHealth() <= 0) {
            int itemIndex = -1;
            ItemStack[] sequence = degradable.getDegradeSequence();
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
                degradable.onDegrade(currentItem, currentItem = sequence[itemIndex + 1]);
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
     * @param container   the container to degrade the item
     * @return true if the item was degraded, return false otherwise
     * @see #getDegradeSequence()
     * @see #onDegrade(ItemStack)
     */
    public static boolean degrade(Degradable degradable, ItemStack degradeFrom, Container container) {
        return degrade(degradable, degradeFrom, container, container);
    }

    public static boolean hasItem(Degradable degradable, Container container) {
        for (ItemStack i : degradable.getDegradeSequence())
            if (container.contains(i.getId(), 1))
                return true;
        return false;
    }

}
