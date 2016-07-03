package org.maxgamer.rs.repository;

import org.maxgamer.rs.model.item.ItemType;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author netherfoam
 */
public class ItemTypeRepository extends Repository<ItemType> {
    public ItemTypeRepository() {
        super(ItemType.class);
    }

    @SuppressWarnings("unchecked")
    public List<ItemType> findByName(String name) {
        return this.getManager().createQuery("FROM " + this.name() + " WHERE name = :name").setParameter("name", name).list();
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
