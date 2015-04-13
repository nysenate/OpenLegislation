package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kyle on 1/22/15.
 */
public class ActiveListCheckReportService implements SpotCheckReportService<CalendarActiveListId> {
    @Override
    public SpotCheckReport<CalendarActiveListId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        return null;
    }

    @Override
    public void saveReport(SpotCheckReport<CalendarActiveListId> report) {

    }

    @Override
    public SpotCheckReport<CalendarActiveListId> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
        return null;
    }

    @Override
    public List<SpotCheckReportId> getReportIds(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    @Override
    public void deleteReport(SpotCheckReportId reportId) {

    }
}
