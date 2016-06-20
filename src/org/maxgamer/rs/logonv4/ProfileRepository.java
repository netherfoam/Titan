package org.maxgamer.rs.logonv4;

import org.maxgamer.rs.repository.Repository;

/**
 * @author netherfoam
 */
public class ProfileRepository extends Repository<Profile> {
    public ProfileRepository() {
        super(Profile.class);
    }

    public Profile find(String name) {
        if(name == null) return null;
        return super.find(name.toLowerCase());
    }
}
