package gov.nysenate.openleg.config;

import gov.nysenate.openleg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig
{
    @Value("${env.directory}") private String envDirectory;

    @Bean
    public Environment defaultEnvironment() {
        return new Environment(envDirectory);
    }
}
