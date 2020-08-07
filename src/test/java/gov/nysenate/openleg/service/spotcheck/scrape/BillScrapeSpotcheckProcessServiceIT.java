package gov.nysenate.openleg.service.spotcheck.scrape;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.dao.bill.scrape.SqlFsBillScrapeReferenceDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.billscrape.ScrapeQueuePriority;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class BillScrapeSpotcheckProcessServiceIT extends BaseTests {

    @Autowired
    gov.nysenate.openleg.service.spotcheck.scrape.BillScrapeSpotcheckProcessService procService;

    @Autowired
    SqlFsBillScrapeReferenceDao dao;

    @Test
    public void queueThenProcessTest() {
        BaseBillId billId = new BaseBillId("S5513", 2015);
        dao.addBillToScrapeQueue(billId, ScrapeQueuePriority.MANUAL_ENTRY.getPriority());
        assertEquals(1, procService.collate());
        assertEquals(1, procService.ingest());
    }
}


