package org.maxgamer.rs.model.map.spawns;

import java.util.UUID;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.structure.dbmodel.Mapping;
import org.maxgamer.rs.structure.dbmodel.Transparent;

public class NPCSpawn extends Transparent {
	@Mapping
	protected String id;
	@Mapping
	protected int npc_id;
	@Mapping
	protected String map;
	@Mapping
	protected short x;
	@Mapping
	protected short y;
	@Mapping
	protected byte z;
	
	public NPCSpawn(int id) {
		super("NPCSpawn", new String[]{"id"}, new Object[]{id});
	}
	
	public NPCSpawn(int npc_id, Location loc){
		super("npc_spawns", new String[]{"id"}, new Object[]{UUID.randomUUID().toString()});
		this.x = (short) loc.x;
		this.y = (short) loc.y;
		this.z = (byte) loc.z;
		this.map = loc.getMap().getName();
	}
	
	public Location getLocation(){
		return new Location(Core.getServer().getMaps().get(map), x, y, z);
	}
	
	public NPC spawn(){
		Location l = getLocation();
		if(l.getMap() == null){
			throw new IllegalStateException("Map not found: " + map + ", cannot spawn NPC.");
		}
		
		NPC npc = new NPC(npc_id, id, l);
		npc.setSpawn(l);
		return npc;
	}
}
