package gov.nysenate.openleg.api;

import java.io.Serial;

/**
 * Thrown when a request uses a retired option. Unlike other invalid parameters, the response
 * message carries migration guidance instead of the generic error code text.
 */
public class RetiredRequestParamEx extends InvalidRequestParamEx {
    @Serial
    private static final long serialVersionUID = 6283318540931657473L;

    public RetiredRequestParamEx(String parameterValue, String parameterName, String parameterType,
                                 String parameterConstraint, String guidance) {
        super(parameterValue, parameterName, parameterType, parameterConstraint, guidance);
    }
}
