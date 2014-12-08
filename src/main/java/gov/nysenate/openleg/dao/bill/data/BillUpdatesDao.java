package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateDigest;
import gov.nysenate.openleg.model.bill.BillUpdateToken;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.List;

public interface BillUpdatesDao
{
    /**
     * Discovers which bills have been updated and persisted into the database during a specified date/time range.
     *
     * @param dateTimeRange Range<LocalDateTime> - Date range to search for updates within
     * @param dateOrder SortOrder - Order by the update date/time.
     * @param limOff LimitOffset - Restrict the result set
     * @return List<BillUpdateDigest>
     */
    public PaginatedList<BillUpdateToken> billsUpdatedDuring(Range<LocalDateTime> dateTimeRange, SortOrder dateOrder,
                                                                   LimitOffset limOff);

    /**
     * Returns a list of digests which contain all the information pertaining to a bill that have changed during the
     * specified date range.
     *
     * @param billId BaseBillId
     * @param dateTimeRange Range<LocalDateTime>
     * @param dateOrder SortOrder
     * @return List<BillUpdateDigest>
     */
    public List<BillUpdateDigest> getUpdateDigests(BaseBillId billId, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder);
}