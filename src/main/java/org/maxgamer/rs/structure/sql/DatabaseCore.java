package org.maxgamer.rs.structure.sql;

import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author netherfoam
 */
public interface DatabaseCore {
    Connection getConnection() throws SQLException;

    SessionFactory getSessionFactory(List<Class<?>> entities);

    void close();
}