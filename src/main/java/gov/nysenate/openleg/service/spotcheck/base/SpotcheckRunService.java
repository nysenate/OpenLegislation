package gov.nysenate.openleg.service.spotcheck.base;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceEvent;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.service.spotcheck.agenda.AgendaReportService;
import gov.nysenate.openleg.service.spotcheck.agenda.IntervalAgendaReportService;
import gov.nysenate.openleg.service.spotcheck.billtext.BillTextReportService;
import gov.nysenate.openleg.service.spotcheck.calendar.CalendarReportService;
import gov.nysenate.openleg.service.spotcheck.calendar.IntervalCalendarReportService;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakReportService;
import gov.nysenate.openleg.service.spotcheck.senatesite.agenda.SenSiteAgendaReportService;
import gov.nysenate.openleg.service.spotcheck.senatesite.bill.BillReportService;
import gov.nysenate.openleg.service.spotcheck.senatesite.calendar.SenateSiteCalendarReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckRefType.*;

/**
 * Runs spotcheck reports based on scheduling and events
 */
@Service
public class SpotcheckRunService {

    private static final Logger logger = LoggerFactory.getLogger(SpotcheckRunService.class);

    @Autowired private Environment env;

    @Autowired private EventBus eventBus;

    @Autowired private SpotCheckNotificationService spotCheckNotificationService;

    /** A multimap of reports that run whenever pertinent references are generated */
    SetMultimap<SpotCheckRefType, SpotCheckReportService> eventTriggeredReports;

    /** A set of reports are automatically ran based on the scheduler.spotcheck.interval.cron */
    Set<SpotCheckReportService> intervalReports;

    /** --- Report Services --- */

    /** Agenda Report Services */
    @Autowired private AgendaReportService agendaReportService;
    @Autowired private IntervalAgendaReportService intervalAgendaReportService;

    /** Bill Report Services */
    @Autowired private DaybreakReportService daybreakReportService;
    @Autowired private BillTextReportService billTextReportService;

    /** Calendar Report Services */
    @Autowired private CalendarReportService calendarReportService;
    @Autowired private IntervalCalendarReportService intervalCalendarReportService;

    /** Nysenate.gov Report Services */
    @Autowired private BillReportService senSiteBillReportService;
    @Autowired private SenateSiteCalendarReportService senSiteCalReportService;
    @Autowired private SenSiteAgendaReportService senSiteAgendaReportService;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        eventTriggeredReports = ImmutableSetMultimap.<SpotCheckRefType, SpotCheckReportService>builder()
                .put(LBDC_AGENDA_ALERT, agendaReportService)
                .put(LBDC_DAYBREAK, daybreakReportService)
                .put(LBDC_SCRAPED_BILL, billTextReportService)
                .put(LBDC_CALENDAR_ALERT, calendarReportService)
                .put(SENATE_SITE_BILLS, senSiteBillReportService)
                .put(SENATE_SITE_CALENDAR, senSiteCalReportService)
                .put(SENATE_SITE_AGENDA,senSiteAgendaReportService)
                .build();
        intervalReports = ImmutableSet.<SpotCheckReportService>builder()
                .add(intervalAgendaReportService)
                .add(intervalCalendarReportService)
                .build();
    }

    /**
     * Runs all interval reports according to {@code scheduler.spotcheck.interval.cron} in app.properties.
     * Only runs if spotcheck processing is enabled/scheduled.
     */
    @Scheduled(cron = "${scheduler.spotcheck.interval.cron:0 45 23 * * *}")
    public synchronized void runIntervalReports() {
        if (env.isSpotcheckScheduled()) {
            runIntervalReports(LocalDate.now().getYear());
        }
    }

    /**
     * Runs all interval reports, checking all data in the specified year.
     * @param year
     */
    public synchronized void runIntervalReports(int year) {
        Range<LocalDateTime> yearRange = Range.closed(LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 0, 0));
        intervalReports.forEach(reportService -> runReport(reportService, yearRange));
    }

    /**
     * Given a spotcheck reference event, runs all reports that use the event's spotcheck reference type
     * @param referenceEvent SpotCheckReferenceEvent
     */
    @Subscribe
    public synchronized void handleSpotcheckReferenceEvent(SpotCheckReferenceEvent referenceEvent) {
        runReports(referenceEvent.getRefType());
    }

    /**
     * Run all reports that use the give reference type for the given date time range
     *
     * @param refType SpotCheckRefType
     * @param reportRange Range<LocalDateTime>
     */
    public synchronized void runReports(SpotCheckRefType refType, Range<LocalDateTime> reportRange) {
        eventTriggeredReports.get(refType)
                .forEach(reportService -> runReport(reportService, reportRange));
    }

    /**
     * Run all reports that use the given reference type
     *
     * @param refType SpotCheckRefType
     */
    public synchronized void runReports(SpotCheckRefType refType) {
        runReports(refType, DateUtils.ALL_DATE_TIMES);
    }

    /** --- Internal Methods --- */

    private <T> void runReport(SpotCheckReportService<T> reportService, Range<LocalDateTime> reportRange) {
        logger.info("Attempting to run a {} report..", reportService.getSpotcheckRefType());
        try {
            SpotCheckReport<T> report = reportService.generateReport(
                    DateUtils.startOfDateTimeRange(reportRange), DateUtils.endOfDateTimeRange(reportRange));
            int notesCutoff = 140;
            logger.info("Saving {} report. obs: {} mm: {} notes: {}",
                    report.getReferenceType(), report.getObservedCount(), report.getOpenMismatchCount(true),
                    StringUtils.abbreviate(report.getNotes(), notesCutoff));
            reportService.saveReport(report);
            spotCheckNotificationService.spotcheckCompleteNotification(report);
        } catch (ReferenceDataNotFoundEx ex) {
            logger.info("No report generated: no {} references could be found. Message: " + ex.getMessage(), reportService.getSpotcheckRefType());
        } catch (Exception ex) {
            spotCheckNotificationService.handleSpotcheckException(ex, true);
        }
    }
}
