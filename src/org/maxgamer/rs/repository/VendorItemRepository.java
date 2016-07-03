package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.vendor.VendorItem;

/**
 * @author netherfoam
 */
public class VendorItemRepository extends AbstractRepository<VendorItem> {
    public VendorItemRepository() {
        super(VendorItem.class);
    }
}
