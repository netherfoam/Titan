package org.maxgamer.rs.structure.sql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.maxgamer.rs.repository.AbstractRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class Database {
    /**
     * Represents a connection error, generally when the server can't connect to
     * MySQL or something.
     */
    public static class ConnectionException extends Exception {
        private static final long serialVersionUID = 8348749992936357317L;

        public ConnectionException(String msg) {
            super(msg);
        }
    }

    private DatabaseCore core;
    private HashMap<Class<? extends AbstractRepository>, AbstractRepository<?>> repositories = new HashMap<Class<? extends AbstractRepository>, AbstractRepository<?>>();
    private ArrayList<Class<?>> managedEntities = new ArrayList<Class<?>>();
    private SessionFactory sessionFactory;
    private Session session;

    /**
     * Creates a new database and validates its connection.
     * <p>
     * If the connection is invalid, this will throw a ConnectionException.
     *
     * @param core The core for the database, either MySQL or SQLite.
     * @throws ConnectionException If the connection was invalid
     */
    public Database(DatabaseCore core) throws ConnectionException {
        this.core = core;
    }

    public <T> void addRepository(AbstractRepository<T> repository) {
        if (repository.getDatabase() != null) {
            throw new IllegalArgumentException("Repository " + repository + " already has a database assigned.");
        }

        this.repositories.put(repository.getClass(), repository);
        if (!this.managedEntities.contains(repository.getType())) {
            this.addEntity(repository.getType());
        }
        repository.setDatabase(this);
    }

    public void removeRepository(Class<? extends AbstractRepository> type) {
        AbstractRepository<?> r = this.repositories.remove(type);
        if (r != null && r.getDatabase() == this) {
            r.setDatabase(null);
        }
    }

    @SuppressWarnings("unchecked")
    public <R extends AbstractRepository> R getRepository(Class<R> type) {
        return (R) repositories.get(type);
    }

    public void addEntity(Class<?> type) {
        if (this.sessionFactory != null) {
            throw new IllegalStateException("SessionFactory has already been instantiated. Too late");
        }
        if (this.managedEntities.contains(type)) {
            throw new IllegalArgumentException("Entity type " + type.getName() + " is already managed.");
        }

        this.managedEntities.add(type);
    }

    /**
     * Returns the database core object, that this database runs on.
     *
     * @return the database core object, that this database runs on.
     */
    public DatabaseCore getCore() {
        return core;
    }

    /**
     * Fetches the connection to this database for querying. Try to avoid doing
     * this in the main thread. This gives each thread a separate connection. If
     * the connection is closed, another is retrieved when this method is
     * called.
     *
     * @return Fetches the connection to this database for querying.
     */
    public Connection getConnection() throws SQLException {
        return core.getConnection();
    }

    /**
     * Closes the database
     */
    public void close() {
        this.core.close();
    }

    /**
     * The {@link EntityManager} for this Database. This will automatically begin a transaction,
     * which will be flushed when the next server tick completes, if it was used.
     *
     * @return The {@link EntityManager} for this Database
     */
    public synchronized Session getSession() {
        if (this.session == null || this.session.isOpen() == false) {
            this.session = this.getSessionFactory().openSession();
        }

        return this.session;
    }

    /**
     * Lazily initializes the {@link EntityManagerFactory}
     *
     * @return the {@link EntityManagerFactory}
     */
    private SessionFactory getSessionFactory() {
        if (this.sessionFactory == null) {
            this.sessionFactory = core.getSessionFactory(this.managedEntities);

        }
        return this.sessionFactory;
    }

    /**
     * Flushes the {@link EntityManager}, if it has been used and not committed
     */
    public void flush() {

    }
}