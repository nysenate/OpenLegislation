package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.scraping.CalendarScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kyle on 11/3/14.
 */
@Category(SillyTest.class)
public class LRSCalendarScraperTest extends BaseTests {
    @Autowired
    CalendarScraper scraper;

    @Test
    public void test() throws Exception{
        scraper.scrape();
    }

}
