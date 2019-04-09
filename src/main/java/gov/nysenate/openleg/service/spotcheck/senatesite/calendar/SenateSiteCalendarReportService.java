package gov.nysenate.openleg.service.spotcheck.senatesite.calendar;

import gov.nysenate.openleg.dao.spotcheck.CalendarEntryListIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.calendar.SenateSiteCalendar;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.spotcheck.senatesite.base.BaseSenateSiteReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.base.LimitOffset.ALL;
import static gov.nysenate.openleg.dao.base.SortOrder.ASC;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckRefType.SENATE_SITE_CALENDAR;

/**
 * Generates calendar spotcheck reports based on data from NYSenate.gov
 */
@Service
public class SenateSiteCalendarReportService extends BaseSenateSiteReportService<CalendarEntryListId> {

    @Autowired private SenateSiteCalendarCheckService senateSiteCalendarCheckService;
    @Autowired private CalendarJsonParser calendarJsonParser;
    @Autowired private CalendarDataService calendarDataService;
    @Autowired private CalendarEntryListIdSpotCheckReportDao calendarEntryListIdSpotCheckReportDao;

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SENATE_SITE_CALENDAR;
    }

    @Override
    protected void checkDump(SenateSiteDump dump, SpotCheckReport<CalendarEntryListId> report) {
        //Get openleg calendars for session
        List<Calendar> openlegCalendars = calendarDataService.getCalendars(dump.getDumpId().getYear(), ASC, ALL);

        // Parse senate site calendars from dump
        List<SenateSiteCalendar> senSiteCalendars = calendarJsonParser.parseCalendars(dump);

        checkCalendars(report, openlegCalendars, senSiteCalendars);
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
                report.addObservation(senateSiteCalendarCheckService.check(olCalendar, senateSiteCalendar));
            }
        }

        // Add observed data missing mismatches for all remaining senate site calendars
        senSiteCalMap.keySet().forEach(report::addObservedDataMissingObs);
    }
}
