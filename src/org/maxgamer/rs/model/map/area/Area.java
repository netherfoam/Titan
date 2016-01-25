package org.maxgamer.rs.model.map.area;

import java.util.Collection;

import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.areagrid.MBRUtil;

/**
 * @author netherfoam
 * @author Albert Beaupre
 */
public class Area implements MBR {
	public enum AreaChangeState {
		/**
		 * The area was changed because the mob walked
		 */
		WALK,
		
		/**
		 * The area was changed because the mob teleported
		 */
		TELEPORT, 
		
		/**
		 * The area was changed by some other method of location modification. 
		 * Includes logging in and out, or adding/removing an area from the
		 * {@link AreaManager}. Essentially, the Server triggered this moreso
		 * than the Mob triggered this.
		 */
		SERVER
		
	}
	
	/**
	 * The bottom left corner (South-West most)
	 */
	private Location min;
	
	/**
	 * The top right corner (North-East most)
	 */
	private Location max;
	
	/**
	 * Constructs a new {@code AbstractArea} within the given locations to
	 * create a cubic area region inside the specified {@code min} and
	 * {@code max} arguments.
	 * 
	 * @param cornerA the first corner of the area
	 * @param cornerB the opposite corner of the area
	 */
	public Area(Location cornerA, Location cornerB) {
		if (cornerA.getMap() != cornerB.getMap()) throw new IllegalArgumentException("An Area must range across only one map.");
		
		this.min = Location.min(cornerA, cornerB);
		this.max = Location.max(cornerA, cornerB);
	}
	
	public final Location getMin(){
		return min;
	}
	
	public final Location getMax(){
		return max;
	}
	
	@Override
	public int getMin(int axis) {
		return min.getMin(axis);
	}
	
	@Override
	public int getDimension(int axis) {
		return max.getMin(axis) - min.getMin(axis);
	}
	
	@Override
	public int getDimensions() {
		return min.getDimensions(); // three
	}
	
	public boolean contains(MBR m) {
		return MBRUtil.isOverlap(this, m);
	}
	
	/**
	 * Returns true if the given position is within this area.  This 
	 * @param p
	 * @return
	 */
	public boolean contains(Position p) {
		if(p.x < min.x) return false;
		if(p.y < min.y) return false;
		if(p.x > max.x) return false;
		if(p.y > max.y) return false;
		
		return true;
	}
	
	public WorldMap getMap() {
		return min.getMap();
	}
	
	public <E extends Entity> Collection<E> getAll(int guess, Class<E> clazz) {
		return getMap().getEntities(this, guess, clazz);
	}
	
	/**
	 * This method is executed when the specified {@code mob} enters this
	 * {@code Area}.
	 * 
	 * @param mob the mob entering the area
	 * @param state the state at which the mob entered this area
	 */
	public void onEnter(Mob mob, AreaChangeState state) {
	}
	
	/**
	 * This method is executed when the specified {@code mob} leaves this
	 * {@code Area}.
	 * 
	 * @param mob the mob entering the area
	 * @param state the state at which the mob leaves this area
	 */
	public void onLeave(Mob mob, AreaChangeState state) {
	}
}