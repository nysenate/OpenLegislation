package gov.nysenate.openleg.model.auth;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;

public class ApiRequest
{
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
    private Integer requestId;

    /** --- Constructors --- */

    public ApiRequest() {}

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

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ApiUser getApiUser() {
        return apiUser;
    }

    public void setApiUser(ApiUser apiUser) {
        this.apiUser = apiUser;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
