package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.openleg.OpenlegBillReportService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Created by readman on 6/26/2017.
 */
public class OpenlegBillReportServiceTest extends BaseTests{
    private static final Logger logger = LoggerFactory.getLogger(OpenlegBillReportServiceTest.class);

    @Autowired
    OpenlegBillReportService spotcheckRunService;

    @Test
    public void runReports()
    {
        try {
           SpotCheckReport<BaseBillId> spotCheckReport = spotcheckRunService.generateReport(LocalDateTime.of(2017,1,1,0,1,1,1),null);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
