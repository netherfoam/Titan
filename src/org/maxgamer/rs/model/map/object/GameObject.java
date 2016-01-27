package org.maxgamer.rs.model.map.object;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.maxgamer.rs.cache.Archive;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.definition.GameObjectProto;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.persona.player.Viewport;
import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.rs.model.map.path.SimpleDirection;

/**
 * @author netherfoam
 */
public abstract class GameObject extends Entity implements Interactable {
	/**
	 * The previously loaded game object definitions
	 */
	private static HashMap<Integer, GameObjectProto> definitions = new HashMap<Integer, GameObjectProto>(65000);
	
	public static GameObjectProto getDefinition(int id) {
		if (definitions.containsKey(id) == false) {
			//The gameobject has not been loaded before.
			try {
				//Each Archive from the IDX file has up to 256 subfiles
				Archive a = Core.getCache().getArchive(IDX.OBJECTS, id >>> 8);
				ByteBuffer src = a.get(id & 0xFF);
				GameObjectProto def = GameObjectProto.decode(id, src);
				if (src.remaining() > 0) {
					throw new IOException("Error parsing gameobject " + id);
				}
				definitions.put(id, def);
				return def;
			}
			catch (IOException e) {
				//We shouldn't be forced to catch this every time we want to construct
				//a game object.
				throw new RuntimeException(e);
			}
		}
		else {
			//The game object definition is used elsewhere, we use the cached version to save resources.
			return definitions.get(id);
		}
	}
	
	/**
	 * The data for this object. This could represent anything, generally health
	 * or the number of harvests it has remaining.
	 */
	private int data = -1;
	
	/**
	 * The definition for this object
	 */
	private GameObjectProto def;
	
	/**
	 * True if this object is hidden, false if it is visible. A hidden object
	 * does not have any clip effect in the world, similar to if getLocation()
	 * is null.
	 */
	private boolean hidden = false;
	
	private Tickable showTask;
	
	/**
	 * The rotation for this object. NESW represent 0,1,2,3 for protocol
	 * purposes
	 */
	private SimpleDirection rotation = Directions.NORTH;
	
	/**
	 * The type of this object, this ranges from 0 to 22.
	 */
	private int type;
	
	/**
	 * Constructs a new GameObject. This class should not be constructed without
	 * extending it, instead you should extend DynamicGameObject generally.
	 * @param id the ID of the object, a RuntimeException will be thrown if this
	 *        is not available in the cache
	 * @param type the type for the object, values are 0-22. See
	 *        {@link GameObject#getType()}
	 * @throws {@link RuntimeException} if there was an error parsing the
	 *         object. This exception is a wrapper for an IOException if an
	 *         IOException occurred
	 */
	public GameObject(int id, int type) {
		super();
		
		if (definitions.containsKey(id) == false) {
			//The gameobject has not been loaded before.
			try {
				//Each Archive from the IDX file has up to 256 subfiles
				Archive a = Core.getCache().getArchive(IDX.OBJECTS, id >>> 8);
				ByteBuffer src = a.get(id & 0xFF);
				this.def = GameObjectProto.decode(id, src);
				if (src.remaining() > 0) {
					throw new IOException("Error parsing gameobject " + id);
				}
				definitions.put(id, this.def);
			}
			catch (IOException e) {
				//We shouldn't be forced to catch this every time we want to construct
				//a game object.
				throw new RuntimeException(e);
			}
		}
		else {
			//The game object definition is used elsewhere, we use the cached version to save resources.
			this.def = definitions.get(id);
		}
		
		this.type = type;
		//Now we adjust our size
		this.setSize(this.getSizeX(), this.getSizeY());
	}
	
	/** Adds the clip from this object to the world */
	private void applyClip() {
		//Remove any old clip
		int[][] clip = this.getClip(); //A 3x3 clip array, referenced below
		
		/*
		 * Applies the below clipping (# = point of interest) [?][?][?]
		 * [?][#][?] [?][?][?]
		 */
		Location swCorner = this.getLocation();
		for (int i = 0; i < this.getSizeX(); i++) {
			for (int j = 0; j < this.getSizeY(); j++) {
				this.getLocation().getMap().addClip(swCorner.x + i, swCorner.y + j, swCorner.z, clip[1][1]); //Apply the center clip
			}
		}
		
		/*
		 * Applies the below clipping (# = point of interest) [?][#][?]
		 * [?][?][?] [?][#][?]
		 */
		for (int i = 0; i < this.getSizeX(); i++) {
			this.getLocation().getMap().addClip(swCorner.x + i, swCorner.y - 1, swCorner.z, clip[1][0]);
			this.getLocation().getMap().addClip(swCorner.x + i, swCorner.y + this.getSizeY(), swCorner.z, clip[1][2]);
		}
		
		/*
		 * Applies the below clipping (# = point of interest) [?][?][?]
		 * [#][?][#] [?][?][?]
		 */
		for (int j = 0; j < this.getSizeY(); j++) {
			this.getLocation().getMap().addClip(swCorner.x - 1, swCorner.y + j, swCorner.z, clip[0][1]);
			this.getLocation().getMap().addClip(swCorner.x + this.getSizeX(), swCorner.y + j, swCorner.z, clip[2][1]);
		}
		
		/*
		 * Applies the below clipping (# = point of interest) [#][?][#]
		 * [?][?][?] [#][?][#]
		 */
		this.getLocation().getMap().addClip(swCorner.x - 1, swCorner.y - 1, swCorner.z, clip[0][0]);
		this.getLocation().getMap().addClip(swCorner.x - 1, swCorner.y + this.getSizeY(), swCorner.z, clip[0][2]);
		this.getLocation().getMap().addClip(swCorner.x + this.getSizeX(), swCorner.y - 1, swCorner.z, clip[2][0]);
		this.getLocation().getMap().addClip(swCorner.x + this.getSizeX(), swCorner.y + this.getSizeY(), swCorner.z, clip[2][2]);
	}
	
	@Override
	public void destroy() {
		//Calls setLocation(null) which calls hide() if the previous location wasn't null.
		//Therefore we don't need to inform nearby players of this, we're already doing it.
		super.destroy();
	}
	
	public int getActionCount() {
		return this.def.getActionCount();
	}
	
	/**
	 * Gets the clip for this object. This is a 3x3 int array, which can be
	 * stretched over the object: [!][@][#] [$][%][^] [&][*][~]
	 * 
	 * is returned, which can be stretched into (Say a 2x3 size object) like
	 * this. (Match the symbols) [!][@][@][#] [$][%][%][^] [$][%][%][^]
	 * [$][%][%][^] [&][*][*][~]
	 * 
	 * Note that this returns clipping for the nearby tiles, eg an object that
	 * takes 5x5 in area, will require to be stretched across a 7x7 area.
	 * @return
	 */
	public int[][] getClip() {
		int type = this.getType();
		
		int clip = 0;
		int[][] clips = new int[3][3];
		
		if (type == 22) {
			if (this.getActionCount() == 1) {
				clip |= ClipMasks.BLOCKED_TILE;
			}
		}
		else if (type >= 9 && type <= 11) {
			if (this.isSolid() || this.getActionCount() != 0) {
				clip |= this.getClipForSolidObject();
			}
		}
		else if (type >= 0 && type <= 3) {
			if (this.getActionCount() != 0) {
				clips = this.getClipForVariableObject();
			}
		}
		
		clips[1][1] |= clip;
		
		return clips;
	}
	
	private int getClipForSolidObject() {
		int clipping = 0xFF;
		if (this.isSolid()) {
			clipping |= ClipMasks.OBJECT_BLOCK;
		}
		if (this.hasRangeBlockClipFlag() == false) {
			clipping |= ClipMasks.OBJECT_ALLOW_RANGE;
		}
		return clipping;
	}
	
	private int[][] getClipForVariableObject() {
		int type = this.getType();
		Direction direction = this.getFacing();
		int[][] clips = new int[3][3];
		boolean isSolid = this.isSolid();
		boolean hasAllowRangeFlag = !this.hasRangeBlockClipFlag();
		
		if (type == 0) {
			if (direction == Directions.NORTH) {
				clips[1][1] |= ClipMasks.WALL_WEST;
				clips[0][1] |= ClipMasks.WALL_EAST;
			}
			if (direction == Directions.EAST) {
				clips[1][1] |= ClipMasks.WALL_NORTH;
				clips[1][2] |= ClipMasks.WALL_SOUTH;
			}
			if (direction == Directions.SOUTH) {
				clips[1][1] |= ClipMasks.WALL_EAST;
				clips[2][1] |= ClipMasks.WALL_WEST;
			}
			if (direction == Directions.WEST) {
				clips[1][1] |= ClipMasks.WALL_SOUTH;
				clips[1][0] |= ClipMasks.WALL_NORTH;
			}
		}
		if (type == 1 || type == 3) {
			if (direction == Directions.NORTH) {
				clips[1][1] |= ClipMasks.WALL_NORTH_WEST;
				clips[0][2] |= ClipMasks.WALL_SOUTH_EAST;
			}
			if (direction == Directions.EAST) {
				clips[1][1] |= ClipMasks.WALL_NORTH_EAST;
				clips[2][2] |= ClipMasks.WALL_SOUTH_WEST;
			}
			if (direction == Directions.SOUTH) {
				clips[1][1] |= ClipMasks.WALL_SOUTH_EAST;
				clips[2][0] |= ClipMasks.WALL_NORTH_WEST;
			}
			if (direction == Directions.WEST) {
				clips[1][1] |= ClipMasks.WALL_SOUTH_WEST;
				clips[0][0] |= ClipMasks.WALL_NORTH_EAST;
			}
		}
		if (type == 2) {
			if (direction == Directions.NORTH) {
				clips[1][1] |= ClipMasks.WALL_WEST | ClipMasks.WALL_NORTH;
				clips[0][1] |= ClipMasks.WALL_EAST;
				clips[1][2] |= ClipMasks.WALL_SOUTH;
			}
			if (direction == Directions.EAST) {
				clips[1][1] |= ClipMasks.WALL_NORTH | ClipMasks.WALL_EAST;
				clips[1][2] |= ClipMasks.WALL_SOUTH;
				clips[2][1] |= ClipMasks.WALL_WEST;
			}
			if (direction == Directions.SOUTH) {
				clips[1][1] |= ClipMasks.WALL_EAST | ClipMasks.WALL_SOUTH;
				clips[2][1] |= ClipMasks.WALL_WEST;
				clips[1][0] |= ClipMasks.WALL_NORTH;
			}
			if (direction == Directions.WEST) {
				clips[1][1] |= ClipMasks.WALL_SOUTH | ClipMasks.WALL_WEST;
				clips[1][0] |= ClipMasks.WALL_NORTH;
				clips[0][1] |= ClipMasks.WALL_EAST;
			}
		}
		if (isSolid && getActionCount() != 2) { 
			/* TODO: This may cause issues with walking through objects, but fixes the lumbridge spinning wheel doorway!
			 * If issues occur, try remove the && getActionCount() != 2 comparison */
			if (type == 0) {
				if (direction == Directions.NORTH) {
					clips[1][1] |= ClipMasks.BLOCKED_WEST;
					clips[0][1] |= ClipMasks.BLOCKED_EAST;
				}
				if (direction == Directions.EAST) {
					clips[1][1] |= ClipMasks.BLOCKED_NORTH;
					clips[1][2] |= ClipMasks.BLOCKED_SOUTH;
				}
				if (direction == Directions.SOUTH) {
					clips[1][1] |= ClipMasks.BLOCKED_EAST;
					clips[2][1] |= ClipMasks.BLOCKED_WEST;
				}
				if (direction == Directions.WEST) {
					clips[1][1] |= ClipMasks.BLOCKED_SOUTH;
					clips[1][0] |= ClipMasks.BLOCKED_NORTH;
				}
			}
			if (type == 1 || type == 3) {
				if (direction == Directions.NORTH) {
					clips[1][1] |= ClipMasks.BLOCKED_NORTH_WEST;
					clips[0][2] |= ClipMasks.BLOCKED_EAST;
				}
				if (direction == Directions.EAST) {
					clips[1][1] |= ClipMasks.BLOCKED_NORTH_EAST;
					clips[2][2] |= ClipMasks.BLOCKED_SOUTH_WEST;
				}
				if (direction == Directions.SOUTH) {
					clips[1][1] |= ClipMasks.BLOCKED_EAST;
					clips[2][0] |= ClipMasks.BLOCKED_NORTH_WEST;
				}
				if (direction == Directions.WEST) {
					clips[1][1] |= ClipMasks.BLOCKED_SOUTH_WEST;
					clips[0][0] |= ClipMasks.BLOCKED_NORTH_EAST;
				}
			}
			if (type == 2) {
				if (direction == Directions.NORTH) {
					clips[1][1] |= ClipMasks.BLOCKED_WEST | ClipMasks.BLOCKED_NORTH;
					clips[0][1] |= ClipMasks.BLOCKED_EAST;
					clips[1][2] |= ClipMasks.BLOCKED_SOUTH;
				}
				if (direction == Directions.EAST) {
					clips[1][1] |= ClipMasks.BLOCKED_NORTH | ClipMasks.BLOCKED_EAST;
					clips[1][2] |= ClipMasks.BLOCKED_SOUTH;
					clips[2][1] |= ClipMasks.BLOCKED_WEST;
				}
				if (direction == Directions.SOUTH) {
					clips[1][1] |= ClipMasks.BLOCKED_EAST | ClipMasks.BLOCKED_SOUTH;
					clips[2][1] |= ClipMasks.BLOCKED_WEST;
					clips[1][0] |= ClipMasks.BLOCKED_NORTH;
				}
				if (direction == Directions.WEST) {
					clips[1][1] |= ClipMasks.BLOCKED_SOUTH | ClipMasks.BLOCKED_WEST;
					clips[1][0] |= ClipMasks.BLOCKED_NORTH;
					clips[0][1] |= ClipMasks.BLOCKED_EAST;
				}
			}
		}
		if (hasAllowRangeFlag) {
			if (type == 0) {
				if (direction == Directions.NORTH) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_WEST;
					clips[0][1] |= ClipMasks.WALL_ALLOW_RANGE_EAST;
				}
				if (direction == Directions.EAST) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_NORTH;
					clips[1][2] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH;
				}
				if (direction == Directions.SOUTH) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_EAST;
					clips[2][1] |= ClipMasks.WALL_ALLOW_RANGE_WEST;
				}
				if (direction == Directions.WEST) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH;
					clips[1][0] |= ClipMasks.WALL_ALLOW_RANGE_NORTH;
				}
			}
			if (type == 1 || type == 3) {
				if (direction == Directions.NORTH) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_NORTH_WEST;
					clips[0][2] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH_EAST;
				}
				if (direction == Directions.EAST) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_NORTH_EAST;
					clips[2][2] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH_WEST;
				}
				if (direction == Directions.SOUTH) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH_EAST;
					clips[2][0] |= ClipMasks.WALL_ALLOW_RANGE_NORTH_WEST;
				}
				if (direction == Directions.WEST) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH_WEST;
					clips[0][0] |= ClipMasks.WALL_ALLOW_RANGE_NORTH_EAST;
				}
			}
			if (type == 2) {
				if (direction == Directions.NORTH) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_WEST | ClipMasks.WALL_ALLOW_RANGE_NORTH;
					clips[0][1] |= ClipMasks.WALL_ALLOW_RANGE_EAST;
					clips[1][2] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH;
				}
				if (direction == Directions.EAST) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_NORTH | ClipMasks.WALL_ALLOW_RANGE_EAST;
					clips[1][2] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH;
					clips[2][1] |= ClipMasks.WALL_ALLOW_RANGE_WEST;
				}
				if (direction == Directions.SOUTH) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_EAST | ClipMasks.WALL_ALLOW_RANGE_SOUTH;
					clips[2][1] |= ClipMasks.WALL_ALLOW_RANGE_WEST;
					clips[1][0] |= ClipMasks.WALL_ALLOW_RANGE_NORTH;
				}
				if (direction == Directions.WEST) {
					clips[1][1] |= ClipMasks.WALL_ALLOW_RANGE_SOUTH | ClipMasks.WALL_ALLOW_RANGE_WEST;
					clips[1][0] |= ClipMasks.WALL_ALLOW_RANGE_NORTH;
					clips[0][1] |= ClipMasks.WALL_ALLOW_RANGE_WEST;
				}
			}
		}
		return clips;
	}
	
	/**
	 * The data value for this object. Eg, the health or the number of harvests
	 * left on this object before it is depleted. This returns -1 if the data
	 * has not been set
	 * @return the data value
	 */
	public int getData() {
		return this.data;
	}
	
	public GameObjectProto getDefiniton() {
		return this.def;
	}
	
	/**
	 * The text received when examining this object. Null frequently
	 * @return
	 */
	public String getExamine() {
		return this.def.getExamine();
	}
	
	/**
	 * The direction this object is facing
	 * @return The direction this object is facing
	 */
	public SimpleDirection getFacing() {
		return this.rotation;
	}
	
	/**
	 * The ID of this GameObject's definition.
	 * @return The ID of this GameObject's definition.
	 */
	public int getId() {
		return this.def.getId();
	}
	
	/**
	 * The name of this object, String "null" frequently.
	 * @return The name of this object
	 */
	public String getName() {
		return this.def.getName();
	}
	
	/**
	 * The size of this object along the x axis of the map. This is based off
	 * the facing of the object
	 * @return
	 */
	@Override
	public int getSizeX() {
		if (this.rotation == Directions.NORTH || this.rotation == Directions.SOUTH) {
			return this.def.getSizeX();
		}
		else {
			//Flipped
			return this.def.getSizeY();
		}
	}
	
	/**
	 * The size of this object along the y axis of the map. This is based off
	 * the facing of the object
	 * @return
	 */
	@Override
	public int getSizeY() {
		if (this.rotation == Directions.NORTH || this.rotation == Directions.SOUTH) {
			return this.def.getSizeY();
		}
		else {
			//Flipped
			return this.def.getSizeX();
		}
	}
	
	/**
	 * The type for this gameobject. Types are: WALL, WALL_DECORATION, NORMAL,
	 * GROUND_DECORATION. Results from this method are: 0: WALL (Straight walls,
	 * fences) 1: WALL (Diagonal walls corner, fences) 2: WALL (Entire walls,
	 * fences, corners) 3: WALL (Straight wall corners, fences, connectors) 4:
	 * WALL_DECORATION (straight inside wall decor) 5: WALL_DECORATION (straight
	 * outside wall decor) 6: WALL_DECORATION (straight outside wall decor) 7:
	 * WALL_DECORATION (straight inside wall decor) 8: NORMAL (diagonal in wall
	 * decor) 9: NORMAL (diagonal walls, fences) 10: NORMAL (all kinds of
	 * objects, trees, statues, signs, fountains) 11: NORMAL (ground objects
	 * like daisies) 12: NORMAL (straight sloped roofs) 13: NORMAL (daigonal
	 * sloped roofs) 14: NORMAL (diagonal slope connecting roofs) 15: NORMAL
	 * (straight sloped corner connecting roofs) 16: NORMAL (straight sloped
	 * corner roof) 17: NORMAL (straight flat top roofs) 18: NORMAL (straight
	 * bottom edge roofs) 19: NORMAL (diagonal bottom edge connecting roofs) 20:
	 * NORMAL (straight bottom edge connecting roofs) 21: NORMAL (straight
	 * bottom edge connecting corner roofs) 22: GROUND_DECORATION (map signs
	 * (quests, water, fountains, shops, etc)
	 * @return
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Returns true if getData() returns a number that is not -1
	 * @return true if getData() returns a number that is not -1
	 */
	public boolean hasData() {
		return this.data != -1;
	}
	
	public boolean hasOption(String option) {
		return this.getDefiniton().hasOption(option);
	}
	
	public boolean hasRangeBlockClipFlag() {
		return this.def.hasRangeBlockClipFlag();
	}
	
	@Override
	public boolean isVisible(Entity to){
		if(isHidden()) return false;
		return super.isVisible(to);
	}
	
	/**
	 * Hides this object from the view of players. This updates it on the
	 * clients which can view this.
	 * @throws NullPointerException if {@link GameObject#getLocation()} returns
	 *         null
	 */
	public void hide() {
		if (this.getLocation() == null) {
			throw new NullPointerException("May not hide without a location.");
		}
		
		this.removeClip();
		this.hidden = true;
		
		for (Viewport v : this.getLocation().getNearby(Viewport.class, 0)) {
			v.getOwner().getProtocol().hideObject(this);
		}
	}
	
	public void hide(int ticks) {
		if (this.showTask != null && this.showTask.isQueued()) {
			this.showTask.cancel();
			this.showTask = null;
		}
		
		this.hide();
		
		this.showTask = new Tickable() {
			@Override
			public void tick() {
				GameObject.this.show();
			}
		};
		
		this.showTask.queue(ticks);
	}
	
	/**
	 * Returns true if this object is hidden, false if it is visible to players.
	 * If the location is null, this method may still return false. (Eg no
	 * location and visible is a valid state)
	 * @return true if intentionally hidden, false otherwise
	 */
	public boolean isHidden() {
		return this.hidden;
	}
	
	public boolean isSolid() {
		return this.def.isSolid();
	}
	
	/** Removes the clip from this object from the world */
	private void removeClip() {
		//Remove any old clip
		int[][] clip = this.getClip(); //A 3x3 clip array, referenced below
		
		/*
		 * Removes the below clipping (# = point of interest) [?][?][?]
		 * [?][#][?] [?][?][?]
		 */
		Location old = this.getLocation();
		for (int i = 0; i < this.getSizeX(); i++) {
			for (int j = 0; j < this.getSizeY(); j++) {
				this.getLocation().getMap().removeClip(old.x + i, old.y + j, old.z, clip[1][1]); //Apply the center clip
			}
		}
		
		/*
		 * Removes the below clipping (# = point of interest) [?][#][?]
		 * [?][?][?] [?][#][?]
		 */
		for (int i = 0; i < this.getSizeX(); i++) {
			this.getLocation().getMap().removeClip(old.x + i, old.y - 1, old.z, clip[1][0]);
			this.getLocation().getMap().removeClip(old.x + i, old.y + this.getSizeY(), old.z, clip[1][2]);
		}
		
		/*
		 * Removes the below clipping (# = point of interest) [?][?][?]
		 * [#][?][#] [?][?][?]
		 */
		for (int j = 0; j < this.getSizeY(); j++) {
			this.getLocation().getMap().removeClip(old.x - 1, old.y + j, old.z, clip[0][1]);
			this.getLocation().getMap().removeClip(old.x + this.getSizeX(), old.y + j, old.z, clip[2][1]);
		}
		
		/*
		 * Removes the below clipping (# = point of interest) [#][?][#]
		 * [?][?][?] [#][?][#]
		 */
		this.getLocation().getMap().removeClip(old.x - 1, old.y - 1, old.z, clip[0][0]);
		this.getLocation().getMap().removeClip(old.x - 1, old.y + this.getSizeY(), old.z, clip[0][2]);
		this.getLocation().getMap().removeClip(old.x + this.getSizeX(), old.y - 1, old.z, clip[2][0]);
		this.getLocation().getMap().removeClip(old.x + this.getSizeX(), old.y + this.getSizeY(), old.z, clip[2][2]);
		
		//Now request all gameobjects on our location apply their own clip incase we removed it
		//This is a bad system, but it works.
		for (GameObject g : this.getLocation().getNearby(GameObject.class, 0)) {
			if (g != this) {
				g.applyClip(); //TODO: Hidden check
			}
		}
	}
	
	/**
	 * Sets the return value of getData(). When the object is constructed, the
	 * data value is set to -1 to represent no data available.
	 * @param data the data to set.
	 */
	public void setData(int data) {
		this.data = data;
	}
	
	/**
	 * Sets the facing of this object. This calls setLocation() if the location
	 * is not null, and thus updates all surrounding clients when necessary
	 * @param f the new facing direction
	 * @throws IllegalArgumentException from
	 *         {@link GameObject#setLocation(Location)} if the contract stating
	 *         that there may only be one object per tile with a matching facing
	 *         and type is violated
	 */
	public void setFacing(SimpleDirection f) {
		if (f == null) {
			throw new NullPointerException("Facing may not be null.");
		}
		this.rotation = f;
		super.setSize(this.getSizeX(), this.getSizeY()); //Calls setLocation()
	}
	
	/**
	 * Sets the return result of {@link GameObject#getLocation()}. Note that
	 * this isn't allowed by {@link StaticGameObject} which throws an Exception
	 * when calling.
	 * @param l The new location for this object
	 * @throws IllegalArgumentException if this call violates the contract which
	 *         states that a location may only have one gameobject with the same
	 *         facing and type.
	 */
	@Override
	public void setLocation(Location l) {
		if (this.getLocation() != null) {
			this.hide();
		}
		
		if (l != null) {
			//TODO: There may be a bug here where if this object is hidden and setLocation() is called,
			//then we shouldn't be performing this check, should we?
			for (GameObject g : l.getNearby(GameObject.class, 0)) {
				if (g == this) {
					continue;
				}
				if (g.isHidden()) {
					continue;
				}
				
				//TODO: L.getNearby() seems to be returning objects hwicha re not ontop of the samme tile :!(
				if (g.getLocation().equals(l) && g.getFacing() == this.getFacing() && g.getType() == this.getType()) {
					throw new IllegalArgumentException("A single tile may only contain one GameObject with the same rotation and type. The location " + l + " already has an object (" + g + ") and " + this + " can't be placed there.");
				}
			}
		}
		
		super.setLocation(l);
		
		if (this.getLocation() != null) {
			this.show();
		}
	}
	
	/**
	 * Shows this object to nearby players. This updates it on the clients which
	 * can view this.
	 * @throws NullPointerException if {@link GameObject#getLocation()} returns
	 *         null
	 */
	public void show() {
		if (this.getLocation() == null) {
			throw new NullPointerException("May not show without a location.");
		}
		
		this.applyClip();
		this.hidden = false;
		
		for (Viewport v : this.getLocation().getNearby(Viewport.class, 0)) {
			v.getOwner().getProtocol().showObject(this);
		}
	}
	
	@Override
	public String toString() {
		//return "ID: " + getId() + ", Type: " + getType() + ", Facing: " + Directions.getName(getFacing()) + " Size: " + getSizeX() + "x" + getSizeY() + " AC: " + getActionCount() + ", S:" + isSolid() + " R:" + hasRangeBlockClipFlag();
		StringBuilder sb = new StringBuilder();
		if (this.getName() != null && this.getName().isEmpty() == false && this.getName().equals("null") == false) {
			sb.append(this.getName());
		}
		sb.append("(" + this.getId() + ")");
		sb.append(" type=" + this.getType() + ",hidden=" + this.isHidden());
		sb.append(",facing: " + Directions.getName(this.getFacing()));
		sb.append(",ac=" + this.getActionCount() + ",solid=" + this.isSolid() + ",low=" + this.hasRangeBlockClipFlag());
		return sb.toString();
	}
	
	@Override
	public String[] getOptions() {
		return def.getOptions().clone();
	}
}