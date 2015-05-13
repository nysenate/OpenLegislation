package gov.nysenate.openleg.client.view.environment;

public class ImmutableEnvVarException extends RuntimeException {

    private static final long serialVersionUID = -2737492745999746361L;
    private String varName;

    public ImmutableEnvVarException(String varName) {
        super("Environment Variable " + varName + " is immutable and cannot be modified");
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }
}
