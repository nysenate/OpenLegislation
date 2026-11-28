package gov.nysenate.openleg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentUtils {
    private final Environment env;

    @Autowired
    public EnvironmentUtils(Environment environment) {
        this.env = environment;
    }

    public boolean isTest() {
        return env.acceptsProfiles(Profiles.of("test"));
    }
}
