package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

@Transactional
public class SpotCheckReportDaoTests extends BaseTests {

    @Autowired
    private BaseBillIdSpotCheckReportDao reportDao;

    @Test
    public void testGetOpenMismatches() {
        reportDao.getOpenMismatches(SpotCheckDataSource.LBDC, LocalDateTime.now(), LimitOffset.TEN);
    }

    @Ignore
    @Test
    public void save() {
        SpotCheckReport report = new SpotCheckReport();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        reportDao.saveReport(report);
    }

}
