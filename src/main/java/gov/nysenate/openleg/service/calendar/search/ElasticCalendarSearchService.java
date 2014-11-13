package gov.nysenate.openleg.service.calendar.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.calendar.search.CalendarSearchDao;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.calendar.event.CalendarUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ElasticCalendarSearchService implements CalendarSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchService.class);

    private static LimitOffset defaultLimitOffset = LimitOffset.HUNDRED;

    @Autowired
    CalendarSearchDao calendarSearchDao;

    @Autowired
    EventBus eventBus;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CalendarId> searchForCalendars(String query, String sort, LimitOffset limitOffset) throws SearchException {
        return searchCalendars(QueryBuilders.queryString(query), null, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CalendarId> searchForCalendarsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchCalendars(getCalendarYearQuery(year, query), null, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CalendarActiveListId> searchForActiveLists(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchActiveLists(QueryBuilders.queryString(query), null, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CalendarActiveListId> searchForActiveListsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchActiveLists(getCalendarYearQuery(year, query), null, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CalendarSupplementalId> searchForSupplementalCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchFloorCalendars(QueryBuilders.queryString(query), null, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResults<CalendarSupplementalId> searchForSupplementalCalendarsByYear(Integer year, String query, String sort,
                                                                                      LimitOffset limitOffset)
            throws SearchException {
        return searchFloorCalendars(getCalendarYearQuery(year, query), null, sort, limitOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public synchronized void handleCalendarUpdateEvent(CalendarUpdateEvent calendarUpdateEvent) {
        calendarSearchDao.updateCalendarIndex(calendarUpdateEvent.getCalendar());
    }

    /**
     * --- Helper Methods ---
     */

    /**
     * Returns a query that can be used for all calendar types that matches calendars for the given year
     *  in addition to the criteria specified by the given query string
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
            limitOffset = defaultLimitOffset;
        }
        try {
            return calendarSearchDao.searchCalendars(query, postFilter, sort, limitOffset);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (Exception ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }

    /**
     * Performs a search on the active list calendar index using the search dao, handling any exceptions that may arise
     *
     * @param query
     * @param postFilter
     * @param sort
     * @param limitOffset
     * @return
     * @throws SearchException
     */
    private SearchResults<CalendarActiveListId> searchActiveLists(QueryBuilder query, FilterBuilder postFilter,
                                                      String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = defaultLimitOffset;
        }
        try {
            return calendarSearchDao.searchActiveLists(query, postFilter, sort, limitOffset);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }

    /**
     * Performs a search on the floor calendar index using the search dao, handling any exceptions that may arise
     *
     * @param query
     * @param postFilter
     * @param sort
     * @param limitOffset
     * @return
     * @throws SearchException
     */
    private SearchResults<CalendarSupplementalId> searchFloorCalendars(QueryBuilder query, FilterBuilder postFilter,
                                                      String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = defaultLimitOffset;
        }
        try {
            return calendarSearchDao.searchCalendarSupplementals(query, postFilter, sort, limitOffset);
        }
        catch (SearchParseException ex) {
            throw new SearchException("There was a problem parsing the supplied query string.", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }
}
