package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.spotcheck.CalendarAlertReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public abstract class BaseCalendarReportService extends BaseSpotCheckReportService<CalendarId> {

    private static final Logger logger = LoggerFactory.getLogger(BaseCalendarReportService.class);

    @Autowired
    private CalendarAlertReportDao reportDao;

    @Autowired
    private CalendarCheckService checkService;

    @Autowired
    private Environment environment;

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
    protected SpotCheckReportDao<CalendarId> getReportDao() {
        return reportDao;
    }

    @Override
    public SpotCheckReport<CalendarId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx, Exception {
        List<Calendar> references = retrieveReferences(start, end);
        LocalDateTime referenceDateTime = getMostRecentReference(references);
        SpotCheckReportId reportId = new SpotCheckReportId(getSpotcheckRefType(),
                                                           referenceDateTime,
                                                           LocalDateTime.now());
        SpotCheckReport<CalendarId> report = new SpotCheckReport<>(reportId);
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

    private List<SpotCheckObservation<CalendarId>> createObservations(List<Calendar> references) {
        List<SpotCheckObservation<CalendarId>> observations = new ArrayList<>();
        for (Calendar reference : references) {
            CalendarId id = reference.getId();
            Calendar actual = getActualCalendar(id, reference.getCalDate());
            if (actual == null) {
                if (LocalDateTime.now()
                        .minus(environment.getSpotcheckAlertGracePeriod())
                        .isBefore(reference.getPublishedDateTime())) {
                    continue; // Do not add a not found mismatch if reference publish date is within grace period
                }
                recordMismatch(observations, reference, id);
            } else {
                observations.add(checkService.check(actual, reference));
            }
            markAsChecked(id);
        }
        // Cancel the report if there are no observations
        if (observations.isEmpty()) {
            throw new SpotCheckAbortException();
        }
        return observations;
    }

    private void recordMismatch(List<SpotCheckObservation<CalendarId>> observations, Calendar reference, CalendarId id) {
        SpotCheckReferenceId obsRefId = new SpotCheckReferenceId(
                getSpotcheckRefType(), reference.getPublishedDateTime());

        SpotCheckObservation<CalendarId> observation = new SpotCheckObservation<>(obsRefId, id);
        observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING,
                "", id.toString()));
        observations.add(observation);
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
