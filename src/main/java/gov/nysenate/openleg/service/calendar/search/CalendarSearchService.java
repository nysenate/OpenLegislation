package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;

public interface CalendarSearchService {

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
     * Performs a search of active list calendars, returning a list of matching active list ids.
     *
     * @param query
     * @param sort
     * @return SearchResults<CalendarActiveListId> a list of active list calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarActiveListId> searchForActiveLists(String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Performs a search of active list calendars that were published on the given year, returning a list of matching active list ids.
     *
     * @param year
     * @param query
     * @param sort
     * @return SearchResults<CalendarActiveListId> a list of active list calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarActiveListId> searchForActiveListsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Performs a search on floor calendars, returning a list of matching floor calendar ids.
     *
     * @param query
     * @param sort
     * @return SearchResults<CalendarSupplementalId> a list of supplemental calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarSupplementalId> searchForFloorCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * Performs a search on floor calendars that match that were published on the given year, returning a list of matching floor calendar ids.
     *
     * @param year
     * @param query
     * @param sort
     * @return SearchResults<CalendarSupplementalId> a list of supplemental calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarSupplementalId> searchForFloorCalendarsByYear(Integer year, String query, String sort, LimitOffset limitOffset)
            throws SearchException;
}
