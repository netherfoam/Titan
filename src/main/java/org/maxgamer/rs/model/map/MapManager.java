package org.maxgamer.rs.model.map;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.events.world.ChunkLoadEvent;
import org.maxgamer.rs.model.events.world.ChunkUnloadEvent;
import org.maxgamer.rs.model.map.spawns.NPCSpawn;
import org.maxgamer.rs.repository.NPCSpawnRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class MapManager implements EventListener, Iterable<WorldMap> {
    private File folder;
    private HashMap<String, WorldMap> worlds = new HashMap<String, WorldMap>(4);

    public MapManager(File folder) {
        this.folder = folder;
    }

    public void persist(WorldMap map) throws IOException {
        WorldMap old = worlds.get(map.getName());
        if (old == map) {
            throw new IllegalArgumentException("Map appears to already be persisted: " + old);
        }

        if (old != null) {
            throw new IllegalArgumentException("Maps have the same name " + map + " and " + old + " but are different maps.");
        }

        worlds.put(map.getName(), map);
    }

    public void unpersist(WorldMap map) throws IOException {
        File structure = new File(folder, map.getName() + MapStructure.EXTENSION);
        if (structure.exists()) {
            structure.delete();
        }
        worlds.remove(map.getName());
    }

    public boolean isPersisted(WorldMap map) {
        if (worlds.get(map.getName()) != null) {
            return true;
        }
        return false;
    }

    public void persist(NPC npc) {
        if (isPersisted(npc.getMap()) == false) {
            throw new IllegalArgumentException("Map is not persisted!");
        }

        Location loc = npc.getSpawn();

        if (loc == null) {
            throw new IllegalStateException("NPC has no spawn location set.");
        }


        NPCSpawn spawn = new NPCSpawn(npc.getId(), loc);
        Core.getServer().getDatabase().getSession().persist(spawn);
    }

    public void unpersist(NPC npc) {
        if (isPersisted(npc.getMap()) == false) {
            throw new IllegalArgumentException("Map is not persisted!");
        }

        Core.getServer().getDatabase().getRepository(NPCSpawnRepository.class).deleteById(npc.getSpawnId());
    }

    public WorldMap mainland() {
        return get("mainland");
    }

    public Iterator<WorldMap> iterator() {
        return new Iterator<WorldMap>() {
            Iterator<WorldMap> src = worlds.values().iterator();

            @Override
            public WorldMap next() {
                return src.next();
            }

            @Override
            public boolean hasNext() {
                return src.hasNext();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Fetches the given map. If it is not loaded, this loads the map. If the
     * map is not found or an {@link IOException} is raised during the loading
     * process, then this returns null. If an {@link IOException} is raised, the
     * message is printed to System.out
     *
     * @param name
     * @return the map or null if there was a problem loading it.
     */
    public WorldMap get(String name) {
        WorldMap map = worlds.get(name);
        if (map == null) {
            if (exists(name)) {
                try {
                    map = MapStructure.load(folder, name).read();
                    worlds.put(name, map);

					/* Load all of the NPC spawns */
                    map.init();
                } catch (IOException e) {
                    e.printStackTrace();
                    map = null;
                }
            } else {
                map = null;
            }
        }

        if (map == null) {
            return null;
        }

        return map;
    }

    public boolean exists(String name) {
        if (worlds.containsKey(name)) {
            return true;
        }

        File f = new File(folder, name + MapStructure.EXTENSION);
        if (f.exists()) {
            return true;
        }

        return false;
    }

    /**
     * Saves all loaded maps to disk. Returns the number of {@link IOException}
     * 's raised during saving process.
     *
     * @return
     */
    public int save() {
        // Write all loaded maps to disk
        int errors = 0;
        for (WorldMap map : worlds.values()) {
            try {
                this.save(map);
            } catch (IOException e) {
                errors++;
            }
        }
        return errors;
    }

    public void save(WorldMap map) throws IOException {
        MapStructure.save(folder, map);
    }

    @EventHandler(priority = EventPriority.LOW, skipIfCancelled = true)
    public void onLoad(ChunkLoadEvent e) {
        if (isPersisted(e.getMap()) == false) {
            return;
        }

        // TODO: This can be optimised by grouping chunks and then doing that selection instead of doing 16,000 odd queries for loading all of F2P
		/*
		try{
			Connection con = Core.getDatabase().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM npc_spawns WHERE map = ? AND x BETWEEN ? AND ? AND y BETWEEN ? AND ? AND z = ?");
			int x = e.getChunkX() << WorldMap.CHUNK_BITS;
			int y = e.getChunkY() << WorldMap.CHUNK_BITS;
			int z = e.getChunkZ();
			ps.setString(1, e.getMap().getName());
			ps.setInt(2, x);
			ps.setInt(3, x + WorldMap.CHUNK_SIZE - 1);
			
			ps.setInt(4, y);
			ps.setInt(5, y + WorldMap.CHUNK_SIZE - 1);
			
			ps.setInt(6, z);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				NPCSpawn spawn = new NPCSpawn(rs.getInt("id"));
				spawn.reload(rs);
				spawn.spawn();
			}
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}*/
    }

    @EventHandler(priority = EventPriority.HIGH, skipIfCancelled = true)
    public void onUnload(ChunkUnloadEvent e) {
        if (isPersisted(e.getMap()) == false) {
            return;
        }

        // TODO: Delete the NPC if it was spawned from the database
    }
}
