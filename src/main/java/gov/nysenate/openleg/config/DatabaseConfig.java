package gov.nysenate.openleg.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@EnableTransactionManagement
@Configuration
public class DatabaseConfig
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    /** PostgreSQL Database Configuration */
    @Value("${postgresdb.driver}") private String dbDriver;
    @Value("${postgresdb.type}")  private String dbType;
    @Value("${postgresdb.host}")  private String dbHost;
    @Value("${postgresdb.name}")  private String dbName;
    @Value("${postgresdb.user}")  private String dbUser;
    @Value("${postgresdb.pass}")  private String dbPass;

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(postgresDataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate namedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(postgresDataSource());
    }

    /**
     * Configures the sql data source using a connection pool.
     * @return DataSource
     */
    @Bean
    public DataSource postgresDataSource() {
        final String jdbcUrlTemplate = "jdbc:%s//%s/%s";
        ComboPooledDataSource pool = new ComboPooledDataSource();
        try {
            pool.setDriverClass(dbDriver);
        }
        catch (PropertyVetoException ex) {
            logger.error("Error when setting the database driver " + dbDriver + "{}", ex.getMessage());
        }
        pool.setJdbcUrl(String.format(jdbcUrlTemplate, dbType, dbHost, dbName));
        logger.info("Connecting to Postgres: " + pool.getJdbcUrl());
        pool.setUser(dbUser);
        pool.setPassword(dbPass);
        pool.setMinPoolSize(3);
        pool.setMaxPoolSize(10);
        return pool;
    }

    /**
     * Configures a Spring transaction manager for the postgres data source.
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(postgresDataSource());
    }
}
