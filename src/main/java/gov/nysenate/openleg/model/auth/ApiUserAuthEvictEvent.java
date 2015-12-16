package gov.nysenate.openleg.model.auth;

public class ApiUserAuthEvictEvent {

    protected String apiKey;

    public ApiUserAuthEvictEvent(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
