package gov.nysenate.openleg.service.calendar.search;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.search.ElasticCalendarSearchDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.search.*;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.event.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.service.calendar.event.CalendarUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;

@Service
public class ElasticCalendarSearchService implements CalendarSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchService.class);

    @Autowired private ElasticCalendarSearchDao calendarSearchDao;
    @Autowired private CalendarDataService calendarDataService;
    @Autowired private Environment env;
    @Autowired private EventBus eventBus;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CalendarId> searchForCalendars(String query, String sort, LimitOffset limitOffset) throws SearchException {
        return searchCalendars(QueryBuilders.queryString(smartSearch(query)), null, sort, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CalendarId> searchForCalendarsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchCalendars(getCalendarYearQuery(year, smartSearch(query)), null, sort, limitOffset);
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public synchronized void handleCalendarUpdateEvent(CalendarUpdateEvent calendarUpdateEvent) {

        updateIndex(calendarUpdateEvent.getCalendar());
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleBulkCalendarUpdateEvent(BulkCalendarUpdateEvent bulkCalendarUpdateEvent) {
        updateIndex(bulkCalendarUpdateEvent.getCalendars());
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
            for (int year = calendarYearRange.get().lowerEndpoint();
                 year <= calendarYearRange.get().upperEndpoint(); year++) {
                updateIndex(calendarDataService.getCalendars(year, SortOrder.NONE, LimitOffset.ALL));
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

    /** --- Helper Methods --- */

    /**
     * Returns a query that can be used for all calendar types that matches calendars for the given year
     * in addition to the criteria specified by the given query string
     *
     * @param year
     * @param query
     * @return
     */
    private QueryBuilder getCalendarYearQuery(Integer year, String query) {
        return QueryBuilders.filteredQuery(QueryBuilders.queryString(query), FilterBuilders.termFilter("year", year));
    }

    /**
     * Performs a search on the calendar index using the search dao, handling any exceptions that may arise
     *
     * @param query
     * @param postFilter
     * @param sort
     * @param limitOffset
     * @return
     * @throws SearchException
     */
    private SearchResults<CalendarId> searchCalendars(QueryBuilder query, FilterBuilder postFilter,
                                             String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        try {
            return calendarSearchDao.searchCalendars(query, postFilter,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
        } catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        } catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex);
        }
    }

    private String smartSearch(String query) {
        if (query != null && !query.contains(":")) {
            Matcher matcher = CalendarId.calendarIdPattern.matcher(query.replace("\\s+", ""));
            if (matcher.matches()) {
                query = String.format("year:%s AND calendarNumber:%s", matcher.group(1), matcher.group(2));
            }
        }
        return query;
    }
}
