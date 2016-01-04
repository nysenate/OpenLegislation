package gov.nysenate.openleg.client.view.log;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;

import java.time.LocalDateTime;

/**
 * View for the ApiResponse
 */
public class ApiLogItemView implements ViewObject
{
    private int requestId;
    private LocalDateTime requestTime;
    private String url;
    private String ipAddress;
    private String requestMethod;
    private String userAgent;
    private String apiKey;
    private String apiUserName;
    private String apiUserEmail;
    private LocalDateTime responseDateTime;
    private int statusCode;
    private String contentType;
    private double processTime;

    /** --- Constructors --- */

    public ApiLogItemView() {}

    public ApiLogItemView(String json) {}

    public ApiLogItemView(ApiResponse apiResponse) {
        if (apiResponse != null) {
            if (apiResponse.getBaseRequest() != null) {
                ApiRequest req = apiResponse.getBaseRequest();
                this.requestId = req.getRequestId();
                this.requestTime = req.getRequestTime();
                this.url = req.getUrl();
                this.ipAddress = (req.getIpAddress() != null) ? req.getIpAddress().getHostAddress() : null;
                this.requestMethod = req.getRequestMethod();
                this.userAgent = req.getUserAgent();
                this.apiKey = req.getApiKey();
                if (req.getApiUser() != null) {
                    this.apiUserEmail = req.getApiUser().getEmail();
                    this.apiUserName = req.getApiUser().getName();
                }
            }
            this.responseDateTime = apiResponse.getResponseDateTime();
            this.statusCode = apiResponse.getStatusCode();
            this.contentType = apiResponse.getContentType();
            this.processTime = apiResponse.getProcessTime();
        }
    }

    @Override
    public String getViewType() {
        return "api-log-item";
    }

    /** --- Basic Getters --- */

    public int getRequestId() {
        return requestId;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public String getUrl() {
        return url;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUserName() {
        return apiUserName;
    }

    public String getApiUserEmail() {
        return apiUserEmail;
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