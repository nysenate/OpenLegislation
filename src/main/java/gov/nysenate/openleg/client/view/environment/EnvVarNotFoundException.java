package gov.nysenate.openleg.client.view.environment;

public class EnvVarNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4046771266840167802L;
    private String varName;

    public EnvVarNotFoundException(String varName) {
        super("Environment variable " + varName + " does not exist");
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }
}
