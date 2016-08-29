package org.maxgamer.rs.structure.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author netherfoam
 */
public class MySQLC3P0Core implements DatabaseCore {
    /**
     * The timeout for connections - 20 minutes
     */
	private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(20);

    /**
     * The {@link javax.sql.DataSource} we use to connect to the database internally
     */
	private ComboPooledDataSource pool;

    /**
     * The flyway object we use to migrate our database
     */
    private Flyway flyway;

	/**
	 * Constructs a new MySQL database core backed by a C3P0 {@link ComboPooledDataSource}
	 * @param host the host to connect to
	 * @param user the user to connect as
	 * @param pass the password to connect with
	 * @param database the name of the database schema to use
     * @param port the port to use
     */
	public MySQLC3P0Core(String host, String user, String pass, String database, String port) {
		pool = new ComboPooledDataSource();
		pool.setTestConnectionOnCheckout(true);

        try {
            pool.setDriverClass("com.mysql.jdbc.Driver");
        }
        catch(PropertyVetoException e) {
            throw new RuntimeException("Unable to find MySQL driver", e);
        }
		
		pool.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
		pool.setUser(user);
		pool.setPassword(pass);
		pool.setMinPoolSize(3);
		pool.setAcquireIncrement(5);
		pool.getProperties().setProperty("autoReconnect", "true");
		pool.setMaxPoolSize(20);

        flyway = new Flyway();
        flyway.setDataSource(pool);

        // Configures flyway such that, if it is missing it's metadata table, it will
        // accept the current configuration as the latest version (Eg. this time, no
        // migrations will run)
        flyway.setBaselineOnMigrate(true);

        // Perform any necessary migrations
        flyway.migrate();
	}

	/**
	 * Initializes hibernate configuration parameters to use C3P0
	 * @param hibernateProperties the properties to configure
     */
	private void configure(Properties hibernateProperties) {
		hibernateProperties.put("hibernate.connection.url", pool.getJdbcUrl());
		hibernateProperties.put("hibernate.connection.username", pool.getUser());
		hibernateProperties.put("hibernate.connection.password", pool.getPassword());
		hibernateProperties.put("hibernate.hbm2ddl.auto", "");

		// For C3p0
		hibernateProperties.put("hibernate.c3p0.min_size", "5");
		hibernateProperties.put("hibernate.c3p0.max_size", "20");
		hibernateProperties.put("hibernate.c3p0.testConnectionOnCheckout", "true");
		hibernateProperties.put("hibernate.c3p0.timeout", String.valueOf(TIMEOUT));
		hibernateProperties.put("hibernate.connection.provider_class", "org.hibernate.c3p0.internal.C3P0ConnectionProvider");
		hibernateProperties.put("hibernate.c3p0.idle_test_period", String.valueOf(TIMEOUT));
		hibernateProperties.put("hibernate.connection.autoReconnect", "true");
		hibernateProperties.put("hibernate.connection.driver_class", pool.getDriverClass());

		// For persistent session storage
		hibernateProperties.put("hibernate.current_session_context_class", "managed");
		hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
		hibernateProperties.put("hibernate.archive.autodetection", "true");
	}

	/**
	 * Creates a session factory for the given list of entities
	 * @param entities the list of entities to create the factory for
	 * @return the factory
     */
	@Override
	public SessionFactory getSessionFactory(List<Class<?>> entities) {
		Properties properties = new Properties();
		configure(properties);

        Configuration configuration = new Configuration();
        configuration.addProperties(properties);

        for(Class<?> type : entities) {
            configuration.addAnnotatedClass(type);
        }

        configuration.setInterceptor(new HibernateJPAListener());

        return configuration.buildSessionFactory();
	}
	
	/**
	 * Fetches the connection to this database for querying. Try to avoid doing
	 * this in the main thread. This gives each thread a separate connection. If
	 * the connection is closed, another is retrieved when this method is
	 * called.
	 * @return Fetches the connection to this database for querying.
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}
	
	@Override
	public void close() {
		//Nothing, because queries are executed immediately for MySQL
	}
}