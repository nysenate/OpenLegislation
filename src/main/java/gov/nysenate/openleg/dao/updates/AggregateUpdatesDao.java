package gov.nysenate.openleg.dao.updates;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.updates.UpdateContentType;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface AggregateUpdatesDao {

    /**
     * Gets an aggregation of update tokens that report the last update for all content items of the
     *  requested types, that fall within the given time range.  Updates can be retrieved by published or processed time.
     *
     * @param dateTimeRange Range<LocalDateTime> - restrict the date time range of returned update tokens
     * @param types Set<UpdateContentType> - return updates for the content types specified in this set
     * @param updateType UpdateType - specifies whether updates are retrieved based on published or processed date time
     * @param order SortOrder - determines update sort order based on the update type
     * @param limitOffset LimitOffset - Limit the response
     * @return PaginatedList<UpdateToken<Properties>>
     */
    public PaginatedList<UpdateToken<Map<String, String>>> getUpdateTokens(Range<LocalDateTime> dateTimeRange,
                                                                           Set<UpdateContentType> types, UpdateType updateType,
                                                                           SortOrder order, LimitOffset limitOffset);

    /**
     * Gets an aggregation of update digests that report all updates for each content item of the
     *  requested types, that fall within the given time range.  If detailed digests are requested, the updated data
     *  is displayed. Updates can be retrieved by published or processed time.
     *
     * @param dateTimeRange Range<LocalDateTime> - restrict the date time range of returned update digests
     * @param types Set<UpdateContentType> - return updates for the content types specified in this set
     * @param updateType UpdateType - specifies whether updates are retrieved based on published or processed date time
     * @param order SortOrder - determines update sort order based on the update type
     * @param limitOffset LimitOffset - Limit the response
     * @param detail boolean - will return detailed update digests if set to true
     * @return PaginatedList<UpdateToken<Properties>>
     */
    public PaginatedList<UpdateDigest<Map<String, String>>> getUpdateDigests(Range<LocalDateTime> dateTimeRange,
                                                                             Set<UpdateContentType> types, UpdateType updateType,
                                                                             SortOrder order, LimitOffset limitOffset,
                                                                             boolean detail);

    /**
     * An override of getUpdateDigests that does not return detailed digests
     * @see #getUpdateDigests
     */
    public default PaginatedList<UpdateDigest<Map<String, String>>> getUpdateDigests(Range<LocalDateTime> dateTimeRange,
                                                                                     Set<UpdateContentType> types, UpdateType updateType,
                                                                                     SortOrder order, LimitOffset limitOffset) {
        return getUpdateDigests(dateTimeRange, types, updateType, order, limitOffset, false);
    }
}
