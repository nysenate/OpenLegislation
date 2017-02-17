package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.*;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertTrue;

@Transactional
public class SpotCheckReportDaoTests extends BaseTests {

    @Autowired
    private BaseBillIdSpotCheckReportDao reportDao;

    @Test
    public void updatableMismatches() {
        System.out.println(reportDao.getUpdatableMismatches(SpotCheckDataSource.LBDC, SpotCheckContentType.BILL,
                SpotCheckRefType.LBDC_DAYBREAK.checkedMismatchTypes(), LocalDateTime.of(2016, 12, 31, 1, 1)).size());
    }

    @Test
    public void save() {
        LocalDateTime refDateTime = LocalDateTime.now();
        SpotCheckReport report = new SpotCheckReport();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime, LocalDateTime.now());
        report.setReportId(reportId);
        SpotCheckObservation ob = new SpotCheckObservation(new SpotCheckReferenceId(SpotCheckRefType.LBDC_DAYBREAK, refDateTime), new BillId("A1029", 2017));
        report.addObservation(ob);
        reportDao.saveReport(report);
    }

}
