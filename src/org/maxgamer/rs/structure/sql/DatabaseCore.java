package org.maxgamer.rs.structure.sql;

import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author netherfoam
 */
public interface DatabaseCore {
	public Connection getConnection() throws SQLException;
	public SessionFactory getSessionFactory(List<Class<?>> entities);
	public void close();
}