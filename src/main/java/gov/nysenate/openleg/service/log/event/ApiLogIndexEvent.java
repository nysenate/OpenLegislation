package gov.nysenate.openleg.service.log.event;

import gov.nysenate.openleg.model.auth.ApiResponse;

/**
 * A simple event that is intercepted by the log search indexing service.
 */
public class ApiLogIndexEvent
{
    protected ApiResponse apiResponse;

    /** --- Constructors --- */

    public ApiLogIndexEvent(ApiResponse apiResponse) {
        if (apiResponse == null || apiResponse.getBaseRequest() == null || apiResponse.getBaseRequest().getRequestId() == null) {
            throw new IllegalArgumentException("The api response passed in the constructor is not valid.");
        }
        this.apiResponse = apiResponse;
    }

    /** --- Basic Getters --- */

    public ApiResponse getApiResponse() {
        return apiResponse;
    }
}
