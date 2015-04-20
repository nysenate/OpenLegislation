package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.dao.calendar.data.SqlCalendarDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarCheckReportService implements SpotCheckReportService<CalendarId>{

    @Autowired
    private SqlCalendarAlertDao referenceDao;

    @Autowired
    private SqlCalendarDao actualDao;

    @Autowired
    private CalendarSpotCheckService checkService;

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_FLOOR_CALENDAR;
    }

    @Override
    public SpotCheckReport<CalendarId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<Calendar> references = referenceDao.getCalendarAlertsByDateRange(start, end);
        LocalDateTime referenceDateTime = getMostRecentReference(references);

        SpotCheckReportId reportId = new SpotCheckReportId(getSpotcheckRefType(),
                                                           referenceDateTime.truncatedTo(ChronoUnit.SECONDS),
                                                           LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        SpotCheckReport report = new SpotCheckReport(reportId);
        report.addObservations(createObservations(references));

        return report;
    }

    private List<SpotCheckObservation<CalendarId>> createObservations(List<Calendar> references) {
        List<SpotCheckObservation<CalendarId>> observations = new ArrayList<>();
        for(Calendar reference: references) {
            CalendarId id = reference.getId();
            try {
                Calendar actual = actualDao.getCalendar(id);
                observations.add(checkService.check(actual, reference));
            }
            catch (DataAccessException e) {
                // actual data is missing this calendar
                SpotCheckReferenceId obsRefId = new SpotCheckReferenceId(
                        getSpotcheckRefType(), reference.getPublishedDateTime().truncatedTo(ChronoUnit.SECONDS));

                SpotCheckObservation<CalendarId> observation = new SpotCheckObservation<>(obsRefId, id);
                observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING,
                                                              id.toString(), ""));
                observations.add(observation);
            }
            // TODO: mark reference object as "checked" in db
        }
        return observations;
    }

    private LocalDateTime getMostRecentReference(List<Calendar> references) {
        LocalDateTime dateTime = LocalDateTime.from(DateUtils.LONG_AGO);
        for (Calendar cal: references) {
           if(cal.getPublishedDateTime().isAfter(dateTime)) {
               dateTime = cal.getPublishedDateTime();
           }
        }
        return dateTime;
    }

    @Override
    public void saveReport(SpotCheckReport<CalendarId> report) {

    }

    @Override
    public SpotCheckReport<CalendarId> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
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
