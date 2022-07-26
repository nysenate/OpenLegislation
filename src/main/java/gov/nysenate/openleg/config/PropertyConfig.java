package gov.nysenate.openleg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configures access to the app.properties config file through @Value annotations.
 */
@Configuration
@PropertySource("classpath:/app.properties")
@Profile({"dev", "prod"})
public class PropertyConfig {
    /**
     * This instance is necessary for Spring to load up the property file and allow access to
     * it through the @Value(${propertyName}) annotation. Also note that this bean must be static
     * in order to work properly with current Spring behavior.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        var pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setIgnoreUnresolvablePlaceholders(true);
        return pspc;
    }
}
