package gov.nysenate.openleg.auth.model;

import org.apache.shiro.authc.HostAuthenticationToken;

import java.io.Serial;

public class ApiKeyLoginToken implements HostAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 5205740787431133249L;

    private final String apiKey;
    private final String host;

    public ApiKeyLoginToken(String apiKey, String host) {
        this.apiKey = apiKey;
        this.host = host;
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
