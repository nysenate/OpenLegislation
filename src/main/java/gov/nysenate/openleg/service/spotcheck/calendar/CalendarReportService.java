package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.dao.calendar.data.SqlCalendarDao;
import gov.nysenate.openleg.dao.spotcheck.CalendarAlertReportDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarReportService implements SpotCheckReportService<CalendarId> {

    @Autowired
    private CalendarAlertReportDao reportDao;

    @Autowired
    private SqlCalendarAlertDao referenceDao;

    @Autowired
    private SqlCalendarDao actualDao;

    @Autowired
    private CalendarCheckService checkService;

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_CALENDAR_ALERT;
    }

    @Override
    public SpotCheckReport<CalendarId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<Calendar> references = retrieveReferences(start, end);
        LocalDateTime referenceDateTime = getMostRecentReference(references);
        SpotCheckReportId reportId = new SpotCheckReportId(getSpotcheckRefType(),
                                                           referenceDateTime.truncatedTo(ChronoUnit.SECONDS),
                                                           LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        SpotCheckReport<CalendarId> report = new SpotCheckReport<>(reportId);
        report.addObservations(createObservations(references));
        return report;
    }

    @Override
    public void saveReport(SpotCheckReport<CalendarId> report) {
        reportDao.saveReport(report);
    }

    @Override
    public SpotCheckReport<CalendarId> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplies reportId cannot be null.");
        }
        try {
            return reportDao.getReport(reportId);
        } catch (EmptyResultDataAccessException e) {
            throw new SpotCheckReportNotFoundEx(reportId);
        }
    }

    @Override
    public List<SpotCheckReportId> getReportIds(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return reportDao.getReportIds(getSpotcheckRefType(), start, end, dateOrder, limOff);
    }

    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Cannot delete a null reportId.");
        }
        reportDao.deleteReport(reportId);
    }

    private List<Calendar> retrieveReferences(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<Calendar> references = referenceDao.getCalendarAlertsByDateRange(start, end);
        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx(
                    String.format("No calendar alerts references were found between %s and %s", start, end));
        }
        return references;
    }

    private List<SpotCheckObservation<CalendarId>> createObservations(List<Calendar> references) {
        List<SpotCheckObservation<CalendarId>> observations = new ArrayList<>();
        for (Calendar reference : references) {
            CalendarId id = reference.getId();
            try {
                Calendar actual = actualDao.getCalendar(id);
                observations.add(checkService.check(actual, reference));
            } catch (DataAccessException e) {
                SpotCheckReferenceId obsRefId = new SpotCheckReferenceId(
                        getSpotcheckRefType(), reference.getPublishedDateTime().truncatedTo(ChronoUnit.SECONDS));

                SpotCheckObservation<CalendarId> observation = new SpotCheckObservation<>(obsRefId, id);
                observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING,
                                                              id.toString(), ""));
                observations.add(observation);
            }
            referenceDao.markAsChecked(id);
        }
        return observations;
    }

    private LocalDateTime getMostRecentReference(List<Calendar> references) {
        LocalDateTime dateTime = LocalDateTime.from(DateUtils.LONG_AGO.atStartOfDay());
        for (Calendar cal : references) {
            if (cal.getPublishedDateTime().isAfter(dateTime)) {
                dateTime = cal.getPublishedDateTime();
            }
        }
        return dateTime;
    }
}
