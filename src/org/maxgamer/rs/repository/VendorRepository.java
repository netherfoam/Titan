package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.vendor.Vendor;

/**
 * @author netherfoam
 */
public class VendorRepository extends Repository<Vendor> {
    public VendorRepository() {
        super(Vendor.class);
    }

    public Vendor findOneByName(String name) {
        return (Vendor) getManager().createQuery("FROM " + this.name() + " WHERE name = :name").setParameter("name", name).uniqueResult();
    }
}
