package org.maxgamer.rs.structure.sql;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author netherfoam
 */
public class MySQLC3P0Core implements DatabaseCore {
	private ComboPooledDataSource pool;
	
	public MySQLC3P0Core(String host, String user, String pass, String database, String port) {
		//This removes the debug spam from the C3P0 logger
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
	}
	
	/**
	 * Gets the database connection for executing queries on.
	 * @return The database connection
	 * @throws SQLException
	 */
	public Connection getConnection() {
		try {
			Connection con = pool.getConnection();
			if (con.isValid(5)) {
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
	public void queue(BufferStatement bs) {
		try {
			Connection con = this.getConnection();
			PreparedStatement ps = bs.prepareStatement(con);
			ps.execute();
			ps.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public void close() {
		//Nothing, because queries are executed immediately for MySQL
	}
	
	@Override
	public void flush() {
		//Nothing, because queries are executed immediately for MySQL
	}
}