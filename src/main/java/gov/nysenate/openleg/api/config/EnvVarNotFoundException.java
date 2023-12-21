package gov.nysenate.openleg.api.config;

import java.io.Serial;

public class EnvVarNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4046771266840167802L;
    private final String varName;

    public EnvVarNotFoundException(String varName) {
        super("Environment variable " + varName + " does not exist");
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }
}
