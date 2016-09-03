package org.maxgamer.rs.model.events.world;

import org.maxgamer.rs.model.map.Chunk;
import org.maxgamer.rs.model.map.WorldMap;

/**
 * @author netherfoam
 */
public class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(WorldMap map, Chunk c, int cx, int cy, int z) {
        super(map, c, cx, cy, z);
    }
}