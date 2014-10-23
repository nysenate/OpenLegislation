package gov.nysenate.openleg.util;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.lucene.Lucene;
import gov.nysenate.util.Config;
import gov.nysenate.util.DB;
import gov.nysenate.util.Mailer;
import gov.nysenate.util.listener.NYSenateConfigurationListener;

import java.io.File;
import java.io.IOException;

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
    protected Environment environment;
    protected Lucene lucene;
    protected Storage storage;
    protected DB db;

    /**
     * Public access call to build()
     * @return boolean - If true then build succeeded
     */
    public static boolean bootstrap()
    {
        return bootstrap(prodPropertyFileName, true);
    }

    public static boolean bootstrap(String propertyFileName, boolean luceneReadOnly)
    {
        try
        {
            appInstance.config = new Config(propertyFileName);
            appInstance.db = new DB(appInstance.config, "mysqldb");
            appInstance.mailer = new Mailer(appInstance.config, "mailer");
            appInstance.environment = new Environment(appInstance.config, "env");
            appInstance.lucene = new Lucene(new File(appInstance.config.getValue("lucene.directory")), luceneReadOnly);
            appInstance.storage = new Storage(appInstance.environment.getStorageDirectory());
            return true;
        }
        catch (ConfigurationException ce)
        {
            logger.fatal("Failed to load configuration file " + propertyFileName);
            logger.fatal(ce.getMessage(), ce);
        }
        catch (Exception ex)
        {
            logger.fatal("An exception occurred while building dependencies");
            logger.fatal(ex.getMessage(), ex);
        }
        return false;
    }

    public static boolean shutdown() throws IOException
    {
        if (appInstance.lucene != null) {
            appInstance.lucene.close();
        }
        return true;
    }

    public static Config getConfig() {
        return appInstance.config;
    }

    public static DB getDB() {
        return appInstance.db;
    }

    public static Lucene getLucene() {
        return appInstance.lucene;
    }

    public static Environment getEnvironment() {
        return appInstance.environment;
    }

    public static Storage getStorage() {
        return appInstance.storage;
    }
}
