package org.maxgamer.rs.structure.sql;

import com.vladmihalcea.book.hpjp.util.PersistenceUnitOfInfoImpl;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.maxgamer.rs.core.Core;

import javax.persistence.spi.PersistenceUnitInfo;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author netherfoam
 */
public class SQLiteCore implements DatabaseCore {
	private Connection connection;
	private File dbFile;
	private Properties entityManagerProperties;
	
	public SQLiteCore(File dbFile) {
		this.dbFile = dbFile;

		this.entityManagerProperties = new Properties();
		this.entityManagerProperties.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + this.dbFile);
		this.entityManagerProperties.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
	}

	public EntityManagerFactoryImpl getEntityManagerFactory(List<String> entities) {
		PersistenceUnitInfo info = new PersistenceUnitOfInfoImpl(Core.class.getSimpleName(), entities, this.entityManagerProperties);
		Map<String, Object> configuration = new HashMap<>();

		EntityManagerFactoryImpl factory = (EntityManagerFactoryImpl) new EntityManagerFactoryBuilderImpl(
				new PersistenceUnitInfoDescriptor(info), configuration
		).build();

		return factory;
	}
	
	/**
	 * Gets the database connection for executing queries on.
	 * @return The database connection
	 */
	public Connection getConnection() {
		try {
			//If we have a current connection, fetch it
			if (this.connection != null && !this.connection.isClosed()) {
				return this.connection;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if (this.dbFile.exists()) {
			//So we need a new connection
			try {
				Class.forName("org.sqlite.JDBC");
				Properties properties = new Properties();
				this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFile, properties);
				return this.connection;
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			//So we need a new file too.
			try {
				//Create the file
				this.dbFile.createNewFile();
				//Now we won't need a new file, just a connection.
				//This will return that new connection.
				return this.getConnection();
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	@Override
	public void close() {
		try {
			this.connection.close();
		}
		catch (SQLException e) {}
	}
}