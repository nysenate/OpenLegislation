package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Category(SillyTest.class)
public class DaybreakSpotCheckReportServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotCheckReportServiceTest.class);

    @Autowired
    DaybreakReportService daybreakReportService;

    @Test
    public void testGenerateReport() throws Exception {
        SpotCheckReport<BillId> report =
            daybreakReportService.generateReport(DateUtils.LONG_AGO.atStartOfDay(), LocalDateTime.now());
        daybreakReportService.saveReport(report);
    }
}
