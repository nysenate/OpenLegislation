package gov.nysenate.openleg.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PropertyConfig.class, DatabaseConfig.class, ApplicationConfig.class})
@ComponentScan(
    value= "gov.nysenate.openleg",
    // Exclude the WebApplicationConfig since it can only be constructed within a ServletContext
    excludeFilters = {@ComponentScan.Filter(value = WebApplicationConfig.class, type = FilterType.ASSIGNABLE_TYPE)}
)
public class ConsoleApplicationConfig
{
    /** --- Any console specific bean definitions go here --- */
}
