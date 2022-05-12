package gov.nysenate.openleg.spotchecks.base;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.spotchecks.SpotCheckReportDao;
import gov.nysenate.openleg.spotchecks.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * Runs spotcheck reports based on scheduling and events
 */
@Service
public class SpotcheckRunService {

    private static final Logger logger = LoggerFactory.getLogger(SpotcheckRunService.class);

    private final EventBus eventBus;
    private final OpenLegEnvironment env;
    private final SpotCheckNotificationService spotCheckNotificationService;
    private final SpotCheckReportDao reportDao;

    /**
     * A multimap of reports that run whenever pertinent references are generated
     */
    private final ImmutableSetMultimap<SpotCheckRefType, SpotCheckReportService> eventTriggeredReports;

    /**
     * A set of reports are automatically ran based on the scheduler.spotcheck.interval.cron
     */
    private final ImmutableSet<SpotCheckReportService> intervalReports;

    public SpotcheckRunService(OpenLegEnvironment env,
                               EventBus eventBus,
                               SpotCheckNotificationService spotCheckNotificationService,
                               SpotCheckReportDao reportDao,
                               List<SpotCheckReportService> reportServices) {
        this.env = env;
        this.spotCheckNotificationService = spotCheckNotificationService;
        this.reportDao = reportDao;
        this.eventBus = eventBus;

        ImmutableSetMultimap.Builder<SpotCheckRefType, SpotCheckReportService> eventReportsBuilder =
                ImmutableSetMultimap.builder();
        ImmutableSet.Builder<SpotCheckReportService> intervalReportsBuilder = ImmutableSet.builder();
        // Sort report services into periodic and event driven collections
        for (SpotCheckReportService reportService : reportServices) {
            switch (reportService.getRunMode()) {
                case EVENT_DRIVEN -> eventReportsBuilder.put(reportService.getSpotcheckRefType(), reportService);
                case PERIODIC -> intervalReportsBuilder.add(reportService);
                default -> throw new IllegalArgumentException("Unknown run mode: " + reportService.getRunMode());
            }
        }

        this.eventTriggeredReports = eventReportsBuilder.build();
        this.intervalReports = intervalReportsBuilder.build();

        eventBus.register(this);
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
     *
     * @param year year to run reports for
     * @param refType run interval reports only of this type if not null
     */
    public synchronized void runIntervalReports(int year, SpotCheckRefType refType) {
        LocalDateTime startOfYear = Year.of(year).atDay(1).atStartOfDay();
        Range<LocalDateTime> yearRange = Range.closedOpen(startOfYear, startOfYear.plusYears(1));
        intervalReports.stream()
                .filter(rs -> refType == null || rs.getSpotcheckRefType() == refType)
                .forEach(reportService -> runReport(reportService, yearRange));
    }

    /**
     * Overload of {@link #runIntervalReports(int, SpotCheckRefType)} that doesn't filter by ref type.
     *
     * @param year year
     */
    public synchronized void runIntervalReports(int year) {
        runIntervalReports(year, null);
    }

    /**
     * Given a spotcheck reference event, runs all reports that use the event's spotcheck reference type
     *
     * @param referenceEvent SpotCheckReferenceEvent
     */
    @Subscribe
    public synchronized void handleSpotcheckReferenceEvent(SpotCheckReferenceEvent referenceEvent) {
        runReports(referenceEvent.getRefType());
    }

    /**
     * Run all reports that use the give reference type for the given date time range
     *
     * @param refType     SpotCheckRefType
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

    /* --- Internal Methods --- */

    private void runReport(SpotCheckReportService<?> reportService, Range<LocalDateTime> reportRange) {
        logger.info("Attempting to run a {} report...", reportService.getSpotcheckRefType());
        try {
            SpotCheckReport<?> report = reportService.generateReport(
                    DateUtils.startOfDateTimeRange(reportRange), DateUtils.endOfDateTimeRange(reportRange));
            int notesCutoff = 140;
            logger.info("Saving {} report. obs: {} mm: {}({}ig.) notes: {}",
                    report.getReferenceType(), report.getObservedCount(),
                    report.getOpenMismatchCount(false), report.getOpenMismatchCount(true),
                    StringUtils.abbreviate(report.getNotes(), notesCutoff));
            sendMismatchEvents(report);
            reportDao.saveReport(report);
            spotCheckNotificationService.spotcheckCompleteNotification(report);
            logger.info("Done saving spotcheck report.");
        } catch (ReferenceDataNotFoundEx ex) {
            logger.info("No report generated: no {} references could be found. Message: " + ex.getMessage(), reportService.getSpotcheckRefType());
        } catch (Exception ex) {
            spotCheckNotificationService.handleSpotcheckException(ex, true);
        }
    }

    /**
     * Generate and post {@link SpotcheckMismatchEvent} for all generated mismatches
     *
     * @param report {@link SpotCheckReport}
     */
    private void sendMismatchEvents(SpotCheckReport<?> report) {
        for (SpotCheckObservation<?> observation : report.getObservationMap().values()) {
            for (SpotCheckMismatch mismatch : observation.getMismatches().values()) {
                eventBus.post(new SpotcheckMismatchEvent<>(observation.getKey(), mismatch));
            }
        }
    }
}
