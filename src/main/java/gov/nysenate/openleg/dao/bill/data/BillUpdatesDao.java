package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateDigest;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.model.bill.BillUpdateInfo;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;

import java.time.LocalDateTime;
import java.util.List;

public interface BillUpdatesDao
{
    /**
     * Discovers which bills have been updated and persisted into the database during a specified date/time range.
     *
     * @param dateTimeRange Range<LocalDateTime> - Date range to search for updates within
     * @param filter BillUpdateField - If not null, limit updates to those that affect the specified field.
     * @param dateOrder SortOrder - Order by the update date/time.
     * @param limOff LimitOffset - Restrict the result set
     * @return PaginatedList<UpdateToken<BaseBillId>>
     */
    public PaginatedList<UpdateToken<BaseBillId>> getUpdateTokens(Range<LocalDateTime> dateTimeRange, BillUpdateField filter,
                                                                  SortOrder dateOrder, LimitOffset limOff);

    /**
     * Returns a list of digests which contain all the information pertaining to a bill that have changed during the
     * specified date range.
     *
     * @param billId BaseBillId
     * @param filter BillUpdateField - If not null, limit updates to those that affect the specified field.
     * @param dateTimeRange Range<LocalDateTime>
     * @param dateOrder SortOrder
     * @return List<UpdateDigest<BaseBillId>>
     */
    public List<UpdateDigest<BaseBillId>> getUpdateDigests(BaseBillId billId, Range<LocalDateTime> dateTimeRange,
                                                           BillUpdateField filter, SortOrder dateOrder);
}