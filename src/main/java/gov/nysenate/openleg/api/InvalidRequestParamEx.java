package gov.nysenate.openleg.api;

import java.io.Serial;

public class InvalidRequestParamEx extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4090577171105617540L;

    protected String parameterName;
    protected String parameterType;
    protected String parameterValue;
    protected String parameterConstraint;
    protected String responseMessage;

    public InvalidRequestParamEx(String parameterValue, String parameterName,
                                 String parameterType, String parameterConstraint) {
        this(parameterValue, parameterName, parameterType, parameterConstraint, null);
    }

    /**
     * @param responseMessage String - replaces the generic error code text in the response message,
     *                        or null to keep it
     */
    public InvalidRequestParamEx(String parameterValue, String parameterName, String parameterType,
                                 String parameterConstraint, String responseMessage) {
        super(String.format("The received value for a request parameter did not satisfy the parameter constraint" +
                " value: %s, paramName: %s, paramType: %s, constraint: %s",
                parameterValue, parameterName, parameterType, parameterConstraint));
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.parameterValue = parameterValue;
        this.parameterConstraint = parameterConstraint;
        this.responseMessage = responseMessage;
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

    /**
     * @return String - the response message, or null to use the generic error code text
     */
    public String getResponseMessage() {
        return responseMessage;
    }
}
