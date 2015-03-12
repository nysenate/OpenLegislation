package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.calendar.event.BulkCalendarUpdateEvent;
import gov.nysenate.openleg.service.calendar.event.CalendarUpdateEvent;

public interface CalendarSearchService extends IndexedSearchService<Calendar> {

    /**
     * Performs a generic search of all calendar types, returning a list of matching calendar ids.
     *
     * @param query
     * @param sort
     * @return SearchResults<CalendarId> A list of calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarId> searchForCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Performs a search of all calendar types that were published on a given year, returning a list of matching calendar ids.
     *
     * @param year
     * @param query
     * @param sort
     * @return SearchResults<CalendarId> A list of calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarId> searchForCalendarsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Handles a calendar update event by indexing the updated calendar into the search index
     *
     * @param calendarUpdateEvent
     */
    public void handleCalendarUpdateEvent(CalendarUpdateEvent calendarUpdateEvent);

    /**
     * Handles a bulk calendar update event by indexing the updated calendars into the search index
     * @param bulkCalendarUpdateEvent
     */
    public void handleBulkCalendarUpdateEvent(BulkCalendarUpdateEvent bulkCalendarUpdateEvent);
}
