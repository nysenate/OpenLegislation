package gov.nysenate.openleg.search.logs;


import gov.nysenate.openleg.auth.model.ApiUser;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

public class ApiRequest {
    /** The time at which an ApiRequest is made */
    private LocalDateTime requestTime;

    /** The requested URL */
    private String url;

    /** The IP Address that made the request */
    private InetAddress ipAddress;

    /** The request method used */
    private String requestMethod;

    /** THe User Agent that made this request */
    private String userAgent;

    /** The user's api key, if provided */
    private String apiKey;

    /** Reference to the ApiUser if applicable */
    private ApiUser apiUser;

    /** A unique identifier used to specify each request made */
    private long requestId;

    /** --- Constructors --- */

    public ApiRequest (HttpServletRequest request, LocalDateTime requestDateTime) {
        if (request != null) {
            this.apiKey = request.getParameter("key");
            this.userAgent = request.getHeader("User-Agent");
            try {
                this.ipAddress = InetAddress.getByName(request.getRemoteAddr());
            } catch (UnknownHostException e) {
                // Ignore
            }
            this.requestMethod = request.getMethod();
            this.url = request.getRequestURI() + ((request.getQueryString() != null) ? ("?" + request.getQueryString()) : "");
            this.requestTime = requestDateTime;
        }
    }

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public String getUrl() {
        return url;
    }

    public InetAddress getIpAddress() {
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

    public ApiUser getApiUser() {
        return apiUser;
    }

    public void setApiUser(ApiUser apiUser) {
        this.apiUser = apiUser;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long id) {
        this.requestId = id;
    }
}
