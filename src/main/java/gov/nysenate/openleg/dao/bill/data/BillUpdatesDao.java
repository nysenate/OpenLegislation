package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;

import java.time.LocalDateTime;

public interface BillUpdatesDao
{
    /**
     * Discovers which bills have been updated and persisted into the database during a specified date/time range.
     *
     * @param dateTimeRange Range<LocalDateTime> - Date range to search for updates within
     * @param type UpdateType - The type of updates (based on sobi published date or when data was processed)
     * @param filter BillUpdateField - If not null, limit updates to those that affect the specified field.
     * @param dateOrder SortOrder - Order by the update date/time.
     * @param limOff LimitOffset - Restrict the result set
     * @return PaginatedList<UpdateToken<BaseBillId>>
     */
    public PaginatedList<UpdateToken<BaseBillId>> getUpdates(
        Range<LocalDateTime> dateTimeRange, UpdateType type, BillUpdateField filter, SortOrder dateOrder, LimitOffset limOff);

    /**
     * Retrieves update digests during a given date range with an optional filter.
     * @see #getUpdates for param details.
     */
    public PaginatedList<UpdateDigest<BaseBillId>> getDetailedUpdates(
        Range<LocalDateTime> dateTimeRange, UpdateType type, BillUpdateField filter, SortOrder dateOrder, LimitOffset limOff);

    /**
     * Returns a list of digests which contain all the information pertaining to a bill that have changed during the
     * specified date range.
     *
     * @param billId BaseBillId - The bill id to get updates for.
     * @see #getUpdates for other param details.
     * @return PaginatedList<UpdateDigest<BaseBillId>>
     */
    public PaginatedList<UpdateDigest<BaseBillId>> getDetailedUpdatesForBill(
        BaseBillId billId, Range<LocalDateTime> dateTimeRange, UpdateType type, BillUpdateField filter, SortOrder dateOrder,
        LimitOffset limOff);
}