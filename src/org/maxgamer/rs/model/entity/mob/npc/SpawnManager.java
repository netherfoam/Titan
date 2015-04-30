package org.maxgamer.rs.model.entity.mob.npc;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;

import org.maxgamer.event.EventHandler;
import org.maxgamer.event.EventListener;
import org.maxgamer.event.EventPriority;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.events.world.ChunkLoadEvent;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.structure.timings.StopWatch;

/**
 * @author netherfoam
 */
public class SpawnManager implements EventListener {
	public static void convert() throws IOException, SQLException {
		Connection con = Core.getWorldDatabase().getConnection();
		
		PreparedStatement ps = con.prepareStatement("INSERT INTO npc_spawns (npc, x, y, z) VALUES (?, ?, ?, ?)");
		
		Scanner sc = new Scanner(new File("data", "unpackedSpawnsList [more].txt"));
		while (sc.hasNextLine()) {
			String l = sc.nextLine();
			if (l.contains("//")) continue; //Comment
			
			String[] parts = l.split(" ");
			//if(parts.length != 5){
			//Log.debug("Line " + l + " unrecognised");
			//}
			int npcId = Integer.parseInt(parts[0]);
			int x = Integer.parseInt(parts[2]); //parts[1] is a -
			int y = Integer.parseInt(parts[3]);
			int z = Integer.parseInt(parts[4]);
			
			ps.setInt(1, npcId);
			ps.setInt(2, x);
			ps.setInt(3, y);
			ps.setInt(4, z);
			ps.addBatch();
		}
		sc.close();
		ps.executeBatch();
	}
	
	public static void loadAll() throws SQLException, IOException {
		//convert();
		try {
			Log.info("Loading Mob Spawns...");
			Connection con = Core.getWorldDatabase().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM npc_spawns");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int npcId = rs.getInt("npc");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				
				Location loc = new Location(Core.getServer().getMap(), x, y, z);
				
				//If the NPC is standing on a fully clipped tile, we wish to move them off of it.
				//This section of code will move the npc within 5 tiles if possible. Once an unclipped
				//location is found, the location is changed and updated in the database. If the whole
				//area is clipped, this won't have any effect and the NPC will be spawned in the clipped
				//area anyway.
				Core.getServer().getMap().load(loc.x, loc.y);
				boolean change = false;
				loop: if ((loc.getClip() & (ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_ALL | ClipMasks.WALL_ALL)) != 0) {
					for (int i = 0; i <= 5; i++) {
						for (int j = 0; j <= 5; j++) {
							Location l = loc.add(i, j);
							if ((l.getClip() & (ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_ALL | ClipMasks.WALL_ALL)) == 0) {
								loc = l;
								change = true;
								break loop;
							}
							
							l = loc.add(-i, -j);
							if ((l.getClip() & (ClipMasks.OBJECT_BLOCK | ClipMasks.BLOCKED_ALL | ClipMasks.WALL_ALL)) == 0) {
								loc = l;
								change = true;
								break loop;
							}
						}
					}
				}
				
				if (change) {
					Log.info("Correcting spawn location for spawnId " + rs.getInt("spawnId") + " to " + loc);
					PreparedStatement fix = con.prepareStatement("UPDATE npc_spawns SET x = ?, y = ? WHERE spawnId = ?");
					fix.setInt(1, loc.x);
					fix.setInt(2, loc.y);
					fix.setInt(3, rs.getInt("spawnId"));
					fix.execute();
				}
				
				try {
					NPC npc = new NPC(npcId, loc);
					npc.setSpawn(loc);
				}
				catch (WorldFullException e) {
					Log.warning("World is full, failed to spawn NPC " + npcId + ". Skipping remainder.");
					return;
				}
				catch (Exception e) {
					Log.debug("Bad NPC ID in spawns database being deleted: " + npcId);
					con.prepareStatement("DELETE FROM npc_spawns WHERE npc = " + npcId).execute();
					continue;
				}
			}
			rs.close();
			ps.close();
			Log.debug("Spawns loaded!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HashSet<Integer> loaded = new HashSet<>(2048);
	
	@EventHandler(priority = EventPriority.LOW)
	public void onLoad(ChunkLoadEvent e) {
		//We assume that the ground will be loaded if the Z value is loaded. 
		//Safe to assume, for now.
		if (e.getChunkZ() != 0) {
			return;
		}
		
		StopWatch w = Core.getTimings().start("spawn-manager");
		try {
			int key = (e.getChunkX() << 16) | (e.getChunkY());
			if (loaded.add(key)) {
				//Zone is new.
				try {
					Connection con = Core.getWorldDatabase().getConnection();
					PreparedStatement ps = con.prepareStatement("SELECT * FROM npc_spawns WHERE x BETWEEN ? AND ? AND y BETWEEN ? AND ?");
					
					ps.setInt(1, (e.getChunkX() << 3));
					ps.setInt(2, (e.getChunkX() << 3) + 7);
					ps.setInt(3, (e.getChunkY() << 3));
					ps.setInt(4, (e.getChunkY() << 3) + 7);
					
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						int npcId = rs.getInt("npc");
						int x = rs.getInt("x");
						int y = rs.getInt("y");
						int z = rs.getInt("z");
						
						Location loc = new Location(Core.getServer().getMap(), x, y, z);
						
						try {
							NPC npc = new NPC(npcId, loc);
							npc.setSpawn(loc);
						}
						catch (WorldFullException ex) {
							Log.warning("World is full, failed to spawn NPC " + npcId + ". Skipping remainder.");
							return;
						}
						catch (Exception ex) {
							ex.printStackTrace();
							Log.debug("Bad NPC ID in spawns database being deleted: " + npcId);
							con.prepareStatement("DELETE FROM npc_spawns WHERE npc = " + npcId).execute();
							continue;
						}
					}
				}
				catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
		finally {
			w.stop();
		}
	}
}