package gov.nysenate.openleg.spotchecks.scrape.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeSpotcheckProcessService;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.ScrapeQueuePriority;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.SqlFsBillScrapeReferenceDao;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class BillScrapeSpotcheckProcessServiceIT extends BaseTests {
    @Autowired
    private BillScrapeSpotcheckProcessService procService;

    @Autowired
    private SqlFsBillScrapeReferenceDao dao;

    @Autowired
    private OpenLegEnvironment env;

    @Test
    public void queueThenProcessTest() {
        BaseBillId billId = new BaseBillId("S5513", 2015);
        dao.addBillToScrapeQueue(billId, ScrapeQueuePriority.MANUAL_ENTRY.getPriority());
        final int expected = env.isSpotcheckScheduled() ? 1 : 0;
        assertEquals(expected, procService.collate());
        assertEquals(expected, procService.ingest());
    }
}
