package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.scraping.CalendarScraper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kyle on 11/3/14.
 */
public class LRSCalendarScraperTest extends BaseTests {
    @Autowired
    CalendarScraper scraper;

    @Test
    public void test() throws Exception{
        scraper.scrape();
    }

}
