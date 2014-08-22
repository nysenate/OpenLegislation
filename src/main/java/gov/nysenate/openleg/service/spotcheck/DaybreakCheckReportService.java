package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.bill.BillDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service("daybreakReport")
public class DaybreakCheckReportService implements SpotCheckReportService<BaseBillId>
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckReportService.class);

    @Resource(name = "daybreak")
    private SpotCheckService<BaseBillId, Bill, DaybreakBill> daybreakCheckService;

    @Autowired
    private DaybreakDao daybreakDao;

    @Autowired
    private SpotCheckReportDao<BaseBillId> reportDao;

    @Autowired
    private BillDataService billDataService;

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(boolean priorContext) {
        return generateReport(priorContext, LocalDateTime.now());
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(boolean priorContext, LocalDateTime latestRefDateTime) {
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.now()));
        // Fetch all the current daybreak bills
        List<DaybreakBill> daybreakBills = new ArrayList<>(); // TODO: Fetch from daybreakDao
        // Create a set of the base bill ids
        Set<BaseBillId> daybreakBillIds = daybreakBills.stream()
            .map(DaybreakBill::getBaseBillId).collect(Collectors.toSet());
        Set<BaseBillId> openlegBillIds =
            Sets.newHashSet(billDataService.getBillIds(SessionYear.current(), LimitOffset.ALL));
        // Check for base bill ids that the daybreak has but openleg does not.
        // These will be stored as 'missing source data' observations
//        Sets.difference(daybreakBillIds, openlegBillIds).stream()
//            .map(id -> new SpotCheckObservation<>())

        List<SpotCheckObservation<BaseBillId>> observations =
            daybreakBills.stream()
                .map(daybreakBill -> {
                    Bill bill = billDataService.getBill(daybreakBill.getBaseBillId());
                    return daybreakCheckService.check(bill, daybreakBill);
                })
                .collect(toList());

        report.setObservations(Maps.uniqueIndex(observations, o -> o.getKey()));
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public int saveReport(SpotCheckReport<BaseBillId> report) {
        if (report == null) {
            throw new IllegalArgumentException("Supplied report cannot be null.");
        }
        reportDao.saveReport(report);
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<BaseBillId> getReport(SpotCheckReportId reportId) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReport<BaseBillId>> getReports(SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReport<BaseBillId>> getReports(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteReport(int reportId) {

    }
}