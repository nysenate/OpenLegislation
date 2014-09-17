package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class DbBillSearchServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DbBillSearchServiceTests.class);

    @Autowired
    private DbBillSearchService billSearch;

    @Test
    public void testAdvancedSearch() throws Exception {
        Map<BillSearchField, String> criteria = new HashMap<>();
        criteria.put(BillSearchField.SPONSOR, "SAVINO ----- //()");
        logger.info("{}", billSearch.searchAdvanced(criteria, LimitOffset.FIFTY));
    }

    @Test
    public void testSanitizeQuery() throws Exception {
        logger.info("{}", billSearch.sanitizeQuery("12/12/2014"));
    }
}
