package org.maxgamer.rs.model.map.object;

import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.SimpleDirection;

import java.io.IOException;

/**
 * @author netherfoam
 */
public class StaticGameObject extends GameObject {

    public StaticGameObject(int id, int type, Location loc, SimpleDirection facing) throws IllegalArgumentException, IOException {
        super(id, type);
        super.setFacing(facing);
        super.setLocation(loc);
    }

    @Override
    public void setLocation(Location l) {
        throw new IllegalStateException("StaticGameObjects may have their location changed.");
    }

    @Override
    public void setFacing(SimpleDirection dir) {
        throw new IllegalStateException("StaticGameObjects may not have their facing (Location) changed.");
    }
}