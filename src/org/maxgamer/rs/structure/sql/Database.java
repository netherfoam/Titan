package org.maxgamer.rs.structure.sql;

import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.maxgamer.rs.repository.Repository;

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
	private HashMap<Class<? extends Repository>, Repository<?>> repositories = new HashMap<Class<? extends Repository>, Repository<?>>();
    private ArrayList<String> managedEntities = new ArrayList<String>();
    private HibernateEntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
	
	/**
	 * Creates a new database and validates its connection.
	 * 
	 * If the connection is invalid, this will throw a ConnectionException.
	 * @param core The core for the database, either MySQL or SQLite.
	 * @throws ConnectionException If the connection was invalid
	 */
	public Database(DatabaseCore core) throws ConnectionException {
		this.core = core;
		
		try {
			if (!getConnection().isValid(10)) {
				throw new ConnectionException("Database doesn't not appear to be valid!");
			}
		}
		catch (AbstractMethodError e) {
			//You don't need to validate this core.
		}
		catch (Exception e) {
			throw new ConnectionException("Database doesn't not appear to be valid!");
		}
	}

    public <T> void addRepository(Repository<T> repository) {
        if(repository.getDatabase() != null) {
            throw new IllegalArgumentException("Repository " + repository + " already has a database assigned.");
        }

        this.repositories.put(repository.getClass(), repository);
        if(this.managedEntities.contains(repository.getType().getName()) == false) {
            this.addEntity(repository.getType());
        }
        repository.setDatabase(this);
    }

    public void removeRepository(Class<? extends Repository> type) {
        Repository<?> r = this.repositories.remove(type);
        if(r != null && r.getDatabase() == this) {
            r.setDatabase(null);
        }
    }

	@SuppressWarnings("unchecked")
	public <R extends Repository> R getRepository(Class<R> type) {
		return (R) repositories.get(type);
	}

    public void addEntity(Class<?> type){
        if(this.managedEntities.contains(type.getName())) {
            throw new IllegalArgumentException("Entity type " + type.getName() + " is already managed.");
        }

        if(this.entityManager == null || this.entityManager.isOpen() == false) {
            // The entity manager is not set up yet. No need to reset it.
            this.managedEntities.add(type.getName());
            return;
        }

        if(this.entityManager.getTransaction().isActive()){
            this.entityManager.getTransaction().commit();
            this.entityManager.flush();
            this.entityManager.close();
        }
        this.entityManager = null;

        if(this.entityManagerFactory != null && this.entityManagerFactory.isOpen()){
            this.entityManagerFactory.getSessionFactory().close();
        }
        this.entityManagerFactory = null;

        this.managedEntities.add(type.getName());
    }

    public void removeEntity(Class<?> type){
        if(this.entityManager != null && this.entityManager.isOpen() && this.entityManager.getTransaction().isActive()){
            throw new IllegalStateException("EntityManager transaction in progress. Can't unregister a type.");
        }
        this.entityManager = null;

        if(this.entityManagerFactory != null && this.entityManagerFactory.isOpen()){
            this.entityManagerFactory.close();
        }
        this.entityManagerFactory = null;

        this.managedEntities.remove(type.getName());
    }
	
	/**
	 * Returns the database core object, that this database runs on.
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
	public synchronized EntityManager getEntityManager() {
		if(this.entityManager == null || this.entityManager.isOpen() == false) {
            this.entityManager = this.getEntityManagerFactory().createEntityManager();
		}

        if(this.entityManager.getTransaction().isActive() == false){
            this.entityManager.getTransaction().begin();
        }

		return this.entityManager;
	}

    /**
     * Lazily initializes the {@link EntityManagerFactory}
     *
     * @return the {@link EntityManagerFactory}
     */
    private EntityManagerFactory getEntityManagerFactory() {
        if(this.entityManagerFactory == null || this.entityManagerFactory.isOpen() == false){
            this.entityManagerFactory = core.getEntityManagerFactory(this.managedEntities);

        }
        return this.entityManagerFactory;
    }

    /**
     * Flushes the {@link EntityManager}, if it has been used and not committed
     */
    public void flush() {
        if(this.entityManager == null || this.entityManager.isOpen() == false) {
            return;
        }

        if(this.entityManager.getTransaction().isActive()) {
            this.entityManager.getTransaction().commit();
        }
    }
}