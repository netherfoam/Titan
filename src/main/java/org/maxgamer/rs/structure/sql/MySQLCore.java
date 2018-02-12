package org.maxgamer.rs.structure.sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author netherfoam
 */
public class MySQLCore implements DatabaseCore {
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    /**
     * The timeout for connections - 20 minutes
     */
    private static final long TIMEOUT = TimeUnit.MINUTES.toSeconds(60);

    private ThreadLocal<Connection> connections = new ThreadLocal<>();

    private String host;
    private String user;
    private String pass;
    private String database;
    private int port;

    /**
     * The flyway object we use to migrate our database
     */
    private Flyway flyway;

    private MysqlDataSource dataSource;

    /**
     * Constructs a new MySQL database core
     *
     * @param host     the host to connect to
     * @param user     the user to connect as
     * @param pass     the password to connect with
     * @param database the name of the database schema to use
     * @param port     the port to use
     */
    public MySQLCore(String host, String user, String pass, String database, String port) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.database = database;
        this.port = Integer.parseInt(port);

        System.out.println("Database URL: " + "jdbc:mysql://" + host + ":" + port + "/" + database);
        System.out.println("Database User: " + user);
        System.out.println("Database Pass: " + pass.replaceAll(".", "*"));

        flyway = new Flyway();
        flyway.setDataSource(getDataSource());

        // Configures flyway such that, if it is missing it's metadata table, it will
        // accept the current configuration as the latest version (Eg. this time, no
        // migrations will run)
        flyway.setBaselineOnMigrate(true);

        // Perform any necessary migrations
        flyway.migrate();
    }

    /**
     * Initializes hibernate configuration parameters
     *
     * @param hibernateProperties the properties to configure
     */
    private void configure(Properties hibernateProperties) {
        hibernateProperties.put("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + database);
        hibernateProperties.put("hibernate.connection.username", user);
        hibernateProperties.put("hibernate.connection.password", pass);
        hibernateProperties.put("hibernate.hbm2ddl.auto", "");

        // For persistent session storage
        hibernateProperties.put("hibernate.current_session_context_class", "managed");
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        hibernateProperties.put("hibernate.archive.autodetection", "true");
    }

    /**
     * Creates a session factory for the given list of entities
     *
     * @param entities the list of entities to create the factory for
     * @return the factory
     */
    @Override
    public SessionFactory getSessionFactory(List<Class<?>> entities) {
        Properties properties = new Properties();
        configure(properties);

        Configuration configuration = new Configuration();
        configuration.addProperties(properties);

        for (Class<?> type : entities) {
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
     *
     * @return Fetches the connection to this database for querying.
     */
    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private synchronized DataSource getDataSource() {
        if(dataSource != null) return dataSource;

        dataSource = new MysqlDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(pass);
        dataSource.setURL("jdbc:mysql://" + host + ":" + port + "/" + database);

        return dataSource;
    }

    @Override
    public void close() {
        //Nothing, because queries are executed immediately for MySQL
    }
}