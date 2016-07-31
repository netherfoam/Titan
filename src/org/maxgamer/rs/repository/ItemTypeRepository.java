package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.ItemType;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author netherfoam
 */
public class ItemTypeRepository extends AbstractRepository<ItemType> {
    public ItemTypeRepository() {
        super(ItemType.class);
    }

    @SuppressWarnings("unchecked")
    public List<ItemType> findByName(String name) {
        return this.getManager().createQuery("FROM " + this.name() + " WHERE name = :name").setParameter("name", name).list();
    }

    /**
     * Find, at most, one {@link ItemType} with the given name. If more than one is found, the first one is returned.
     *
     * @param name the name of the item
     * @return the found item or null if none were found
     */
    public ItemType findOneByName(String name) {
        List<ItemType> list = this.getManager().createQuery("FROM " + this.name() + " WHERE name = :name ORDER BY id ASC").setParameter("name", name).list();
        if(list.isEmpty()) return null;

        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    public SortedMap<Integer, String> findNames() {
        List<Object[]> results = this.getManager().createSQLQuery("SELECT id, name FROM " + this.tableName() + " ORDER BY id ASC").list();
        SortedMap<Integer, String> map = new TreeMap<>();
        for(Object[] o : results){
            map.put(((Integer) o[0]), (String) o[1]);
        }
        return map;
    }
}
