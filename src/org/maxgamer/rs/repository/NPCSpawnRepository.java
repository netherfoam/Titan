package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.map.spawns.NPCSpawn;

import java.util.List;

/**
 * @author netherfoam
 */
public class NPCSpawnRepository extends Repository<NPCSpawn> {
    public NPCSpawnRepository() {
        super(NPCSpawn.class);
    }

    public List<NPCSpawn> findAllByMap(String map) {
        return getManager().createQuery("FROM " + name() + " WHERE map = :map").setParameter("map", map).getResultList();
    }

    public void deleteById(long id) {
        NPCSpawn spawn = this.find(id);
        if(spawn != null) {
            this.getManager().remove(spawn);
        }
    }
}
