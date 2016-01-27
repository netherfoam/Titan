package org.maxgamer.rs.model.map.area;

import java.util.Collection;
import java.util.HashSet;

import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobMoveEvent;
import org.maxgamer.rs.model.events.mob.MobTeleportEvent;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerEnterWorldEvent;
import org.maxgamer.rs.model.events.mob.persona.player.PlayerLeaveWorldEvent;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.WorldMap;
import org.maxgamer.rs.model.map.area.Area.AreaChangeState;
import org.maxgamer.rs.structure.areagrid.AreaGrid;
import org.maxgamer.rs.structure.areagrid.MBR;

/**
 * @author netherfoam
 */
public class AreaManager implements EventListener{
	private WorldMap map;
	private AreaGrid<Area> areas;
	
	public AreaManager(WorldMap map) {
		this.map = map;
		this.areas = new AreaGrid<Area>(map.width(), map.height(), WorldMap.CHUNK_SIZE);
	}
	
	public HashSet<Area> getAreas(MBR overlap) {
		return areas.get(overlap, 16);
	}
	
	public void add(Area a) {
		if (a.getMap() != this.map) throw new IllegalArgumentException("Cannot add an area from one map to an AreaManager of a different map.");
		this.areas.put(a, a);
		
		for(Mob mob : map.getEntities(a, 20, Mob.class)){
			a.onEnter(mob, AreaChangeState.SERVER);
		}
	}
	
	public void remove(Area a) {
		this.areas.remove(a, a);
		
		for(Mob mob : map.getEntities(a, 20, Mob.class)){
			a.onLeave(mob, AreaChangeState.SERVER);
		}
	}
	
	@EventHandler(consumer = false, skipIfCancelled = true, priority = EventPriority.MONITOR)
	public void onMove(MobMoveEvent e) {
		move(e.getMob(), e.getFrom(), e.getTo(), AreaChangeState.WALK);
	}
	
	@EventHandler(consumer = false, skipIfCancelled = true, priority = EventPriority.MONITOR)
	public void onTeleport(MobTeleportEvent e) {
		move(e.getMob(), e.getFrom(), e.getTo(), AreaChangeState.TELEPORT);
	}
	
	@EventHandler(consumer = false, skipIfCancelled = true, priority = EventPriority.MONITOR)
	public void onQuit(PlayerLeaveWorldEvent e){
		for(Area a : areas.get(e.getMob().getLocation(), 4)){
			a.onLeave(e.getMob(), AreaChangeState.SERVER);
		}
	}
	
	@EventHandler(consumer = false, skipIfCancelled = true, priority = EventPriority.MONITOR)
	public void onJoin(PlayerEnterWorldEvent e){
		for(Area a : areas.get(e.getMob().getLocation(), 4)){
			a.onEnter(e.getMob(), AreaChangeState.SERVER);
		}
	}
	
	private void move(Mob m, Position src, Position dst, AreaChangeState type){
		Collection<Area> source = areas.get(src, 4);
		Collection<Area> dest = areas.get(dst, 4);
		
		for(Area a : source){
			if(dest.remove(a)){
				// No area change has occurred
				continue;
			}
			
			a.onLeave(m, AreaChangeState.WALK);
		}
		
		for(Area a : dest){
			a.onEnter(m, AreaChangeState.WALK);
		}
	}
}