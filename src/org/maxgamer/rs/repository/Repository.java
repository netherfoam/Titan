package org.maxgamer.rs.repository;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.structure.sql.Database;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import java.util.List;

/**
 * @author netherfoam
 */
public class Repository<T> {
    // TODO: Document this class
    private Class<T> type;
    private Entity annotation;
    private Table table;

    public Repository(Class<T> type) {
        this.type = type;
        this.annotation = type.getAnnotation(Entity.class);
        this.table = type.getAnnotation(Table.class);
        if(this.annotation == null) throw new IllegalArgumentException("Class " + type.getName() + " is not annotated with @" + Entity.class);
    }

    public Class<T> getType() {
        return this.type;
    }

    public T find(Object id) {
        return getManager().find(type, id);
    }

    public List<T> findAll() {
        return getManager().createQuery("FROM " + name()).getResultList();
    }

    public Database getDatabase() {
        return Core.getWorldDatabase();
    }

    protected EntityManager getManager() {
        return this.getDatabase().getEntityManager();
    }

    protected String name() {
        if(this.annotation.name().isEmpty() == false){
            return this.annotation.name();
        }
        return this.type.getSimpleName();
    }

    protected String tableName() {
        if(this.table != null && this.table.name().isEmpty() == false) {
            return this.table.name();
        }
        return name();
    }
}
