package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface CalendarSearchDao {

    /**
     * Performs a calendar search based on the given query string.
     * Results are sorted and curtailed according to the given sort string and limit offset.
     *
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CalendarId> searchCalendars(String query, String sort, LimitOffset limitOffset);

    /**
     * Performs a calendar active list search based on the given query string.
     * Results are sorted and curtailed according to the given sort string and limit offset.
     *
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CalendarActiveListId> searchActiveLists(String query, String sort, LimitOffset limitOffset);

    /**
     * Performs a floor calendar search based on the given query string.
     * Results are sorted and curtailed according to the given sort string and limit offset.
     *
     * @param query
     * @param sort
     * @param limitOffset
     * @return
     */
    public SearchResults<CalendarSupplementalId> searchFloorCalendars(String query, String sort, LimitOffset limitOffset);
}
