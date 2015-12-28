package org.maxgamer.rs.model.map.area;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobMoveEvent;
import org.maxgamer.rs.model.events.mob.MobPreTeleportEvent;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;

/**
 * @author Albert Beaupre
 */
public abstract class AbstractArea extends Area implements EventListener {
	
	public enum AreaChangeState {
		TELEPORT, WALK
	}
	
	/**
	 * Constructs a new {@code AbstractArea} within the given locations to
	 * create a cubic area region inside the specified {@code min} and
	 * {@code max} arguments.
	 * 
	 * @param min the minimum location to start the cubic area from
	 * @param max the maximum location to end the cubic area
	 */
	public AbstractArea(Location min, Location max) {
		super(min, max);
		
		Core.getServer().getEvents().register(this);
	}
	
	/**
	 * This method is executed when the specified {@code mob} enters this
	 * {@code AbstractArea}.
	 * 
	 * @param mob the mob entering the area
	 * @param state the state at which the mob entered this area
	 */
	public abstract void onEnter(Mob mob, AreaChangeState state);
	
	/**
	 * This method is executed when the specified {@code mob} leaves this
	 * {@code AbstractArea}.
	 * 
	 * @param mob the mob entering the area
	 * @param state the state at which the mob leaves this area
	 */
	public abstract void onLeave(Mob mob, AreaChangeState state);
	
	@EventHandler()
	public void onMove(MobMoveEvent event) {
		if (event.getMob() == null || event.getDestination() == null) return;
		Mob m = event.getMob();
		Position p = event.getDestination();
		if (contains(m) && !containsPosition(p)) {
			onLeave(m, AreaChangeState.WALK);
		}
		if (!contains(m) && containsPosition(p)) {
			onEnter(m, AreaChangeState.WALK);
		}
	}
	
	@EventHandler()
	public void onTeleport(MobPreTeleportEvent event) {
		if (event.getMob() == null) return;
		
		Location from = event.getTeleportFromLocation();
		Location to = event.getTeleportToLocation();
		
		if (containsPosition(from) && !containsPosition(to)) {
			onLeave(event.getMob(), AreaChangeState.TELEPORT);
		}
		else if (!containsPosition(from) && containsPosition(to)) {
			onEnter(event.getMob(), AreaChangeState.TELEPORT);
		}
	}
	
}
