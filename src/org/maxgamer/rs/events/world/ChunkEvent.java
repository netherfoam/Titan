package org.maxgamer.rs.events.world;

import org.maxgamer.rs.events.RSEvent;
import org.maxgamer.rs.model.map.Chunk;
import org.maxgamer.rs.model.map.WorldMap;

/**
 * @author netherfoam
 */
public class ChunkEvent extends RSEvent {
	private WorldMap map;
	private Chunk c;
	private int cx;
	private int cy;
	private int z;
	
	public ChunkEvent(WorldMap map, Chunk c, int cx, int cy, int z) {
		this.map = map;
		this.c = c;
		this.cx = cx;
		this.cy = cy;
		this.z = z;
	}
	
	public Chunk getChunk() {
		return c;
	}
	
	public WorldMap getMap() {
		return map;
	}
	
	public int getChunkX() {
		return cx;
	}
	
	public int getChunkY() {
		return cy;
	}
	
	public int getChunkZ() {
		return z;
	}
}