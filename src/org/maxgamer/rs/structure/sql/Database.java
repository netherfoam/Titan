package org.maxgamer.rs.structure.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author netherfoam
 */
public class Database {
	private DatabaseCore core;
	
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
				throw new ConnectionException("Database doesn not appear to be valid!");
			}
		}
		catch (AbstractMethodError e) {
			//You don't need to validate this core.
		}
		catch (Exception e) {
			throw new ConnectionException("Database doesn not appear to be valid!");
		}
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
	 * Represents a connection error, generally when the server can't connect to
	 * MySQL or something.
	 */
	public static class ConnectionException extends Exception {
		private static final long serialVersionUID = 8348749992936357317L;
		
		public ConnectionException(String msg) {
			super(msg);
		}
	}
}