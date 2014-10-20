package gov.nysenate.openleg.service.calendar.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.InvalidParametersSearchException;

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
     * @param sortOrder
     * @return List<CalendarId> A list of calendar ids that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     */
    public List<CalendarId> searchForCalendars(CalendarSearchParameters searchParams, SortOrder sortOrder, LimitOffset limitOffset)
            throws InvalidParametersSearchException;

    /**
     * @param searchParams
     * @param sortOrder
     * @return List<CalendarActiveListId> a list of active list calendar ids that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     */
    public List<CalendarActiveListId> searchForActiveLists(CalendarSearchParameters searchParams, SortOrder sortOrder, LimitOffset limitOffset)
            throws InvalidParametersSearchException;

    /**
     * @param searchParams
     * @param sortOrder
     * @return List<CalendarSupplementalId> a list of supplemental calendar ids that match the given search parameters
     * @throws gov.nysenate.openleg.service.base.InvalidParametersSearchException When the given search parameters are invalid
     */
    public List<CalendarSupplementalId> searchForFloorCalendars(CalendarSearchParameters searchParams, SortOrder sortOrder, LimitOffset limitOffset)
            throws InvalidParametersSearchException;
}
