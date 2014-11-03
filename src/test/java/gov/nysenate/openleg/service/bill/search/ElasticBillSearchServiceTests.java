package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.search.ElasticBillSearchDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticBillSearchServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchServiceTests.class);

    @Autowired
    BillDataService billDataService;

    @Autowired
    private ElasticBillSearchDao billSearch;

    @Test
    public void testSearch() throws Exception {
    }

    @Test
    public void testBulkIndex() {
        LimitOffset limitOffset = new LimitOffset(500, 0);
        int billCount;
        do {
            logger.info("Retrieving bills...");
            List<Bill> bills = billDataService.getBillIds(SessionYear.current(), limitOffset).stream()
                    .map(billDataService::getBill)
                    .collect(Collectors.toList());
            billCount = bills.size();
            if (billCount > 0) {
                logger.info(String.format("Indexing bills %d - %d",
                        limitOffset.getOffsetStart(), limitOffset.getOffsetStart() + billCount - 1));
                billSearch.updateBillIndex(bills);
            }
            limitOffset = limitOffset.next();
        }
        while (billCount > 0);
        logger.info("done");
    }

    @Test
    public void testRebuildIndex() throws Exception {
//        billSearch.rebuildIndex();
    }
}
