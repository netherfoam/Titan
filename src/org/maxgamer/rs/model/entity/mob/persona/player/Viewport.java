package org.maxgamer.rs.model.entity.mob.persona.player;

import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.structure.areagrid.Cube;
import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.areagrid.MBRUtil;

/**
 * A class to represent the area visible to a player at a given point in time.
 * @author netherfoam
 */
public class Viewport implements MBR {
	private Player owner;
	private ViewDistance distance;
	private Cube cube;
	private Location center;
	
	/**
	 * Constructs a new Viewport based on the given player's current location
	 * (center) and the player's current ViewDistance.
	 * @param owner the player
	 * @throws NullPointerException if the player is null
	 */
	public Viewport(Player owner) {
		if (owner == null) throw new NullPointerException("Owner may not be null");
		this.owner = owner;
		this.center = owner.getLocation();
		this.distance = owner.getViewDistance();
		this.cube = this.distance.getArea(owner.getLocation());
	}
	
	public WorldMap getMap(){
		return center.map;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public ViewDistance getDistance() {
		return distance;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	@Override
	public int getMin(int axis) {
		return cube.getMin(axis);
	}
	
	@Override
	public int getDimension(int axis) {
		return cube.getDimension(axis);
	}
	
	@Override
	public int getDimensions() {
		return cube.getDimensions();
	}
	
	public boolean overlaps(MBR m) {
		return MBRUtil.isOverlap(this, m);
	}
}