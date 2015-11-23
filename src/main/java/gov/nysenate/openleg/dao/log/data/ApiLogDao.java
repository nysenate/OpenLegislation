package gov.nysenate.openleg.dao.log.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiLogDao
{
    /**
     * Retrieve a list of api responses. This includes the api requests.
     *
     * @param limOff LimitOffset - limit results
     * @param order SortOrder - sort by request date time
     * @return List<ApiResponse>
     */
    List<ApiResponse> getResponses(LimitOffset limOff, SortOrder order);

    /**
     * Retrieve a list of api responses during a given request date time range.
     * This includes the api requests.
     *
     * @param dateTimeRange Range<LocalDateTime> - request date time range
     * @param limOff LimitOffset - limit results
     * @param order SortOrder - sort by request date time
     * @return List<ApiResponse>
     */
    List<ApiResponse> getResponses(Range<LocalDateTime> dateTimeRange, LimitOffset limOff, SortOrder order);

    /**
     * Save an ApiResponse into the persistence layer. If the response is saved successfully
     * a request id will be set on the passed in ApiResponse.
     *
     * @param res ApiResponse
     * @throws DataAccessException
     */
    void saveApiResponse(ApiResponse res) throws DataAccessException;
}
