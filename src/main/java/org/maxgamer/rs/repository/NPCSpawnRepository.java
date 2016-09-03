package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.map.spawns.NPCSpawn;

import java.util.List;

/**
 * @author netherfoam
 */
public class NPCSpawnRepository extends AbstractRepository<NPCSpawn> {
    public NPCSpawnRepository() {
        super(NPCSpawn.class);
    }

    @SuppressWarnings("unchecked")
    public List<NPCSpawn> findAllByMap(String map) {
        return (List<NPCSpawn>) getManager().createQuery("FROM " + name() + " WHERE map = :map").setParameter("map", map).list();
    }

    public void deleteById(long id) {
        NPCSpawn spawn = this.find(id);
        if (spawn != null) {
            this.getManager().delete(spawn);
        }
    }
}
