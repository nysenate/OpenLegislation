package gov.nysenate.openleg.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Used to prevent Tomcat having to forcibly unregister the JDBC driver.
 * Code modified from <a href="https://stackoverflow.com/a/23912257">here</a>
 */
public class JdbcDriverDeregistrationListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(JdbcDriverDeregistrationListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ClassLoader cl = getClass().getClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() != cl) {
                continue;
            }
            // This driver was registered by the webapp's ClassLoader, so deregister it:
            try {
                logger.info("De-registering JDBC driver {}", driver);
                DriverManager.deregisterDriver(driver);
            }
            catch (SQLException ex) {
                logger.error("Error de-registering JDBC driver {}", driver, ex);
            }
        }
    }
}
