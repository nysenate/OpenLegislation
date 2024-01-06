package gov.nysenate.openleg.api.logs;

import gov.nysenate.openleg.search.logs.ApiRequest;
import gov.nysenate.openleg.search.logs.ApiResponse;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

public class ApiLogEvent {
    private final LocalDateTime logDateTime;
    private final ApiResponse apiResponse;

    /** --- Constructors --- */

    public ApiLogEvent(ServletRequest servletRequest, ServletResponse servletResponse, LocalDateTime requestStart,
                       LocalDateTime requestEnd) {

        ApiRequest apiRequest = new ApiRequest((HttpServletRequest) servletRequest, requestStart);
        this.apiResponse = new ApiResponse(apiRequest, (HttpServletResponse) servletResponse, requestEnd);
        this.logDateTime = LocalDateTime.now();
    }

    /** --- Functional Getters --- */

    public LocalDateTime getRequestTime() {
        if (apiResponse != null && apiResponse.getBaseRequest() != null) {
            return apiResponse.getBaseRequest().getRequestTime();
        }
        return null;
    }

    /** --- Basic Getters --- */

    public LocalDateTime getLogDateTime() {
        return logDateTime;
    }

    public ApiResponse getApiResponse() {
        return apiResponse;
    }
}
