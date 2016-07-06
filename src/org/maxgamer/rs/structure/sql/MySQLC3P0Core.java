package org.maxgamer.rs.structure.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author netherfoam
 */
public class MySQLC3P0Core implements DatabaseCore {
	private static class Con {
		private Connection c;
		private long lastUsed;
	}
	
	private HashMap<Long, Con> connections = new HashMap<Long, Con>();
	private long timeout = 20 * 60 * 1000; //Timeout after 20 minutes.
	
	private ComboPooledDataSource pool;

	private Properties entityManagerProperties;
	
	public MySQLC3P0Core(String host, String user, String pass, String database, String port)
	{
		System.getProperties().setProperty("c3p0.testConnectionOnCheckout", "true");
		// This removes the debug spam from the C3P0 logger
		Properties p = new Properties(System.getProperties());
		p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperties(p);
		
		pool = new ComboPooledDataSource();
		try {
			pool.setDriverClass("com.mysql.jdbc.Driver");
		}
		catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		pool.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
		pool.setUser(user);
		pool.setPassword(pass);
		pool.setMinPoolSize(3);
		pool.setAcquireIncrement(5);
		pool.getProperties().setProperty("autoReconnect", "true");
		pool.setMaxPoolSize(20);

        this.entityManagerProperties = new Properties();
        this.entityManagerProperties.put("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true");
        this.entityManagerProperties.put("hibernate.connection.username", user);
        this.entityManagerProperties.put("hibernate.connection.password", pass);
        this.entityManagerProperties.put("hibernate.hbm2ddl.auto", "");
		this.entityManagerProperties.put("hibernate.c3p0.idle_test_period", "300");

        // For persistent session storage
        this.entityManagerProperties.put("hibernate.current_session_context_class", "managed");
        this.entityManagerProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        this.entityManagerProperties.put("hibernate.archive.autodetection", "true");
	}

	public SessionFactory getSessionFactory(List<Class<?>> entities) {
        Configuration configuration = new Configuration();
        configuration.addProperties(this.entityManagerProperties);

        for(Class<?> type : entities) {
            configuration.addAnnotatedClass(type);
        }

        return configuration.buildSessionFactory();
	}
	
	public int prune() {
		int n = 0;
		Iterator<Entry<Long, Con>> cit = connections.entrySet().iterator();
		while (cit.hasNext()) {
			Entry<Long, Con> entry = cit.next();
			Con con = entry.getValue();
			if (con.lastUsed + timeout < System.currentTimeMillis()) {
				try {
					con.c.close();
				}
				catch (SQLException e) {
					
				}
				cit.remove();
			}
		}
		return n;
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
	public Connection getNewConnection() {
		try {
			Connection con = pool.getConnection();
			if (con.isValid(5) == false) {
				con.close();
				con = pool.getConnection();
			}
			return con;
		}
		catch (SQLException e) {
			System.out.println("Failed to acquire connection. Details: " + pool.getUser() + ", " + pool.getPassword() + ", " + pool.getJdbcUrl());
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void close() {
		//Nothing, because queries are executed immediately for MySQL
	}
}