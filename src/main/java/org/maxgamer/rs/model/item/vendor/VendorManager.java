package org.maxgamer.rs.model.item.vendor;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.repository.VendorRepository;

public class VendorManager {
    private VendorRepository repository = Core.getServer().getDatabase().getRepository(VendorRepository.class);

    public VendorManager() {

    }

    public Vendor get(String name) {
        return repository.findOneByName(name);
    }

    public Vendor get(int id) {
        return repository.find(id);
    }
}