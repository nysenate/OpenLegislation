package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;

import java.time.LocalDateTime;

public interface CalendarUpdatesDao
{
    /**
     * Returns a list of ids for calendars that have been updated within the specified date time range
     *
     * @param updateType UpdateType - Determines whether to query by the time of the update or the time of the reference
     * @param dateTimeRange Range<LocalDateTime> - Date range to search for digests within
     * @param dateOrder SortOrder - Order by the update date/time.
     * @param limitOffset LimitOffset - Restrict the result set   @return PaginatedList<CalendarUpdateToken>
     */
    public PaginatedList<UpdateToken<CalendarId>> getUpdates(
        UpdateType updateType, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder, LimitOffset limitOffset);

    /**
     * Gets a list of calendar update digests that detail the changes made to any calendars.
     *
     * @param updateType UpdateType
     * @param dateTimeRange Range<LocalDateTime>
     * @param dateOrder SortOrder
     * @param limitOffset LimitOffset
     * @return PaginatedList<CalendarUpdateDigest>
     */
    public PaginatedList<UpdateDigest<CalendarId>> getDetailedUpdates(
        UpdateType updateType, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder, LimitOffset limitOffset);

    /**
     * Gets a list of calendar update digests for a given calendar that detail the changes made to that calendar
     * over the given date time range.
     *
     * @param updateType UpdateType
     * @param calendarId CalendarId
     * @param dateTimeRange Range<LocalDateTime>
     * @param dateOrder SortOrder  @return PaginatedList<CalendarUpdateDigest>
     * @param limitOffset
     */
    public PaginatedList<UpdateDigest<CalendarId>> getDetailedUpdatesForCalendar(
        UpdateType updateType, CalendarId calendarId, Range<LocalDateTime> dateTimeRange,
        SortOrder dateOrder, LimitOffset limitOffset);

}