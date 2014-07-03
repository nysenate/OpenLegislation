package gov.nysenate.openleg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Property config that will execute only when the spring profile is in 'test' mode.
 * This allows for loading test.app.properties for unit tests.
 */
@Configuration
@Profile({"test"})
public class TestConfig
{
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);

    public static final String PROPERTY_FILENAME = "test.app.properties";

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        logger.info("Test property file loaded");
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[] { new ClassPathResource(PROPERTY_FILENAME) };
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        return pspc;
    }
}
