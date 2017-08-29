package gov.nysenate.openleg.dao.law.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawVersionId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;

import java.time.LocalDateTime;

public interface LawUpdatesDao
{
    /**
     * Returns a list of law id tokens that have been updated during the given date range.
     *
     * @param dateTimeRange Range<LocalDateTime> - The date range to search for updates within.
     * @param type UpdateType - The type of updates (based on law file published date or when data was processed)
     * @param dateOrder SortOrder - Order by the update date/time.
     * @param limitOffset LimitOffset - Restrict the result set
     * @return PaginatedList<UpdateToken<LawVersionId>>
     */
    PaginatedList<UpdateToken<LawVersionId>> getUpdates(
            Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset);

    /**
     * Returns a list of law doc id digests that have been updated during the given date range.
     * @see #getUpdates for param details.
     * @return PaginatedList<UpdateDigest<LawDocId>>
     */
    PaginatedList<UpdateDigest<LawDocId>> getDetailedUpdates(
            Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset);

    /**
     * Returns a list of update digests for law documents that have been updated for a given law.
     *
     * @param lawId String - Three letter law id to get updates for
     * @see #getUpdates for the other params.
     * @return PaginatedList<UpdateDigest<LawDocId>>
     */
    PaginatedList<UpdateDigest<LawDocId>> getDetailedUpdatesForLaw(
            String lawId, Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset);

    /**
     * Returns a list of law doc id digests for a given law document. This is basically a history of updates for
     * a particular section of law.
     *
     * @param documentId String - The document id to get updates for
     * @see #getUpdates for other param details
     * @return PaginatedList<UpdateDigest<LawDocId>>
     */
    PaginatedList<UpdateDigest<LawDocId>> getDetailedUpdatesForDocument(
            String documentId, Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset);

    /**
     * Get a list of law ids that have had law tree updates in the given date time range
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @param type {@link UpdateType}
     * @param dateOrder {@link SortOrder}
     * @param limitOffset {@link LimitOffset}
     * @return {@link PaginatedList<UpdateToken<LawVersionId>>} list of law ids that have had tree updates
     */
    PaginatedList<UpdateToken<LawVersionId>> getLawTreeUpdates(Range<LocalDateTime> dateTimeRange, UpdateType type,
                                                               SortOrder dateOrder, LimitOffset limitOffset);
}