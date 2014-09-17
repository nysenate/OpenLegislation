package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.search.BillSearchField;
import org.apache.abdera.model.DateTime;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SqlBillSearchDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillSearchDaoTests.class);

    @Autowired
    private BillSearchDao billSearch;

    @Autowired
    private BillDao billDao;

    @Test
    public void testSearchAll() throws Exception {
        billSearch.searchAll("moose", LimitOffset.TEN);
        StopWatch sw = new StopWatch();
        sw.start();
        billSearch.searchAll("prostitutes", LimitOffset.TEN);
        sw.stop();
        logger.info("{} ms", sw.getTime());
    }

    @Test
    public void testAdvancedBillSearch() throws Exception {
        Map<BillSearchField, String> query = new HashMap<>();
        query.put(BillSearchField.SPONSOR, "MARCHIONE");

        SearchResults<BillId> results = billSearch.searchAdvanced(query, LimitOffset.TEN);
        results.getResults().stream()
            .map(r -> billDao.getBillInfo(r.getResult()))
            .forEach(i -> logger.info("{} - {}", i.getBillId(), i.getTitle()));

    }
}
