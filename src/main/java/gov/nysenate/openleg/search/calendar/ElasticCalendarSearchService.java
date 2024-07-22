package gov.nysenate.openleg.search.calendar;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.calendar.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.updates.calendar.CalendarUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

@Service
public class ElasticCalendarSearchService implements CalendarSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchService.class);

    private final ElasticCalendarSearchDao calendarSearchDao;
    private final CalendarDataService calendarDataService;
    private final OpenLegEnvironment env;

    public ElasticCalendarSearchService(ElasticCalendarSearchDao calendarSearchDao,
                                        CalendarDataService calendarDataService,
                                        OpenLegEnvironment env, EventBus eventBus) {
        this.calendarSearchDao = calendarSearchDao;
        this.calendarDataService = calendarDataService;
        this.env = env;
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CalendarId> searchForCalendarsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchCalendars(
                IndexedSearchService.getBasicBoolQuery("year", year, smartSearch(query)),
                sort, limitOffset);
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
    public void updateIndex(Calendar content) {
        if (env.isElasticIndexing()) {
            logger.info("Indexing calendar {} into elastic search", content.getId());
            calendarSearchDao.updateCalendarIndex(content);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Calendar> content) {
        if (env.isElasticIndexing()) {
            logger.info("Indexing {} calendars into elastic search", content.size());
            calendarSearchDao.updateCalendarIndexBulk(content);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        calendarSearchDao.purgeIndices();
        calendarSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        Optional<Range<Integer>> calendarYearRange =  calendarDataService.getCalendarYearRange();
        if (calendarYearRange.isPresent()) {
            int calYear = calendarYearRange.get().lowerEndpoint();
            logger.info("Starting rebuild with session " + calYear);
            while (calYear <= LocalDate.now().getYear()) {
                LimitOffset limOff = new LimitOffset(5);
                List<Calendar> calendars = calendarDataService.getCalendars(calYear, SortOrder.NONE, limOff);
                while (!calendars.isEmpty()) {
                    updateIndex(calendars);
                    limOff = limOff.next();
                    calendars = calendarDataService.getCalendars(calYear, SortOrder.NONE, limOff);
                }
                calYear++;
                logger.info("The session year is now {}", calYear);
            }
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.CALENDAR)) {
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.CALENDAR)) {
            clearIndex();
        }
    }

    /**
     * Performs a search on the calendar index using the search dao, handling any exceptions that may arise
     *
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     * @throws SearchException
     */
    private SearchResults<CalendarId> searchCalendars(
            Query query, String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        return calendarSearchDao.searchCalendars(query, null,
                ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
    }

    private static String smartSearch(String query) {
        if (query != null && !query.contains(":")) {
            Matcher matcher = CalendarId.calendarIdPattern.matcher(query.replace("\\s+", ""));
            if (matcher.matches()) {
                query = String.format("year:%s AND calendarNumber:%s", matcher.group(1), matcher.group(2));
            }
        }
        return query;
    }
}
