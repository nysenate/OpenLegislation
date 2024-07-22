package gov.nysenate.openleg.search.logs;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ApiResponse {
    /** Reference to the originating API request */
    private final ApiRequest baseRequest;

    /** When the response was returned */
    private final LocalDateTime responseDateTime;

    /** HTTP status code of the response. */
    private final int statusCode;

    /** Content type of response */
    private final String contentType;

    /** Time it took to process the request. */
    private final double processTime;

    public ApiResponse(ApiRequest baseRequest, HttpServletResponse response, LocalDateTime responseDateTime) {
        this.baseRequest = baseRequest;
        this.responseDateTime = responseDateTime;
        this.processTime = ChronoUnit.MILLIS.between(baseRequest.getRequestTime(), responseDateTime);
        this.statusCode = response.getStatus();
        this.contentType = response.getContentType();
    }

    public ApiRequest getBaseRequest() {
        return baseRequest;
    }

    public LocalDateTime getResponseDateTime() {
        return responseDateTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public double getProcessTime() {
        return processTime;
    }

}