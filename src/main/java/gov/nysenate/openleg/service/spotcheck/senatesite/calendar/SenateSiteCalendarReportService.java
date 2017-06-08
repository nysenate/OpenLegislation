package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.calendar.data.CalendarUpdatesDao;
import gov.nysenate.openleg.dao.spotcheck.CalendarEntryListIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpSessionId;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckRefType.SENATE_SITE_CALENDAR;

/**
 * Generates calendar spotcheck reports based on data from NYSenate.gov
 */
@Service
public class SenateSiteCalendarReportService extends BaseSpotCheckReportService<CalendarEntryListId> {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteCalendarReportService.class);

    @Autowired private SenateSiteDao senateSiteDao;
    @Autowired private CalendarCheckServices calendarCheckServices;
    @Autowired private CalendarJsonParser calendarJsonParser;
    @Autowired private CalendarUpdatesDao calendarUpdatesDao;
    @Autowired private CalendarDataService calendarDataService;
    @Autowired private CalendarEntryListIdSpotCheckReportDao calendarEntryListIdSpotCheckReportDao;

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SENATE_SITE_CALENDAR;
    }

    @Override
    public SpotCheckReport<CalendarEntryListId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        SenateSiteDump calendarDump = getMostRecentDump();
        SenateSiteDumpSessionId dumpId = (SenateSiteDumpSessionId) calendarDump.getDumpId();
        SpotCheckReportId reportId = new SpotCheckReportId(SENATE_SITE_CALENDAR, dumpId.getDumpTime(), LocalDateTime.now());
        SpotCheckReport<CalendarEntryListId> report = new SpotCheckReport<>(reportId);
        report.setNotes(dumpId.getNotes());
        try {

            //Get openleg calendars for session
            List<Calendar> openlegCalendars = dumpId.getSession().asYearList().stream()
                    .map(year -> calendarDataService.getCalendars(year, SortOrder.ASC, LimitOffset.ALL))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            // Parse senate site calendars from dump
            List<SenateSiteCalendar> senSiteCalendars = calendarJsonParser.parseCalendars(calendarDump);

            checkCalendars(report, openlegCalendars, senSiteCalendars);

            logger.info("done: {} mismatches", report.getOpenMismatchCount(false));
        } finally {
            logger.info("archiving calendar dump...");
            senateSiteDao.setProcessed(calendarDump);
        }
        return report;
    }

    @Override
    protected SpotCheckReportDao<CalendarEntryListId> getReportDao() {
        return calendarEntryListIdSpotCheckReportDao;
    }

    /* --- Internal Methods --- */

    /**
     * Perform checks between openleg calendars and senate site calendars,
     * adding the results to the given report
     *
     * @param report {@link SpotCheckReport<CalendarEntryListId>}
     * @param openlegCalendars {@link List<Calendar>}
     * @param senSiteCalendars {@link List<SenateSiteCalendar>}
     */
    private void checkCalendars(SpotCheckReport<CalendarEntryListId> report,
                                List<Calendar> openlegCalendars,
                                List<SenateSiteCalendar> senSiteCalendars) {
        // Generate map of entryListId -> SenateSiteCalendar
        // Calendars are removed from this map as checked,
        // and all remaining calendars will be considered as observed data missing mismatches
        Map<CalendarEntryListId, SenateSiteCalendar> senSiteCalMap = senSiteCalendars.stream()
                .collect(Collectors.toMap(SenateSiteCalendar::getCalendarEntryListId, Function.identity()));

        for (Calendar olCalendar : openlegCalendars) {
            for (CalendarEntryListId entryListId : olCalendar.getCalendarEntryListIds()) {
                if (!senSiteCalMap.containsKey(entryListId)) {
                    report.addRefMissingObs(entryListId);
                    continue;
                }
                // Remove calendar from senate site calendar map and check
                SenateSiteCalendar senateSiteCalendar = senSiteCalMap.remove(entryListId);
                report.addObservation(calendarCheckServices.check(olCalendar, senateSiteCalendar));
            }
        }

        // Add observed data missing mismatches for all remaining senate site calendars
        senSiteCalMap.keySet().forEach(report::addObservedDataMissingObs);
    }

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SENATE_SITE_CALENDAR).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site calendar dumps"));
    }
}
