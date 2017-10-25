package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.service.scraping.ActiveListScrapingService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kyle on 12/8/14.
 */
@Category(SillyTest.class)
public class ActiveListScrapingServiceTest extends BaseTests {


    @Autowired
    ActiveListScrapingService actServ;

    @Test
    public void test() throws Exception{
        actServ.main();
    }

}
