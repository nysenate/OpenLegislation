package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import com.google.common.collect.BoundType;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.calendar.data.CalendarUpdatesDao;
import gov.nysenate.openleg.dao.spotcheck.CalendarEntryListIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpSessionId;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.elasticsearch.common.collect.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.REFERENCE_DATA_MISSING;
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


    // VVV Here be unused code VVV

    private Set<CalendarId> getCalendarUpdatesDuring(SenateSiteDump calDump) {
        if(calDump.getDumpId() instanceof SenateSiteDumpSessionId){
            SenateSiteDumpSessionId calDumpId = (SenateSiteDumpSessionId) calDump.getDumpId();
            Set<CalendarId> calendarIds = calendarDataService.getCalendars(calDumpId.getSession().getSessionStartYear(),
                                                    SortOrder.NONE,LimitOffset.ALL).stream()
                                            .map(Calendar::getId)
                                            .collect(Collectors.toCollection(TreeSet::new));
            Set<CalendarId> calendarIds1 = calendarDataService.getCalendars(calDumpId.getSession().getSessionEndYear(),
                                                    SortOrder.NONE,LimitOffset.ALL).stream()
                                            .map(Calendar::getId)
                                            .collect(Collectors.toCollection(TreeSet::new));
            calendarIds.addAll(calendarIds1);
            return calendarIds;
        }
        Range<LocalDateTime> dumpUpdateInterval = calDump.getDumpId().getRange();
        return calendarUpdatesDao.getUpdates(UpdateType.PROCESSED_DATE,
                Range.greaterThan(DateUtils.startOfDateTimeRange(calDump.getDumpId().getRange())),
                 SortOrder.ASC, LimitOffset.ALL)
                .getResults().stream()
                .filter(token -> dumpUpdateInterval.contains(token.getProcessedDateTime()))
                .map(UpdateToken::getId)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private void prunePostDumpcalendars(SenateSiteDump calendarDump, SpotCheckReport report,
                                        Multimap<CalendarEntryListId, SenateSiteCalendar> senSiteCalendars, Map<CalendarId, Calendar> openlegCalendars) {
        Range<LocalDateTime> calendarDumpRange = calendarDump.getDumpId().getRange();
        Range<LocalDateTime> postDumpRange =  Range.downTo(DateUtils.endOfDateTimeRange(calendarDumpRange),
                calendarDumpRange.upperBoundType() == BoundType.OPEN ? BoundType.CLOSED : BoundType.OPEN);
        PaginatedList<UpdateToken<CalendarId>> postDumpUpdates =
                calendarUpdatesDao.getUpdates(UpdateType.PROCESSED_DATE, postDumpRange, SortOrder.NONE, LimitOffset.ALL);
        Set<CalendarId> postDumpUpdatedCalendars = postDumpUpdates.stream()
                .map(UpdateToken::getId)
                .collect(Collectors.toSet());

        if (!postDumpUpdatedCalendars.isEmpty()) {
            // Iterate over calendars updated after the update interval, removing them from the references and
            //  collecting them in a list to add to the report notes
            String notes = postDumpUpdatedCalendars.stream()
                    .peek(senSiteCalendars::removeAll)
                    .peek(openlegCalendars::remove)
                    .reduce("Ignored Calendars:", (str, calendarId) -> str + " " + calendarId, (a, b) -> a + " " + b);
            report.setNotes(notes);
        }
    }

    private Tuple<List<SpotCheckObservation<CalendarEntryListId>>, List<CalendarEntryListId>> getRefDataMissingObs(Collection<SenateSiteCalendar> senSiteCalendars,
                                                                                       Collection<Calendar> openlegCalendars,
                                                                                       SpotCheckReferenceId refId) {
        Set<CalendarEntryListId> senSiteCalendarIds = senSiteCalendars.stream()
                .map(SenateSiteCalendar::getCalendarEntryListId)
                .collect(Collectors.toSet());

        Set<CalendarEntryListId> openlegCalendarIds = openlegCalendars.stream()
                .map(Calendar::getCalendarEntryListIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<CalendarEntryListId> toRemove = new ArrayList<>();

        List<SpotCheckObservation<CalendarEntryListId>> refData = Sets.difference(openlegCalendarIds, senSiteCalendarIds).stream()
                .map(calendarId -> {
                    SpotCheckObservation<CalendarEntryListId> observation =
                            new SpotCheckObservation<>(refId, calendarId);
                    observation.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, "", ""));
                    return observation;
                }).collect(Collectors.toList());

        List<SpotCheckObservation<CalendarEntryListId>> obsData = Sets.difference(senSiteCalendarIds,openlegCalendarIds).stream()
                .map(calendarId -> {
                    SpotCheckObservation<CalendarEntryListId> observation =
                            new SpotCheckObservation<>(refId, calendarId);
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", ""));
                    toRemove.add(calendarId);
                    return observation;
                }).collect(Collectors.toList());

        List<SpotCheckObservation<CalendarEntryListId>> allDataObs = new ArrayList<>();
        allDataObs.addAll(refData);
        allDataObs.addAll(obsData);
        return Tuple.tuple(allDataObs,toRemove);
    }

    @Override
    protected SpotCheckReportDao<CalendarEntryListId> getReportDao() {
        return calendarEntryListIdSpotCheckReportDao;
    }
}
