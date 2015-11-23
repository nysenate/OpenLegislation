package gov.nysenate.openleg.service.log.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.service.log.event.ApiLogEvent;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.auth.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiLogDataService
{
    /**
     * Retrieve persisted log data.
     *
     * @param dateTimeRange Range<LocalDateTime> dateTimeRange
     * @param limOff LimitOffset
     * @param order SortOrder
     * @return List<ApiResponse>
     */
    List<ApiResponse> getResponses(Range<LocalDateTime> dateTimeRange, LimitOffset limOff, SortOrder order);

    /**
     * Persist ApiLogEvent into the data store asynchronously.
     *
     * @param apiLogEvent ApiLogEvent
     * @param emitSearchEvent boolean - set to true if a ApiLogIndexEvent should fire upon persisting.
     */
    void saveApiResponseAsync(ApiLogEvent apiLogEvent, boolean emitSearchEvent);
}
