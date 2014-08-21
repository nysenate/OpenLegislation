package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

public class DaybreakCheckReportService implements SpotCheckReportService<BillId>
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckReportService.class);

    @Resource(name = "daybreak")
    private SpotCheckService<BaseBillId, Bill, DaybreakBill> daybreakSpotCheckService;

    /** --- Implemented Methods --- */

    @Override
    public SpotCheckReport<BillId> generateReport(boolean priorContext) {
        return null;
    }

    @Override
    public SpotCheckReport<BillId> generateReport(boolean priorContext, LocalDateTime latestRefDateTime) {
        return null;
    }

    @Override
    public int saveReport(SpotCheckReport<BillId> report) {
        return 0;
    }

    @Override
    public SpotCheckReport<BillId> getReport(int reportId) {
        return null;
    }

    @Override
    public List<SpotCheckReport<BillId>> getReports(SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    @Override
    public List<SpotCheckReport<BillId>> getReports(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    @Override
    public void deleteReport(int reportId) {

    }
}
