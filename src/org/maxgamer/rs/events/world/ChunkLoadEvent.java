package org.maxgamer.rs.events.world;

import org.maxgamer.rs.model.map.Chunk;
import org.maxgamer.rs.model.map.WorldMap;

/**
 * @author netherfoam
 */
public class ChunkLoadEvent extends ChunkEvent {
	public ChunkLoadEvent(WorldMap map, Chunk c, int cx, int cy, int z) {
		super(map, c, cx, cy, z);
	}
}