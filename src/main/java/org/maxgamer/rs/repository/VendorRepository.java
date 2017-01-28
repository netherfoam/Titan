package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.vendor.VendorType;

/**
 * @author netherfoam
 */
public class VendorRepository extends AbstractRepository<VendorType> {
    public VendorRepository() {
        super(VendorType.class);
    }

    public VendorType findOneByName(String name) {
        return (VendorType) getManager().createQuery("FROM " + this.name() + " WHERE name = :name").setParameter("name", name).uniqueResult();
    }
}
