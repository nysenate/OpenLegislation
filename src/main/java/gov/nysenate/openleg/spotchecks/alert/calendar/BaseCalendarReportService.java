package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlCalendarAlertDao;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;
import gov.nysenate.openleg.spotchecks.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public abstract class BaseCalendarReportService implements SpotCheckReportService<CalendarEntryListId> {
    private final CalendarCheckService checkService;
    private final OpenLegEnvironment environment;
    protected final CalendarDataService calendarDataService;
    protected final SqlCalendarAlertDao alertDao;

    @Autowired
    public BaseCalendarReportService(CalendarCheckService checkService, OpenLegEnvironment environment,
                                     SqlCalendarAlertDao alertDao, CalendarDataService calendarDataService) {
        this.checkService = checkService;
        this.environment = environment;
        this.alertDao = alertDao;
        this.calendarDataService = calendarDataService;
    }

    protected abstract String getNotes();

    protected abstract void markAsChecked(CalendarId id);

    protected abstract List<Calendar> getReferences(LocalDateTime start, LocalDateTime end);

    /**
     * @return The actual calendar or null if it doesn't exist.
     */
    protected abstract Calendar getActualCalendar(CalendarId id, LocalDate calDate);

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_CALENDAR_ALERT;
    }

    @Override
    public SpotCheckReport<CalendarEntryListId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        List<Calendar> references = retrieveReferences(start, end).stream()
                .filter(this::outsideGracePeriod)
                .toList();
        SpotCheckReport<CalendarEntryListId> report = initSpotCheckReport(references);
        report.setNotes(getNotes());
        report.addObservations(createObservations(references));
        return report;
    }

    private List<Calendar> retrieveReferences(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<Calendar> references = getReferences(start, end);
        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx(
                    String.format("No calendar alerts references were found between %s and %s", start, end));
        }
        return references;
    }

    // Returns true if this references is outside the specified grace period.
    // This ensures openleg has time to process the data before we create a mismatch.
    private boolean outsideGracePeriod(Calendar cal) {
        return LocalDateTime.now().minus(environment.getSpotcheckAlertGracePeriod())
                .isAfter(cal.getPublishedDateTime());
    }

    private SpotCheckReport<CalendarEntryListId> initSpotCheckReport(List<Calendar> references) {
        LocalDateTime referenceDateTime = getMostRecentReference(references);
        SpotCheckReportId reportId = new SpotCheckReportId(getSpotcheckRefType(),
                referenceDateTime,
                LocalDateTime.now());
        return new SpotCheckReport<>(reportId);
    }

    private List<SpotCheckObservation<CalendarEntryListId>> createObservations(List<Calendar> references) {
        List<SpotCheckObservation<CalendarEntryListId>> observations = new ArrayList<>();
        for (Calendar reference : references) {
            CalendarId id = reference.getId();
            Calendar actual = getActualCalendar(id, reference.getCalDate());
            observations.addAll(checkService.checkAll(actual, reference));
            markAsChecked(id);
        }
        // Cancel the report if there are no observations
        if (observations.isEmpty()) {
            throw new SpotCheckAbortException();
        }
        return observations;
    }

    private static LocalDateTime getMostRecentReference(List<Calendar> references) {
        LocalDateTime dateTime = LocalDateTime.from(DateUtils.LONG_AGO);
        for (Calendar cal : references) {
            if (cal.getPublishedDateTime().isAfter(dateTime)) {
                dateTime = cal.getPublishedDateTime();
            }
        }
        return dateTime;
    }
}
