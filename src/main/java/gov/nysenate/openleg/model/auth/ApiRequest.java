package gov.nysenate.openleg.model.auth;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

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

    /** The user's apikey, if provided */
    private String apikey;

    /** A unique identifier used to specify each request made */
    private int requestId;

    /** --- Constructors --- */

    public ApiRequest() {

    }

    public ApiRequest (HttpServletRequest request, LocalDateTime requestDateTime) {
        if (request != null) {
            this.apikey = request.getParameter("key");
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

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
