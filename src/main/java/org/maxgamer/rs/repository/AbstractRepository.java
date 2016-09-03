package org.maxgamer.rs.repository;

import org.hibernate.Session;
import org.maxgamer.rs.structure.sql.Database;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Base class to represent a table that can be queried in Hibernate
 *
 * @author netherfoam
 */
public abstract class AbstractRepository<T> {
    /**
     * The type of Entity stored
     */
    private Class<T> type;

    /**
     * The @Entity annotation on the entity class
     */
    private Entity annotation;

    /**
     * The @Table annotation on the entity class
     */
    private Table table;

    /**
     * The database that this repository belongs to
     */
    private Database database;

    /**
     * Constructs a new repository for the given type. This is not initialized with a database.
     *
     * @param type the type of entity
     * @throws IllegalArgumentException if the given class has no @Entity annotation
     */
    public AbstractRepository(Class<T> type) {
        this.type = type;
        this.annotation = type.getAnnotation(Entity.class);
        this.table = type.getAnnotation(Table.class);
        if (this.annotation == null) throw new IllegalArgumentException("Class " + type.getName() + " is not annotated with @" + Entity.class);
    }

    /**
     * The database this repository is connected to
     *
     * @return the database
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * Sets the database on this repository. This is done when by the Database when adding the repository to it
     *
     * @param db the database
     */
    public void setDatabase(Database db) {
        this.database = db;
    }

    /**
     * The managed entity type given in the constructor
     *
     * @return The managed entity type given in the constructor
     */
    public Class<T> getType() {
        return this.type;
    }

    /**
     * Finds the entity with the given ID
     *
     * @param id the id
     * @return the entity
     */
    public T find(Serializable id) {
        return getManager().get(type, id);
    }

    /**
     * Finds all entities in this repository
     *
     * @return all entities in this repository
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return (List<T>) getManager().createQuery("FROM " + name()).list();
    }

    /**
     * The hibernate session
     *
     * @return The hibernate session
     */
    protected Session getManager() {
        return this.getDatabase().getSession();
    }

    /**
     * The name of the managed entity
     *
     * @return the name of the managed entity
     */
    protected String name() {
        if (this.annotation.name().isEmpty() == false) {
            return this.annotation.name();
        }
        return this.type.getSimpleName();
    }

    /**
     * The physical table name of this entity
     *
     * @return The physical table name of this entity
     */
    protected String tableName() {
        if (this.table != null && this.table.name().isEmpty() == false) {
            return this.table.name();
        }
        return name();
    }
}
