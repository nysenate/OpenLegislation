package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.util.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class DaybreakSpotCheckReportServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotCheckReportServiceTests.class);

    @Autowired
    DaybreakCheckReportService daybreakReportService;

    @Test
    public void testGenerateReport() throws Exception {
        SpotCheckReport<BaseBillId> report =
            daybreakReportService.generateReport(DateUtils.longAgo(), LocalDateTime.now());
        daybreakReportService.saveReport(report);
    }
}
