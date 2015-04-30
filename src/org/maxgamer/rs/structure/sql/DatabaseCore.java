package org.maxgamer.rs.structure.sql;

import java.sql.Connection;

/**
 * @author netherfoam
 */
public interface DatabaseCore {
	public Connection getConnection();
	
	public void queue(BufferStatement bs);
	
	public void flush();
	
	public void close();
}