package org.maxgamer.rs.structure.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author netherfoam
 */
public class MySQLCore implements DatabaseCore {
	private static class Con {
		private Connection c;
		private long lastUsed;
	}
	
	private HashMap<Long, Con> connections = new HashMap<>();
	private long timeout = 20 * 60 * 1000; //Timeout after 20 minutes.
	
	private String url;
	/** The connection properties... user, pass, autoReconnect.. */
	private Properties info;
	
	private final int MAX_CONNECTIONS = 8;
	private ArrayList<Connection> pool = new ArrayList<Connection>();
	
	public MySQLCore(String host, String user, String pass, String database, String port) {
		info = new Properties();
		info.put("autoReconnect", "true");
		info.put("user", user);
		info.put("password", pass);
		info.put("useUnicode", "true");
		info.put("characterEncoding", "utf8");
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		
		for (int i = 0; i < MAX_CONNECTIONS; i++){
			pool.add(null);
		}
	}
	
	private boolean validate(Con c) throws SQLException{
		if(c == null){
			return false;
		}
		
		if(c.c == null){
			return false;
		}
		
		if(c.c.isClosed()){
			return false;
		}
		
		try{
			if(c.c.isValid(10) == false){
				c.c.close();
				return false;
			}
		}
		catch(AbstractMethodError e){
			//SQLite
		}
		
		if(c.lastUsed + timeout < System.currentTimeMillis()){
			c.c.close();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Fetches the connection to this database for querying. Try to avoid doing
	 * this in the main thread. This gives each thread a separate connection. If
	 * the connection is closed, another is retrieved when this method is
	 * called.
	 * @return Fetches the connection to this database for querying.
	 */
	public Connection getConnection() throws SQLException {
		Thread t = Thread.currentThread();
		Con c = connections.get(Long.valueOf(t.getId()));
		
		if(validate(c) == false){
			c = null;
		}
		if (c == null) {
			c = new Con();
			c.c = this.getNewConnection();
			connections.put(Long.valueOf(t.getId()), c);
		}
		
		c.lastUsed = System.currentTimeMillis();
		
		return c.c;
	}
	
	/**
	 * Gets the database connection for executing queries on.
	 * @return The database connection
	 * @throws SQLException
	 */
	private Connection getNewConnection() {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			Connection connection = pool.get(i);
			try {
				//If we have a current connection, fetch it
				if (connection != null && !connection.isClosed()) {
					if (connection.isValid(10)) {
						return connection;
					}
					//Else, it is invalid, so we return another connection.
				}
				connection = DriverManager.getConnection(this.url, info);
				
				pool.set(i, connection);
				
				return connection;
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public void close() {
		//Nothing, because queries are executed immediately for MySQL
	}
}