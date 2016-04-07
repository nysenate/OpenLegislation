package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import com.google.common.collect.*;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.calendar.data.CalendarUpdatesDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotcheckCalendarIdSpotCheckReportDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpRangeId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpSessionId;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by PKS on 2/25/16.
 */
@Service
public class CalendarReportServices extends BaseSpotCheckReportService<CalendarEntryListId> {

    private static final Logger logger = LoggerFactory.getLogger(CalendarReportServices.class);

    @Autowired private SenateSiteDao senateSiteDao;
    @Autowired private CalendarCheckServices calendarCheckServices;
    @Autowired private CalendarJsonParser calendarJsonParser;
    @Autowired private CalendarUpdatesDao calendarUpdatesDao;
    @Autowired private CalendarDataService calendarDataService;
    @Autowired private SpotcheckCalendarIdSpotCheckReportDao spotcheckCalendarIdSpotCheckReportDao;

    @Override
    protected SpotCheckReportDao<CalendarEntryListId> getReportDao() {
        return spotcheckCalendarIdSpotCheckReportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.SENATE_SITE_CALENDAR;
    }

    @Override
    public SpotCheckReport<CalendarEntryListId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        SenateSiteDump calendarDump = getMostRecentDump();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_CALENDAR,
                DateUtils.endOfDateTimeRange(calendarDump.getDumpId().getRange()), LocalDateTime.now());
        SpotCheckReport<CalendarEntryListId> report = new SpotCheckReport<>(reportId);
        report.setNotes(getDumpNotes(calendarDump));
        try {

            logger.info("getting calendar updates");

            // Get reference calendars using the calendar dump update interval
            Set<CalendarId> updatedCalendarIds = getCalendarUpdatesDuring(calendarDump);
            logger.info("got {} updated calendar ids", updatedCalendarIds.size());
            Map<CalendarId, Calendar> updatedCalendars = new LinkedHashMap<>();
            logger.info("retrieving calendars");
            for (CalendarId calendarId : updatedCalendarIds) {
                try {
                    updatedCalendars.put(calendarId, calendarDataService.getCalendar(calendarId));
                } catch (CalendarNotFoundEx ex) {
                    SpotCheckObservation<CalendarEntryListId> observation = new SpotCheckObservation<>(reportId.getReferenceId(),
                            new CalendarEntryListId(calendarId, CalendarType.ALL, Version.DEFAULT, 0));
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", calendarId));
                    report.addObservation(observation);
                }
            }
            logger.info("got {} calendars", updatedCalendars.size());
            logger.info("retrieving calendar dump");
            // Extract senate site calendars from the dump
            Multimap<CalendarEntryListId, SenateSiteCalendar> dumpedCalendars = ArrayListMultimap.create();
            calendarJsonParser.parseCalendars(calendarDump).forEach(b -> dumpedCalendars.put(b.getSpotCheckCalendarId(), b));
            logger.info("parsed {} dumped calendars", dumpedCalendars.size());

            prunePostDumpcalendars(calendarDump, report, dumpedCalendars, updatedCalendars);

            logger.info("comparing calendars present");
            // Add observations for any missing calendars that should have been in the dump
            report.addObservations(getRefDataMissingObs(dumpedCalendars.values(), updatedCalendars.values(),
                    reportId.getReferenceId()));

            logger.info("checking calendars");
            // Check each dumped senate site calendar
            dumpedCalendars.values().stream()
                    .filter(sencal -> updatedCalendars.containsValue(sencal.getCalendarId()))
                    .map(senSitecalendar -> calendarCheckServices.check(updatedCalendars.get(senSitecalendar.getCalendarId()), senSitecalendar))
                    .forEach(report::addObservation);

            logger.info("done: {} mismatches", report.getOpenMismatchCount(false));
        } finally {
            logger.info("archiving calendar dump...");
            senateSiteDao.setProcessed(calendarDump);
        }
        return report;
    }

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_CALENDAR).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site calendar dumps"));
    }

    private Set<CalendarId> getCalendarUpdatesDuring(SenateSiteDump calDump) {
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

    private List<SpotCheckObservation<CalendarEntryListId>> getRefDataMissingObs(Collection<SenateSiteCalendar> senSiteCalendars,
                                                                    Collection<Calendar> openlegCalendars,
                                                                    SpotCheckReferenceId refId) {
        Set<CalendarEntryListId> senSiteCalendarIds = senSiteCalendars.stream()
                .map(SenateSiteCalendar::getSpotCheckCalendarId)
                .collect(Collectors.toSet());

        Set<CalendarEntryListId> openlegCalendarIds = openlegCalendars.stream()
                .map(Calendar::getCalendarEntryListIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return Sets.difference(openlegCalendarIds, senSiteCalendarIds).stream()
                .map(calendarId -> {
                    SpotCheckObservation<CalendarEntryListId> observation =
                            new SpotCheckObservation<>(refId, calendarId);
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.REFERENCE_DATA_MISSING, "", ""));
                    return observation;
                })
                .collect(Collectors.toList());
    }

    /**
     * @param dump SenateSiteDump
     * @return String - notes that indicate the type of dump and the relevant dates
     */
    private String getDumpNotes(SenateSiteDump dump) {
        SenateSiteDumpId dumpId = dump.getDumpId();
        if (dumpId instanceof SenateSiteDumpRangeId) {
            return "Generated from update range dump: " + dumpId.getRange();
        } else if (dumpId instanceof SenateSiteDumpSessionId) {
            return "Generated from session year dump: " + ((SenateSiteDumpSessionId) dumpId).getSession();
        }
        return "Generated from unknown dump type: " + dumpId.getClass().getSimpleName() + " " + dumpId.getRange();
    }
}
