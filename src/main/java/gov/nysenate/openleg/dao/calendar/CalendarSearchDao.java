package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface CalendarSearchDao {

    /**
     * Gets the number of results for the specified query for use in pagination
     * @param calendarSearchParameters
     * @return
     */
    public int getCalendarCountforQuery(CalendarSearchParameters calendarSearchParameters) throws DataAccessException;

    /**
     * Returns a list of calendars that conform to the given search query
     *
     * @param calendarSearchParameters
     * @param sortOrder
     * @param limitOffset
     * @return
     * @throws DataAccessException
     */
    public List<CalendarId> getCalendars(CalendarSearchParameters calendarSearchParameters,
                                         SortOrder sortOrder, LimitOffset limitOffset)
                                                       throws DataAccessException;

    /**
     * Returns a list of floor calendars that conform to the given search query
     * @param calendarSearchParameters
     * @param sortOrder
     * @param limitOffset
     * @return
     * @throws DataAccessException
     */
    public List<CalendarSupplementalId> getFloorCalendars(CalendarSearchParameters calendarSearchParameters,
                                                                 SortOrder sortOrder, LimitOffset limitOffset)
                                                                                 throws DataAccessException;

    /**
     * Returns a list of active list calendars that conform to the given search query
     * @param calendarSearchParameters
     * @param sortOrder
     * @param limitOffset
     * @return
     * @throws DataAccessException
     */
    public List<CalendarActiveListId> getActiveLists(CalendarSearchParameters calendarSearchParameters,
                                                     SortOrder sortOrder, LimitOffset limitOffset)
                                                                    throws DataAccessException;
}
