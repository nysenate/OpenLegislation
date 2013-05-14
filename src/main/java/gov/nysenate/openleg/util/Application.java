package gov.nysenate.openleg.util;

import gov.nysenate.util.Config;
import gov.nysenate.util.DB;
import gov.nysenate.util.Mailer;
import gov.nysenate.util.listener.NYSenateConfigurationListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

public class Application
{
    public static Logger logger = Logger.getLogger(Application.class);

    /** Static factory instance */
    protected static Application appInstance = new Application();

    /** Default values */
    protected static final String prodPropertyFileName = "app.properties";
    protected static final String testPropertyFileName = "test.app.properties";

    /** Dependency instances */
    protected NYSenateConfigurationListener configurationListener;
    protected Config config;
    protected Mailer mailer;
    protected DB db;

    /**
     * Public access call to build()
     * @return boolean - If true then build succeeded
     */
    public static boolean bootstrap()
    {
        return bootstrap(prodPropertyFileName);
    }

    public static boolean bootstrap(String propertyFileName)
    {
        try
        {
            appInstance.config = new Config(propertyFileName);
            appInstance.db = new DB(appInstance.config, "mysqldb");
            appInstance.mailer = new Mailer(appInstance.config, "mailer");
            return true;
        }
        catch (ConfigurationException ce)
        {
            logger.fatal("Failed to load configuration file " + propertyFileName);
            logger.fatal(ce.getMessage());
        }
        catch (Exception ex)
        {
            logger.fatal("An exception occurred while building dependencies");
            logger.fatal(ex.getMessage());
        }
        return false;
    }

    public static boolean shutdown()
    {
        return true;
    }

    public static Config getConfig() {
        return appInstance.config;
    }

    public static DB getDB() {
        return appInstance.db;
    }
}
