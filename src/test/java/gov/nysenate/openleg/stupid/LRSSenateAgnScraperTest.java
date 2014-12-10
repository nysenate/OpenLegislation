package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.scraping.SenateAgnScraper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kyle on 11/12/14.
 */
public class LRSSenateAgnScraperTest extends BaseTests {
    @Autowired
    SenateAgnScraper scraper;

    @Test
    public void test() throws Exception{
        scraper.scrape();
    }
}
