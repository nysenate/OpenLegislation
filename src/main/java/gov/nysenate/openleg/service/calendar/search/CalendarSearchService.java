package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.InvalidParametersSearchException;
import gov.nysenate.openleg.service.base.NoResultsSearchException;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;

import java.util.List;

public interface CalendarSearchService {

    /**
     * @param searchParams
     * @return The number of results that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     */
    public int getCalenderSearchResultCount(CalendarSearchParameters searchParams) throws InvalidParametersSearchException;

    /**
     * @param searchParams
     * @return List<CalendarId> A list of calendar ids that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     * @throws CalendarNotFoundEx When no calendars were retrieved from the search
     */
    public List<CalendarId> searchForCalendars(CalendarSearchParameters searchParams, OrderBy orderBy, LimitOffset limitOffset)
            throws InvalidParametersSearchException, NoResultsSearchException;

    /**
     * @param searchParams
     * @return List<CalendarActiveListId> a list of active list calendar ids that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     * @throws CalendarNotFoundEx When no calendars were retrieved from the search
     */
    public List<CalendarActiveListId> searchForActiveLists(CalendarSearchParameters searchParams, OrderBy orderBy, LimitOffset limitOffset)
            throws InvalidParametersSearchException, NoResultsSearchException;

    /**
     * @param searchParams
     * @return List<CalendarSupplementalId> a list of supplemental calendar ids that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     * @throws CalendarNotFoundEx When no calendars were retrieved from the search
     */
    public List<CalendarSupplementalId> searchForFloorCalendars(CalendarSearchParameters searchParams, OrderBy orderBy, LimitOffset limitOffset)
            throws InvalidParametersSearchException, NoResultsSearchException;
}
