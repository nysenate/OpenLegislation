package gov.nysenate.openleg.model.auth;

import org.apache.commons.lang3.RandomStringUtils;
import java.io.Serializable;

/**
 * This class will model an API User.
 */
public class ApiUser implements Serializable, Comparable <ApiUser>
{

    /** Each ApiUser will have their own unique key */
    private String apikey;

    /** The user's email address */
    private String email;

    /** Whether or not the user has confirmed their email address. */
    private boolean authenticated;

    /** Number of Api Requests made by the user */
    private long apiRequests;

    /** Constructor */

    public ApiUser(String email) {
        this.apikey = "";
        this.apiRequests = 0l;
        this.authenticated = false;
        this.email = email;
        generateKey();
    }

    /**
     * Code to handle when an apiuser makes an api request.
     */
    public void logRequest() {
        this.apiRequests++;
    }

    /**
     * This will be called once the ApiUser has authenticated their account.
     * Their key will be emailed to them.
     */
    public void authenticate() {
        this.authenticated = true;
    }

    /**
     * This method will generate a new 32 character long key for the user.
     */
    public void generateKey() {
        this.apikey = RandomStringUtils.randomAlphanumeric(32);
    }


    /** To be completed */
    public int compareTo (ApiUser other)
    {
        return -1;
    }

    /** Getters and Setters */
    public String getEmail() { return this.email; }
    public void setEmail (String newadr) { this.email = newadr; }

    public String getApikey() { return this.apikey; }
    public void setApiKey(String key) { this.apikey = key; }

    public boolean getAuthStatus() { return this.authenticated; }
    public void setAuthStatus(boolean auth) { this.authenticated = auth; }

    public Long getNumRequests() { return this.apiRequests; }
    public void setNumRequests(long number) { this.apiRequests = number; }


}
