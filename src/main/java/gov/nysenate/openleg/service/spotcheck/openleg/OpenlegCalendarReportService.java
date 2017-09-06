package gov.nysenate.openleg.service.spotcheck.openleg;

import gov.nysenate.openleg.dao.calendar.reference.openleg.OpenlegCalenderDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("openlegCalendarReport")
public class OpenlegCalendarReportService extends BaseSpotCheckReportService<CalendarEntryListId> {

    @Value("api.secret")
    private  String apiSecret;

    @Autowired
    private SpotCheckReportDao<CalendarEntryListId> reportDao;

    @Autowired
    private OpenlegCalenderDao openlegCalendarDao;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    OpenlegBillCheckService checkService;

    @Override
    protected SpotCheckReportDao<CalendarEntryListId> getReportDao() {
        return reportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_CAL;
    }

    @Override
    public SpotCheckReport<CalendarEntryListId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        return new SpotCheckReport<>();
    }
}
