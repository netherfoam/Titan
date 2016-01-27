package org.maxgamer.rs.model.map;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.ViewDistance;
import org.maxgamer.rs.model.map.area.AreaManager;
import org.maxgamer.rs.structure.areagrid.AreaGrid;
import org.maxgamer.rs.structure.areagrid.Cube;
import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.timings.StopWatch;

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
	private static int LOAD_RADIUS = ViewDistance.SMALL.getTileSize() / 2;
	
	public static final int FLAG_CLIP = 0x1;
	public static final int FLAG_BRIDGE = 0x2;
	
	/**
	 * Flag given if there is a roof over this piece of terrain
	 */
	public static final int FLAG_ROOF = 0x4;
	
	/**
	 * Steep cliff flag? Elevation flag?
	 */
	public static final int FLAG_UNKNOWN = 0x8;
	
	/**
	 * Wall flag?
	 */
	public static final int FLAG_UNKNOWN2 = 0x10;
	
	/**
	 * A spatial map of all the entities that are currently active in this world
	 */
	private AreaGrid<MBR> entities;
	
	private AreaManager areas;
	
	/**
	 * An array of chunks, where each chunk is indexed by its chunk ID. Eg,
	 * lumbridge is at x=3220, y=3222. Therefore, lumbridge chunks are around
	 * (3220 >> 3), (3222 >> 3) equating to (402, 402). So the chunk is in the
	 * array at index chunks[402][402]. X is first, Y is second.
	 * 
	 * The last coordinate is the Z height coordinate
	 */
	private Chunk[][][] chunks;
	
	//private final Position min;
	private final Position min_chunk;
	
	private final int width;
	private final int height;
	
	/**
	 * The user-friendly name for this map. This will be displayed to players
	 * occasionally
	 */
	private String name;
	
	/**
	 * Constructs a new world map of the given size. The size must be divisible
	 * by CHUNK_SIZE and is the number of tiles in the map.
	 * @param width the size of the map West->East in tiles
	 * @param height the size of the map South->North in tiles
	 */
	public WorldMap(String name, final int width, final int height) {
		this(name, new Position(0, 0), width, height);
	}
	
	public WorldMap(String name, Position min, int width, int height) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("WorldMap name may not be null or empty");
		}
		if(min == null){
			throw new NullPointerException("Min position may not be null");
		}
		
		this.name = name;
		this.min_chunk = new Position(min.x >> CHUNK_BITS, min.y >> CHUNK_BITS);
		this.width = width;
		this.height = height;
		
		if (width() % CHUNK_SIZE != 0 || height() % CHUNK_SIZE != 0) {
			throw new IllegalArgumentException("Maps must be a multiple of chunk size.. given width: " + width() + ", height: " + height());
		}
		
		StopWatch w = Core.getTimings().start("map-init");
		entities = new AreaGrid<MBR>(width(), height(), 8);
		
		//We only initialize the first layer to save memory.
		chunks = new Chunk[width() >> CHUNK_BITS][][];
		
		w.stop();
		areas = new AreaManager(this);
		Core.getServer().getEvents().register(areas);
	}
	
	public final Position offset(){
		return new Position(min_chunk.x << CHUNK_BITS, min_chunk.y << CHUNK_BITS);
	}
	
	private void check(int cx, int cy){
		try{
			if(this.chunks[cx - this.min_chunk.x] == null){
				this.chunks[cx - this.min_chunk.x] = new Chunk[height() >> CHUNK_BITS][];
			}
			if(this.chunks[cx - this.min_chunk.x][cy - this.min_chunk.y] == null){
				this.chunks[cx - this.min_chunk.x][cy - this.min_chunk.y] = new Chunk[4];
			}
		}
		catch(Exception e){
			Log.debug("cx: " + cx + ", cy: " + cy /*", w: " + this.chunks.length + " h: " + this.chunks[0].length*/);
			e.printStackTrace();
		}
	}
	
	public boolean isLoaded(int cx, int cy, int z) {
		Chunk c;
		try{
			c = this.chunks[cx - this.min_chunk.x][cy - this.min_chunk.y][z];
		}
		catch(NullPointerException e){
			return false;
		}
		
		if (c == null) {
			return false;
		}
		if (c.isLoaded() == false) {
			return false;
		}
		
		return true;
	}
	
	public AreaManager getAreas(){
		return areas;
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
	 * @throws WorldFullException 
	 * @throws IOException 
	 */
	public void load(int x, int y) throws IOException {
		//x -= this.min_chunk.x << CHUNK_BITS;
		//y -= this.min_chunk.y << CHUNK_BITS;
		
		StopWatch w = Core.getTimings().start("worldmap-load");
		
		for (int i = (x - LOAD_RADIUS - 7) >> 3; i < (x + LOAD_RADIUS + 7) >> 3; i++) {
			for (int j = (y - LOAD_RADIUS - 7) >> 3; j < (y + LOAD_RADIUS + 7) >> 3; j++) {
				check(i, j);
				try {
					for (int z = 0; z < 4; z++) {
						Chunk c = chunks[i - this.min_chunk.x][j - this.min_chunk.y][z];
						if (c == null || c.isLoaded() == false) {
							//Forces load
							fetch(i, j, z);
						}
					}
				}
				catch (IndexOutOfBoundsException e) {
					//Near map edge
				}
			}
		}
		w.stop();
	}
	
	public void load(MBR m) throws IOException {
		StopWatch w = Core.getTimings().start("worldmap-load");
		for (int i = 0; i < m.getDimension(0); i++) {
			for (int j = 0; j < m.getDimension(1); j++) {
				int x = (i + m.getMin(0)) >> 8;
				int y = (j + m.getMin(1)) >> 8;
					
				check(i, j);
				
				for (int z = 0; z < 4; z++) {
					try {
						Chunk c = chunks[x - this.min_chunk.x][y - this.min_chunk.y][z];
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
	public final int width() {
		return this.width;
	}
	
	/**
	 * The number of tiles vertically (South->North) this map is.
	 * @return the size from South to North in tiles
	 */
	public final int height() {
		return this.height;
	}
	
	/**
	 * Places the given entity in this map's spatial index. This method is
	 * called by the setLocation() method from the Entity class.
	 * @param e The entity
	 */
	public void put(MBR e) {
		StopWatch w = Core.getTimings().start("worldmap-put");
		entities.put(proxy(e), e);
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
		entities.remove(proxy(e), e);
		w.stop();
	}
	
	private MBR proxy(final MBR mbr){
		//TODO: if (min.x == 0 && min.y == 0) return mbr // this will be faster but harder to test
		/*return mbr;*/
		return new MBR() {
			@Override
			public int getMin(int axis) {
				if(axis == 0) return mbr.getMin(axis) - (min_chunk.x << CHUNK_BITS);
				if(axis == 1) return mbr.getMin(axis) - (min_chunk.y << CHUNK_BITS);
				return mbr.getMin(axis);
			}
			
			@Override
			public int getDimensions() {
				return mbr.getDimensions();
			}
			
			@Override
			public int getDimension(int axis) {
				return mbr.getDimension(axis);
			}
		};
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
			check(chunkX, chunkY);
			
			Chunk c = chunks[chunkX - this.min_chunk.x][chunkY - this.min_chunk.y][z];
			if (c == null) {
				c = constructChunk(chunkX, chunkY, z);
				if (c == null) {
					//Couldn't load a chunk, create a blank one
					c = new Chunk(0, 0, 0);
				}
				setChunk(chunkX, chunkY, z, c);
				
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
	 * @throws IOException 
	 */
	protected abstract void fetch(int x, int y, int z) throws IOException;
	
	protected abstract Chunk constructChunk(int chunkX, int chunkY, int z);
	
	/**
	 * Sets the chunk at the given chunkX, chunkY coordinates to the given
	 * chunk. This does not currently remove entities etc.
	 * @param chunkX The chunkX
	 * @param chunkY The chunkY
	 * @param c the new chunk to set
	 */
	protected void setChunk(int chunkX, int chunkY, int z, Chunk c) {
		check(chunkX, chunkY);
		
		if (chunks[chunkX - this.min_chunk.x][chunkY - this.min_chunk.y][z] == c) return; //Already set.
		
		//TODO: Update players, remove items, etc.
		chunks[chunkX - this.min_chunk.x][chunkY - this.min_chunk.y][z] = c;
		
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
			check(cx, cy);
			
			Chunk c = chunks[cx - this.min_chunk.x][cy - this.min_chunk.y][z];
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
			check(cx, cy);
			
			Chunk c = chunks[cx - this.min_chunk.x][cy - this.min_chunk.y][z];
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
	public <T extends MBR> HashSet<T> getEntities(final MBR query, int guess, Class<T> clazz) {
		StopWatch w = Core.getTimings().start("worldmap-getEntities");
		
		MBR proxy = proxy(query);
		
		HashSet<T> set = entities.get(proxy, guess, clazz);
		if (proxy.getDimensions() >= 3) {
			//validate all entities are in the requested dimensions
			Iterator<T> sit = set.iterator();
			while (sit.hasNext()) {
				T t = sit.next();
				//TODO: This is bad, we should be using >= instead.
				if(t.getMin(2) + t.getDimension(2) < proxy.getMin(2) || t.getMin(2) > proxy.getMin(2) + proxy.getDimension(2)){
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
	
	public void destroy(){
		if(this.chunks == null){
			throw new IllegalStateException("Map is already destroyed!");
		}
		
		for(Entity e : this.entities.all(Entity.class)){
			if(e instanceof Persona){
				// We attempt to rescue persona's and send them to their spawn.
				// If their spawn is the current map, they will be destroyed.
				Persona p = (Persona) e;
				p.teleport(p.getSpawn());
				if(p.getMap() != this){
					continue;
				}
			}
			e.destroy();
		}
		
		Core.getServer().getEvents().unregister(this.areas);
		this.chunks = null;
		this.entities = null;
	}
}