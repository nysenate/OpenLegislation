package gov.nysenate.openleg.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {

    private static String config_path = "/app.properties";
    private static Properties config;
    private static Logger logger;

    public static Properties load() {
        // Load the logger first to make sure we can write errors
        if( logger == null ) {
            logger = Logger.getLogger(Config.class);
        }

        // Load the configuration if not already loaded
        // TODO: Store access time and enable auto-reloading
        if( config == null ) {
            config = new Properties();

            InputStream configFile = Config.class.getResourceAsStream(config_path);
            if( configFile == null ) {
                logger.error("Configuration file "+config_path+" not found on classpath.");
                return null;
            }

            try {
                config.load(configFile);
            } catch (IOException e) {
                logger.error("Error Parsing Configuration file", e);
                return null;
            }
        }

        return config;
    }

    public static String get(String key) {
        return Config.load().getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return Config.load().getProperty(key, defaultValue);
    }


}
