package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
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

    @Ignore
    @Test
    public void save() {
        SpotCheckReport report = new SpotCheckReport();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        reportDao.saveReport(report);
    }

}
