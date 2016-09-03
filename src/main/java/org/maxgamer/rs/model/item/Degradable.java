package org.maxgamer.rs.model.item;


public interface Degradable {

    /**
     * Returns the ascending sequence of the items that will be degraded to.
     *
     * @return the ascending sequence of degrading items
     */
    ItemStack[] getDegradeSequence();

    /**
     * This method is called when the specified {@code previousItem} has been
     * degraded and changed to the specified {@code itemChangedTo}.
     *
     * @param previousItem  the previous item before the item was degraded
     * @param itemChangedTo the item the previous item was degraded to
     */
    void onDegrade(ItemStack previousItem, ItemStack itemChangedTo);

}
