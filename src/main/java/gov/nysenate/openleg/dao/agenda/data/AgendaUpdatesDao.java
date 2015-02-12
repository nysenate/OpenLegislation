package gov.nysenate.openleg.dao.agenda.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;

import java.time.LocalDateTime;

public interface AgendaUpdatesDao
{
    /**
     * Discovers which agendas have been updated and persisted into the database during a specified date/time range.
     *
     * @param dateTimeRange Range<LocalDateTime> - Date range to search for updates within
     * @param type UpdateType - The type of updates (based on sobi published date or when data was processed)
     * @param dateOrder SortOrder - Order by the update date/time.
     * @param limOff LimitOffset - Restrict the result set
     * @return PaginatedList<UpdateToken<AgendaId>>
     */
    public PaginatedList<UpdateToken<AgendaId>> getUpdates(
            Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limOff);

    /**
     * Retrieves update digests during a given date range with an optional filter.
     * @see #getUpdates for param details.
     */
    public PaginatedList<UpdateDigest<AgendaId>> getDetailedUpdates(
            Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limOff);

    /**
     * Returns a list of digests which contain all the information pertaining to an agenda that have changed during the
     * specified date range.
     *
     * @param agendaId AgendaId - The agenda id to get updates for.
     * @see #getUpdates for other param details.
     * @return PaginatedList<UpdateDigest<AgendaId>>
     */
    public PaginatedList<UpdateDigest<AgendaId>> getDetailedUpdatesForAgenda(
        AgendaId agendaId, Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limOff);
}
