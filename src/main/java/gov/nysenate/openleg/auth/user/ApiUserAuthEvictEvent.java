package gov.nysenate.openleg.auth.user;

public class ApiUserAuthEvictEvent {

    protected String apiKey;

    public ApiUserAuthEvictEvent(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
