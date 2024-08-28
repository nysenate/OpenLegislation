package gov.nysenate.openleg.search.calendar;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.calendar.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.updates.calendar.CalendarUpdateEvent;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;

@Service
public class ElasticCalendarSearchService extends IndexedSearchService<Calendar> implements CalendarSearchService {
    private final SearchDao<CalendarId, CalendarView, Calendar> calendarSearchDao;
    private final CalendarDataService calendarDataService;

    public ElasticCalendarSearchService(SearchDao<CalendarId, CalendarView, Calendar> calendarSearchDao,
                                        CalendarDataService calendarDataService, EventBus eventBus) {
        super(calendarSearchDao);
        this.calendarSearchDao = calendarSearchDao;
        this.calendarDataService = calendarDataService;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CalendarId> searchForCalendarsByYear(Integer year, String queryStr, String sort, LimitOffset limitOffset)
            throws SearchException {
        return calendarSearchDao.searchForIds(
                smartSearch(queryStr), sort, limitOffset, getYearQuery("year", year)
        );
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleCalendarUpdateEvent(CalendarUpdateEvent calendarUpdateEvent) {
        updateIndex(calendarUpdateEvent.calendar());
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleBulkCalendarUpdateEvent(BulkCalendarUpdateEvent bulkCalendarUpdateEvent) {
        updateIndex(bulkCalendarUpdateEvent.calendars());
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        Range<Integer> calendarYearRange = calendarDataService.getCalendarYearRange()
                .canonical(DiscreteDomain.integers());
        for (int year = calendarYearRange.lowerEndpoint(); year < calendarYearRange.upperEndpoint(); year++) {
            updateIndex(calendarDataService.getCalendars(year, SortOrder.NONE, LimitOffset.ALL));
        }
    }

    private static String smartSearch(String queryStr) {
        if (queryStr != null && !queryStr.contains(":")) {
            Matcher matcher = CalendarId.calendarIdPattern.matcher(queryStr.replace("\\s+", ""));
            if (matcher.matches()) {
                queryStr = String.format("year:%s AND calendarNumber:%s", matcher.group(1), matcher.group(2));
            }
        }
        return queryStr;
    }
}
