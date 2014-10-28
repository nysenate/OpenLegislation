package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;

public interface CalendarSearchService {

    /**
     * @param query
     * @param sort
     * @return SearchResults<CalendarId> A list of calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarId> searchForCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * @param query
     * @param sort
     * @return SearchResults<CalendarActiveListId> a list of active list calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarActiveListId> searchForActiveLists(String query, String sort, LimitOffset limitOffset)
            throws SearchException;

    /**
     * @param query
     * @param sort
     * @return SearchResults<CalendarSupplementalId> a list of supplemental calendar ids that match the given search parameters
     * @throws SearchException When there is a search related error
     */
    public SearchResults<CalendarSupplementalId> searchForFloorCalendars(String query, String sort, LimitOffset limitOffset)
            throws SearchException;
}
