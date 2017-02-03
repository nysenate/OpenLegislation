package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.model.spotcheck.OpenMismatchQuery;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static gov.nysenate.openleg.dao.spotcheck.SqlSpotCheckReportQuery.getOpenObsMismatchesSummaryQuery;
import static org.junit.Assert.*;

public class SqlSpotCheckReportQueryTest {

    private static final Logger logger = LoggerFactory.getLogger(SqlSpotCheckReportQueryTest.class);

    private static final String schema = "master";
    private static final SpotCheckRefType refType = SpotCheckRefType.SENATE_SITE_BILLS;

    @Test
    public void getOpenMismatchSummaryTest() {
        OpenMismatchQuery query = new OpenMismatchQuery(Collections.singleton(refType),
                null, DateUtils.LONG_AGO.atStartOfDay(),
                null, null, null, false,
                true, false, true, true);
        final String sqlQuery = getOpenObsMismatchesSummaryQuery(schema, query);
        logger.info(sqlQuery);
    }
}