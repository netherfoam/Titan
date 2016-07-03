package org.maxgamer.rs.repository;

import org.hibernate.Session;
import org.maxgamer.rs.structure.sql.Database;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * @author netherfoam
 */
public class Repository<T> {
    // TODO: Document this class
    private Class<T> type;
    private Entity annotation;
    private Table table;
    private Database database;

    public Repository(Class<T> type) {
        this.type = type;
        this.annotation = type.getAnnotation(Entity.class);
        this.table = type.getAnnotation(Table.class);
        if(this.annotation == null) throw new IllegalArgumentException("Class " + type.getName() + " is not annotated with @" + Entity.class);
    }

    public void setDatabase(Database db) {
        this.database = db;
    }

    public Database getDatabase() {
        return this.database;
    }

    public Class<T> getType() {
        return this.type;
    }

    public T find(Serializable id) {
        return getManager().get(type, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return (List<T>) getManager().createQuery("FROM " + name()).list();
    }

    protected Session getManager() {
        return this.getDatabase().getSession();
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
