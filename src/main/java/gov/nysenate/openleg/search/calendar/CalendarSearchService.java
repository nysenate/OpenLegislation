package gov.nysenate.openleg.search.calendar;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.search.IndexedSearchService;
import gov.nysenate.openleg.updates.calendar.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.updates.calendar.CalendarUpdateEvent;

public interface CalendarSearchService extends IndexedSearchService<Calendar> {
    /**
     * Performs a generic search of all calendar types, returning a list of matching calendar ids.
     *
     * @param query
     * @param sort
     * @return SearchResults<CalendarId> A list of calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    default SearchResults<CalendarId> searchForCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException {
        return searchForCalendarsByYear(null, query, sort, limitOffset);
    }

    /**
     * Performs a search of all calendar types that were published on a given year, returning a list of matching calendar ids.
     *
     * @param year
     * @param query
     * @param sort
     * @return SearchResults<CalendarId> A list of calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    SearchResults<CalendarId> searchForCalendarsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Handles a calendar update event by indexing the updated calendar into the search index
     *
     * @param calendarUpdateEvent
     */
    void handleCalendarUpdateEvent(CalendarUpdateEvent calendarUpdateEvent);

    /**
     * Handles a bulk calendar update event by indexing the updated calendars into the search index
     * @param bulkCalendarUpdateEvent
     */
    void handleBulkCalendarUpdateEvent(BulkCalendarUpdateEvent bulkCalendarUpdateEvent);
}
