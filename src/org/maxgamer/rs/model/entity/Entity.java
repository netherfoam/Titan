package org.maxgamer.rs.model.entity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.IllegalThreadException;
import org.maxgamer.rs.model.map.Locatable;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.structure.areagrid.MBR;

/**
 * Represents something in the world, whether visible or not. Can be game
 * objects, players, mobs, NPC's, bots, items on the ground and more.
 * @author netherfoam
 */
public abstract class Entity implements MBR, Locatable {
	/** Size along x axis in tiles */
	private int sizeX = 1;
	/** Size along y axis in tiles */
	private int sizeY = 1;
	/**
	 * true if destroyed, false if not destroyed
	 */
	private boolean destroyed = false;
	
	/**
	 * The current location of this Entity, when modifying this you must update
	 * the map properties of the location
	 */
	private Location location;
	
	private ArrayList<WeakReference<Entity>> viewers;
	
	/**
	 * Constructs a new entity with size = 1x1
	 */
	public Entity() {
		
	}
	
	public final void setPrivate(boolean isPrivate){
		if(isPrivate){
			this.viewers = new ArrayList<WeakReference<Entity>>(0);
		}
		else{
			this.viewers = null;
		}
	}
	
	public final boolean isPrivate(){
		if(viewers == null){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns true if this entity is visible to the given entity. Entities, by default,
	 * are visible to all other Entities. Entities can be made private by calling
	 * {@link Entity#setPrivate(true)}. Once private, an entity can be hidden/shown
	 * to specific entities. A destroyed Entity is never visible.
	 * @param viewer the entity attempting to view this entity
	 * @return true if the entity can see this
	 */
	public boolean isVisible(Entity viewer){
		if(this.isDestroyed()){ 
			return false;
		}
		
		if(isPrivate() == false){
			return true;
		}
		
		Iterator<WeakReference<Entity>> it = viewers.iterator();
		while(it.hasNext()){
			WeakReference<Entity> ref = it.next();
			Entity e = ref.get();
			if(e == null){
				it.remove();
			}
			if(e == viewer){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Hides this entity from the given viewer. 
	 * @param viewer the viewer
	 * @throws IllegalStateException if this entity is public
	 */
	public final void addViewer(Entity viewer){
		if(isPrivate() == false){
			throw new IllegalStateException("Entity is public. Therefore it cannot be hidden!");
		}
		
		Iterator<WeakReference<Entity>> it = viewers.iterator();
		while(it.hasNext()){
			WeakReference<Entity> ref = it.next();
			Entity e = ref.get();
			if(e == null){
				it.remove();
			}
			if(e == viewer){
				it.remove();
			}
		}
	}
	
	/**
	 * Shows this entity to the given viewer. If this is public, the method does nothing.
	 * @param viewer the viewer
	 */
	public final void removeViewer(Entity viewer){
		if(isPrivate() == false){
			/* Entity is visible to everyone already */
			return;
		}
		
		Iterator<WeakReference<Entity>> it = viewers.iterator();
		while(it.hasNext()){
			WeakReference<Entity> ref = it.next();
			Entity e = ref.get();
			if(e == null){
				it.remove();
			}
			if(e == viewer){
				return;
			}
		}
		
		viewers.add(new WeakReference<Entity>(viewer));
	}
	
	/**
	 * Constructs a new entity with the given size
	 * @param sizeX the size on the X axis
	 * @param sizeY the size on the Y axis
	 */
	public Entity(int sizeX, int sizeY) {
		this();
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	/**
	 * The size along the X axis
	 * @return The size along the X axis
	 */
	public int getSizeX() {
		return this.sizeX;
	}
	
	/**
	 * The size along the Y axis
	 * @return The size along the Y axis
	 */
	public int getSizeY() {
		return this.sizeY;
	}
	
	/**
	 * Modifies the size of this entity, you should usually call this during the
	 * constructor phase before the location is set.
	 * @param x the size along the x axis (North-South)
	 * @param y the size along the y axis (East-West)
	 */
	public void setSize(int x, int y) {
		Location l = getLocation();
		if (l != null) {
			setLocation(null);
		}
		
		this.sizeX = x;
		this.sizeY = y;
		if (l != null) {
			setLocation(l);
		}
	}
	
	/**
	 * The current location of this entity, may be null. This is the SOUTH WEST
	 * corner of the entity. (Eg, South West corner of world is 0,0). Adding
	 * sizeX - 1 and sizeY - 1 to this will result in having the NORTH EAST
	 * corner of this entity
	 * @return The current location
	 */
	public final Location getLocation() {
		return location;
	}
	
	public final WorldMap getMap() {
		if (location == null) return null;
		return location.getMap();
	}
	
	/**
	 * The center of this Entity. This is the same as getLocation().add(sizeX/2, sizeY/2).
	 * The center is rounded down, with a bias towards NORTH/EAST if sizeX/sizeY are even
	 * (In effect, if there is no center, this will pick the title that is north, east, or
	 * both of the real center)
	 * @return
	 */
	public final Location getCenter() {
		Location l = getLocation();
		if (l == null) return null;
		
		return l.add(this.sizeX / 2, this.sizeY / 2);
	}
	
	/**
	 * Sets the location of this entity. Null is a valid value, but each
	 * subclass may have different rules for what locations are valid or throw
	 * exceptions. For example, two objects of the same type and facing may not
	 * be on the same tile.
	 * @param l the location to set.
	 */
	protected void setLocation(Location l) {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new IllegalThreadException("Entity location should only be changed on server thread");
		}
		
		if (l != null && l.getMap() == null) {
			throw new IllegalArgumentException("Cannot set location for Entity, if the given location is valid but the map is not!");
		}
		
		if (this.location != null) {
			this.location.getMap().remove(this);
		}
		
		this.location = l;
		
		if (this.location != null) {
			this.location.getMap().put(this);
		}
	}
	
	@Override
	public int getMin(int axis) {
		return location.getMin(axis);
	}
	
	@Override
	public int getDimension(int axis) {
		switch (axis) {
			case 0:
				return sizeX;
			case 1:
				return sizeY;
			case 2:
				return 1;
		}
		throw new IllegalArgumentException("Invalid axis requested: " + axis);
	}
	
	@Override
	public int getDimensions() {
		return 3;
	}
	
	/**
	 * Returns true if {@link Entity#destroy()} has been called
	 * @return true if this entity has been destroyed
	 */
	public boolean isDestroyed() {
		return destroyed;
	}
	
	/**
	 * Destroys this entity, setting the location to null.
	 * @throws RuntimeException if this is called from a thread that isn't the
	 *         server thread
	 */
	public void destroy() {
		if (Core.getServer().getThread().isServerThread() == false) {
			throw new RuntimeException("Entities should only be destroyed on the Server thread.");
		}
		if (isDestroyed()) throw new RuntimeException("This entity has already been destroyed.");
		setLocation(null);
		this.destroyed = true;
	}
}