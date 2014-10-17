package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.search.ElasticBillSearchDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ElasticBillSearchServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchServiceTests.class);

    @Autowired
    private ElasticBillSearchDao billSearch;

    @Test
    public void testSearch() throws Exception {
        logger.info("{}", billSearch.searchBills("Moose", "basePrintNo:DESC", LimitOffset.ALL).getResults());
    }
}
