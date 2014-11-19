package gov.nysenate.openleg.config;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

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
