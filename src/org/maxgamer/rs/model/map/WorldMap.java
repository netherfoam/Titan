package org.maxgamer.rs.model.map;

import java.util.HashSet;
import java.util.Iterator;

import org.maxgamer.rs.cache.EncryptedException;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.ViewDistance;
import org.maxgamer.rs.structure.timings.StopWatch;
import org.maxgamer.structure.areagrid.AreaGrid;
import org.maxgamer.structure.areagrid.Cube;
import org.maxgamer.structure.areagrid.MBR;

/**
 * @author netherfoam
 */
public abstract class WorldMap {
	/**
	 * The number of tiles in a chunk. This is hard coded to be eight.
	 */
	public static final int CHUNK_SIZE = 8;
	
	/**
	 * The number of bits you need to shift by to go from a tile number to a
	 * chunk. Eg, position.x >> 3 is the chunkX
	 */
	public static final int CHUNK_BITS = 3;
	
	/**
	 * Minimum load distance for players.
	 */
	private static int LOAD_RANGE = ViewDistance.SMALL.getTileSize() / 2;
	
	/**
	 * A spatial map of all the entities that are currently active in this world
	 */
	private AreaGrid<MBR> entities;
	
	/**
	 * An array of chunks, where each chunk is indexed by its chunk ID. Eg,
	 * lumbridge is at x=3220, y=3222. Therefore, lumbridge chunks are around
	 * (3220 >> 3), (3222 >> 3) equating to (402, 402). So the chunk is in the
	 * array at index chunks[402][402]. X is first, Y is second.
	 * 
	 * The last coordinate is the Z height coordinate
	 */
	private Chunk[][][] chunks;
	
	/**
	 * The size of the map horizontally (West->East) in tiles.
	 */
	private int sizeX;
	
	/**
	 * The size of the map vertically (South->North) in tiles
	 */
	private int sizeY;
	
	/**
	 * The user-friendly name for this map. This will be displayed to players
	 * occasionally
	 */
	private String name;
	
	/**
	 * Constructs a new world map of the given size. The size must be divisible
	 * by CHUNK_SIZE and is the number of tiles in the map.
	 * @param sizeX the size of the map West->East in tiles
	 * @param sizeY the size of the map South->North in tiles
	 */
	public WorldMap(String name, final int sizeX, final int sizeY) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("WorldMap name may not be null or empty");
		}
		if (sizeX % CHUNK_SIZE != 0 || sizeY % CHUNK_SIZE != 0) {
			throw new IllegalArgumentException("Maps must be a multiple of chunk size.. given x: " + sizeX + ", y: " + sizeY);
		}
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.name = name;
		
		StopWatch w = Core.getTimings().start("map-init");
		entities = new AreaGrid<>(sizeX, sizeY, 8);
		chunks = new Chunk[sizeX >> CHUNK_BITS][sizeY >> CHUNK_BITS][4];
		w.stop();
	}
	
	public boolean isLoaded(int cx, int cy, int z) {
		Chunk c = this.chunks[cx][cy][z];
		if (c == null) {
			return false;
		}
		if (c.isLoaded() == false) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * The name of this map
	 * @return The name of this map
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Loads the required range around the given chunk coordiantes.
	 * @param x the x coordinate of the center chunk
	 * @param y the y coordinate of the center chunk
	 * @return true if any loading was caused, false if all tiles were already
	 *         loaded.
	 * @throws EncryptedException if the map file could not be accessed.
	 */
	public void load(int x, int y) throws EncryptedException {
		StopWatch w = Core.getTimings().start("worldmap-load");
		
		for (int i = (x - LOAD_RANGE - 7) >> 3; i < (x + LOAD_RANGE + 7) >> 3; i++) {
			for (int j = (y - LOAD_RANGE - 7) >> 3; j < (y + LOAD_RANGE + 7) >> 3; j++) {
				try {
					for (int z = 0; z < 4; z++) {
						Chunk c = chunks[i][j][z];
						if (c == null || c.isLoaded() == false) {
							//Forces load
							fetch(i, j, z);
						}
					}
				}
				catch (IndexOutOfBoundsException e) {
				}//Near map edge
			}
		}
		w.stop();
	}
	
	public void load(MBR m) throws EncryptedException {
		StopWatch w = Core.getTimings().start("worldmap-load");
		for (int i = 0; i < m.getDimension(0); i++) {
			for (int j = 0; j < m.getDimension(1); j++) {
				int x = (i + m.getMin(0)) >> 8;
				int y = (j + m.getMin(1)) >> 8;
				
				for (int z = 0; z < 4; z++) {
					try {
						Chunk c = chunks[x][y][z];
						if (c == null || c.isLoaded() == false) {
							//Forces load
							fetch(x, y, z);
						}
					}
					catch (IndexOutOfBoundsException e) {
						//Near edge of map
					}
				}
			}
		}
		w.stop();
	}
	
	/**
	 * The number of tiles horizontally (West->East) this map is.
	 * @return the size from West to East in tiles
	 */
	public int getSizeX() {
		return sizeX;
	}
	
	/**
	 * The number of tiles vertically (South->North) this map is.
	 * @return the size from South to North in tiles
	 */
	public int getSizeY() {
		return sizeY;
	}
	
	/**
	 * Places the given entity in this map's spatial index. This method is
	 * called by the setLocation() method from the Entity class.
	 * @param e The entity
	 */
	public void put(MBR e) {
		StopWatch w = Core.getTimings().start("worldmap-put");
		entities.put(e);
		w.stop();
	}
	
	/**
	 * Removes the given entity from this map's spatial index. This method is
	 * called by the setLocation() method from the Entity class when given a
	 * null location.
	 * @param e The entity
	 */
	public void remove(MBR e) {
		StopWatch w = Core.getTimings().start("worldmap-remove");
		entities.remove(e);
		w.stop();
	}
	
	/**
	 * Fetches the chunk at the given chunk coordinates. A chunk coordinate is a
	 * normal coordinate bitshifted right by WorldMap.CHUNK_BITS. (Eg pos.x >>
	 * WorldMap.CHUNK_BITS)
	 * @param chunkX The chunk X
	 * @param chunkY The chunk Y
	 * @return the chunk, null if out of bounds.
	 */
	public Chunk getChunk(int chunkX, int chunkY, int z) {
		StopWatch w = Core.getTimings().start("worldmap-getChunk");
		try {
			Chunk c = chunks[chunkX][chunkY][z];
			if (c == null) {
				c = constructChunk(chunkX, chunkY, z);
				if (c == null) {
					//Couldn't load a chunk, create a blank one
					c = new Chunk(0, 0, 0);
				}
				setChunk(chunkX, chunkY, z, c);
				//chunks[chunkX][chunkY][z] = c;
				
				return c;
			}
			return c;
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
		finally {
			w.stop();
		}
	}
	
	/**
	 * This should call the setChunk(x, y, chunk) method. If it does not load
	 * the given chunks, there will be issues. If you want to load additional
	 * chunks during this request, that is fine. Eg, this requests a specific
	 * chunk only but cache stores groups of 64 chunks in single sections,
	 * meaning it may be faster to simply load 64 chunks.
	 * @param x the chunk x
	 * @param y the chunk y
	 */
	protected abstract void fetch(int x, int y, int z) throws EncryptedException;
	
	protected abstract Chunk constructChunk(int chunkX, int chunkY, int z);
	
	/**
	 * Sets the chunk at the given chunkX, chunkY coordinates to the given
	 * chunk. This does not currently remove entities etc.
	 * @param chunkX The chunkX
	 * @param chunkY The chunkY
	 * @param c the new chunk to set
	 */
	protected void setChunk(int chunkX, int chunkY, int z, Chunk c) {
		if (chunks[chunkX][chunkY][z] == c) return; //Already set.
		
		//TODO: Update players, remove items, etc.
		chunks[chunkX][chunkY][z] = c;
		
		for (Mob mob : getEntities(new Cube(new int[] { chunkX * 8, chunkY * 8 }, new int[] { CHUNK_SIZE, CHUNK_SIZE }), 2, Mob.class)) {
			if (c != null) {
				//We are loading this chunk.
				if (mob.isLoaded() == false) {
					mob.load();
				}
			}
			else {
				//We are unloading this chunk
				if (mob.isLoaded()) {
					mob.unload();
				}
			}
		}
	}
	
	/**
	 * Adds the given clip to the given location in this map. If the location is
	 * out of bounds, then the function returns. If the chunk is null, it
	 * returns.
	 * @param x the x tile coordinate
	 * @param y the y tile coordinate
	 * @param z the height of the tile
	 * @param clip the clip to add. See ClipMasks.*
	 */
	public void addClip(int x, int y, int z, int clip) {
		try {
			int cx = x >> CHUNK_BITS;
			int cy = y >> CHUNK_BITS;
			
			Chunk c = getChunk(cx, cy, z);
			if (c == null) return; //No chunk there.
			//Remove that null check?
			c.addClip(x % CHUNK_SIZE, y % CHUNK_SIZE, clip);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			//We can probably ignore this.
		}
	}
	
	/**
	 * The opposite of addClip(). Removes the given clip at the given location
	 * on this map. If the location is out of bounds, then the function returns.
	 * If the chunk is null, it returns.
	 * @param x the x tile coordinate
	 * @param y the y tile coordinate
	 * @param z the height of the tile
	 * @param clip the clip to remove. See ClipMasks.*
	 */
	public void removeClip(int x, int y, int z, int clip) {
		try {
			int cx = x >> CHUNK_BITS;
			int cy = y >> CHUNK_BITS;
			
			Chunk c = getChunk(cx, cy, z);
			if (c == null) return; //No chunk there.
			
			c.removeClip(x % CHUNK_SIZE, y % CHUNK_SIZE, clip);
		}
		catch (ArrayIndexOutOfBoundsException e) {
		}
	}
	
	/**
	 * Fetches the clip at the given location on the map. If the given location
	 * is out of bounds, or the chunk at that position is null, -1 (0xFFFF FFFF)
	 * is returned, meaning there cannot be movement on that tile.
	 * @param x the x tile coordinate
	 * @param y the y tile coordinate
	 * @param z the height of the tile
	 * @return clip the clip, see ClipMasks.*
	 */
	public int getClip(int x, int y, int z) {
		StopWatch w = Core.getTimings().start("worldmap-getClip");
		try {
			int cx = x >> CHUNK_BITS;
			int cy = y >> CHUNK_BITS;
			Chunk c = chunks[cx][cy][z];
			if (c == null || c.isLoaded() == false) {
				return -1;
			}
			return c.getClip(x & 7, y & 7); //Was changed from x % CHUNK_SIZE
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
		finally {
			w.stop();
		}
	}
	
	public int getFlags(int x, int y, int z) {
		StopWatch w = Core.getTimings().start("worldmap-getFlags");
		try {
			int cx = x >> CHUNK_BITS;
			int cy = y >> CHUNK_BITS;
			Chunk c = chunks[cx][cy][z];
			if (c == null || c.isLoaded() == false) {
				return 0;
			}
			return c.getFlags(x & 7, y & 7);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
		finally {
			w.stop();
		}
	}
	
	/**
	 * Fetches all entities that overlap with the given MBR query.
	 * @param query the query to search for
	 * @param guess an educated guess as to how many entities you will get. This
	 *        is for the hashset's initial size.
	 * @param clazz The type of entity you're trying to retrieve.
	 * @return the hashset of entities in the area overlapping with the query.
	 */
	public <T extends MBR> HashSet<T> getEntities(MBR query, int guess, Class<T> clazz) {
		StopWatch w = Core.getTimings().start("worldmap-getEntities");
		HashSet<T> set = entities.get(query, guess, clazz);
		if (query.getDimensions() >= 3) {
			//validate all entities are in the requested dimensions
			Iterator<T> sit = set.iterator();
			while (sit.hasNext()) {
				T t = sit.next();
				//I think this is correct.
				//TODO: This is bad, we should be using >= instead.
				if (t.getMin(2) < query.getMin(2) || t.getMin(2) > query.getMin(2) + query.getDimension(2)) {
					sit.remove();
					continue;
				}
			}
		}
		w.stop();
		return set;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	public void trim() {
		this.entities.trim();
	}
}