package org.maxgamer.rs.structure.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author netherfoam
 */
public interface DatabaseCore {
	public Connection getConnection() throws SQLException;
	
	public void close();
}