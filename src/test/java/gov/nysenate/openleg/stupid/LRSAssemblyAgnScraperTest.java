package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.scraping.AssemblyAgnScraper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by kyle on 11/10/14.
 */
@Category(SillyTest.class)
public class LRSAssemblyAgnScraperTest extends BaseTests {
    @Autowired
    AssemblyAgnScraper scraper;

    @Test
    public void test() throws Exception{
        scraper.scrape();
    }

}
