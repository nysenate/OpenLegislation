package gov.nysenate.openleg.model.auth;

import org.apache.shiro.authc.HostAuthenticationToken;

public class ApiKeyLoginToken implements HostAuthenticationToken
{
    private static final long serialVersionUID = 5205740787431133249L;

    private String apiKey;
    private String host;

    public ApiKeyLoginToken(String apiKey, String host) {
        this.apiKey = apiKey;
        this.host = host;
    }

    public void clear() {
        this.apiKey = null;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Object getPrincipal() {
        return this.apiKey;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
