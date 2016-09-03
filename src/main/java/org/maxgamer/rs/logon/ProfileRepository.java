package org.maxgamer.rs.logon;

import org.maxgamer.rs.repository.AbstractRepository;

/**
 * @author netherfoam
 */
public class ProfileRepository extends AbstractRepository<Profile> {
    public ProfileRepository() {
        super(Profile.class);
    }

    public Profile find(String name) {
        if (name == null) return null;
        return super.find(name.toLowerCase());
    }
}
