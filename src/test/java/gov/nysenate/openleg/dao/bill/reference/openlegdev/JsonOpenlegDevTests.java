package gov.nysenate.openleg.dao.bill.reference.openlegdev;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.reference.daybreak.SqlFsDaybreakDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.openleg.OpenlegBillReportService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Created by Chenguang He on 2017/3/21.
 */
public class JsonOpenlegDevTests  extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegDevTests.class);

    @Autowired
    OpenlegBillReportService openlegBillReportService;

    @Test
    public void testgetBillView() throws Exception {
        SpotCheckReport<BaseBillId> spotCheckReport = openlegBillReportService.generateReport(LocalDateTime.parse("2017-12-03T10:15:30"),null);
        System.out.println();
    }
}
