package gov.nysenate.openleg.controller.api.base;

public class InvalidRequestParamEx extends RuntimeException
{
    private static final long serialVersionUID = -4090577171105617540L;

    protected String parameterName;
    protected String parameterType;
    protected String parameterValue;
    protected String parameterConstraint;

    public InvalidRequestParamEx(String parameterValue, String parameterName,
                                 String parameterType, String parameterConstraint) {
        super(String.format("The received value for a request parameter did not satisfy the parameter constraint" +
                " value: %s, paramName: %s, paramType: %s, constraint: %s",
                parameterValue, parameterName, parameterType, parameterConstraint));
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.parameterValue = parameterValue;
        this.parameterConstraint = parameterConstraint;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public String getParameterConstraint() {
        return parameterConstraint;
    }
}