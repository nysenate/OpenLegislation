package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.dao.bill.search.BillSearchDao;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlBillSearchDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillSearchDaoTests.class);

    @Autowired
    private BillSearchDao billSearch;

    @Autowired
    private BillDao billDao;

    @Test
    public void testSearchAll() throws Exception {
    }

    @Test
    public void testAdvancedBillSearch() throws Exception {

//        SearchResults<BillId> results = billSearch.searchAdvanced(query, LimitOffset.TEN);
//        results.getResults().stream()
//            .map(r -> billDao.getBillInfo(r.getResult()))
//            .forEach(i -> logger.info("{} - {}", i.getBillId(), i.getTitle()));

    }
}
