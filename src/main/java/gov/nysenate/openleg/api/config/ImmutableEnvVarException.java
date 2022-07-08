package gov.nysenate.openleg.api.config;

import java.io.Serial;

public class ImmutableEnvVarException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2737492745999746361L;
    private final EnvironmentVariableView var;

    public ImmutableEnvVarException(EnvironmentVariableView var) {
        super("Environment Variable " + var.name() + " is immutable and cannot be modified");
        this.var = var;
    }

    public EnvironmentVariableView getVar() {
        return var;
    }
}
