package org.maxgamer.rs.model.map.spawns;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.util.Log;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.util.UUID;

@Entity
@Table(name = "NPCSpawn")
public class NPCSpawn {
	@Id
	protected long id;

	@Column
	protected int npc_id;

	@Column(nullable = false)
	protected String map;

	@Column(nullable = false)
	protected short x;

	@Column(nullable = false)
	protected short y;

	@Column(nullable = false)
	protected byte z;

	public NPCSpawn() {

	}
	
	public NPCSpawn(long id) {
		this();
		this.id = id;
	}
	
	public NPCSpawn(int npc_id, Location loc){
		this(UUID.randomUUID().getLeastSignificantBits());

		this.npc_id = npc_id;
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
		
		NPC npc;
		try{
			npc = new NPC(npc_id, id, l);
		}
		catch(RuntimeException e){
			if(e.getCause() instanceof FileNotFoundException){
				Log.warning("NPC missing from cache. ID: " + id + ", NPC_ID: " + npc_id + " at " + l);
				return null;
			}
			else{
				throw e;
			}
		}

		npc.setSpawn(l);
		return npc;
	}
}
