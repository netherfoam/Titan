package org.maxgamer.rs.model.map.area;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobMoveEvent;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerEnterWorldEvent;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerMapUpdateEvent;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;

/**
 * @author Albert Beaupre
 */
public abstract class AbstractArea extends Area implements EventListener {

	/**
	 * Represents a type at which this {@code AbstractArea} can change between.
	 * 
	 * @author Albert Beaupre
	 */
	public enum AreaChangeState {
		ENTER_WORLD, LEAVE_WORLD, TELEPORT_ENTER, TELEPORT_LEAVE, WALK_ENTER, WALK_LEAVE
	}

	/**
	 * Constructs a new {@code AbstractArea} within the given locations to
	 * create a cubic area region inside the specified {@code min} and
	 * {@code max} arguments.
	 * 
	 * @param min
	 *            the minimum location to start the cubic area from
	 * @param max
	 *            the maximum location to end the cubic area
	 */
	public AbstractArea(Location min, Location max) {
		super(min, max);

		Core.getServer().getEvents().register(this);
	}

	/**
	 * This method is executed when a state of this {@code AbstractArea} is
	 * changed.
	 * 
	 * @param mob
	 *            the mob changing its state in this area
	 * @param state
	 *            the state at which this area changed
	 */
	public abstract void onChange(Mob mob, AreaChangeState state);

	@EventHandler()
	public void onMove(MobMoveEvent event) {
		if (event.getMob() == null || event.getDestination() == null)
			return;
		Mob m = event.getMob();
		Position p = event.getDestination();
		if (contains(m) && !contains(p))
			onChange(m, AreaChangeState.WALK_LEAVE);
		if (!contains(m) && contains(p))
			onChange(m, AreaChangeState.WALK_ENTER);
	}

	@EventHandler()
	public void enterWorld(PlayerEnterWorldEvent event) {
		if (event.getMob() == null)
			return;
		if (contains(event.getMob()))
			onChange(event.getMob(), AreaChangeState.ENTER_WORLD);
	}

	@EventHandler()
	public void mapUpdate(PlayerMapUpdateEvent event) {
		if (event.getMob() == null || event.isInitialUpdate())
			return;
		if (contains(event.getMob()))
			onChange(event.getMob(), AreaChangeState.TELEPORT_ENTER);
		else
			onChange(event.getMob(), AreaChangeState.TELEPORT_LEAVE);
	}
}
