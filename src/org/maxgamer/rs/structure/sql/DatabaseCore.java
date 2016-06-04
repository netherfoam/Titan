package org.maxgamer.rs.structure.sql;

import org.hibernate.jpa.internal.EntityManagerFactoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author netherfoam
 */
public interface DatabaseCore {
	public Connection getConnection() throws SQLException;
	public EntityManagerFactoryImpl getEntityManagerFactory(List<String> entities);
	public void close();
}