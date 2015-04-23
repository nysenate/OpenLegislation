package gov.nysenate.openleg.model.auth;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class ApiRequest
{
    /** The time at which an ApiRequest is made */
    private String requestTime;

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
    private int request_id;

    /**
     * Constructor
     */
    public ApiRequest () {

    }

    /** Getters and Setters */
    public String getRequestTime() { return requestTime; }
    public void setRequestTime(String requestTime) { this.requestTime = requestTime; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public InetAddress getIpAddress() { return ipAddress; }
    public void setIpAddress(InetAddress ipAddress) { this.ipAddress = ipAddress; }

    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getApikey() { return apikey; }
    public void setApikey(String apikey) { this.apikey = apikey; }

    public int getRequest_id() { return request_id; }
    public void setRequest_id(int request_id) { this.request_id = request_id; }
}
